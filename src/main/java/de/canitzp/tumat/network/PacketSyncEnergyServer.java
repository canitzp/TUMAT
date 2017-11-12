package de.canitzp.tumat.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author canitzp
 */
public class PacketSyncEnergyServer implements IMessage, IMessageHandler<PacketSyncEnergyServer, IMessage>{

    private BlockPos pos;
    private EnumFacing side;

    public PacketSyncEnergyServer(){}

    public PacketSyncEnergyServer(BlockPos pos, EnumFacing side){
        this.pos = pos;
        this.side = side;
    }

    @Override
    public void fromBytes(ByteBuf buf){
        this.pos = BlockPos.fromLong(buf.readLong());
        this.side = EnumFacing.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf){
        buf.writeLong(this.pos.toLong());
        buf.writeInt(this.side.ordinal());
    }

    @Override
    public IMessage onMessage(PacketSyncEnergyServer message, MessageContext ctx){
        TileEntity tile = ctx.getServerHandler().player.getEntityWorld().getTileEntity(message.pos);
        if(tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, message.side)){
            IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, message.side);
            if(storage != null){
                return new PacketSyncEnergyClient(message.pos, message.side, storage.getEnergyStored(), storage.getMaxEnergyStored());
            }
        }
        return null;
    }
}
