package de.canitzp.tumat.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

/**
 * @author canitzp
 */
public class PacketSyncEnergyClient implements IMessage, IMessageHandler<PacketSyncEnergyClient, IMessage>{

    private BlockPos pos;
    private int currentEnergy, capEnergy;
    private EnumFacing side;

    public PacketSyncEnergyClient(){}

    public PacketSyncEnergyClient(BlockPos pos, EnumFacing side, int currentEnergy, int capEnergy){
        this.pos = pos;
        this.side = side;
        this.currentEnergy = currentEnergy;
        this.capEnergy = capEnergy;
    }

    @Override
    public void fromBytes(ByteBuf buf){
        PacketBuffer buffer = new PacketBuffer(buf);
        this.pos = buffer.readBlockPos();
        this.currentEnergy = buffer.readInt();
        this.capEnergy = buffer.readInt();
        this.side = EnumFacing.values()[buffer.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf){
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.currentEnergy);
        buffer.writeInt(this.capEnergy);
        buffer.writeInt(this.side.ordinal());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(PacketSyncEnergyClient message, MessageContext ctx){
        Minecraft.getMinecraft().addScheduledTask(new Runnable(){
            @SideOnly(Side.CLIENT)
            @Override
            public void run(){
                SyncUtil.SidedPosition pos = new SyncUtil.SidedPosition(message.pos, message.side);
                for(Map.Entry<SyncUtil.SidedPosition, Pair<Integer, Integer>> entry : SyncUtil.energyMap.entrySet()){
                    if(entry.getKey().equals(pos)){
                        entry.setValue(Pair.of(message.currentEnergy, message.capEnergy));
                        return;
                    }
                }
                SyncUtil.energyMap.put(pos, Pair.of(message.currentEnergy, message.capEnergy));
            }
        });
        return null;
    }
}