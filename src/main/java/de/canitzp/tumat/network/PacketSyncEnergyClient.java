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

/**
 * @author canitzp
 */
public class PacketSyncEnergyClient implements IMessage, IMessageHandler<PacketSyncEnergyClient, IMessage>{

    private BlockPos pos;
    private int energy;
    private EnumFacing side;

    public PacketSyncEnergyClient(){}

    public PacketSyncEnergyClient(BlockPos pos, EnumFacing side, int energy){
        this.pos = pos;
        this.side = side;
        this.energy = energy;
    }

    @Override
    public void fromBytes(ByteBuf buf){
        PacketBuffer buffer = new PacketBuffer(buf);
        this.pos = buffer.readBlockPos();
        this.energy = buffer.readInt();
        this.side = EnumFacing.values()[buffer.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf){
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.energy);
        buffer.writeInt(this.side.ordinal());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(PacketSyncEnergyClient message, MessageContext ctx){
        Minecraft.getMinecraft().addScheduledTask(new Runnable(){
            @SideOnly(Side.CLIENT)
            @Override
            public void run(){
                SyncUtil.energyMap.put(new SyncUtil.SidedPosition(message.pos, message.side), message.energy);
            }
        });
        return null;
    }
}