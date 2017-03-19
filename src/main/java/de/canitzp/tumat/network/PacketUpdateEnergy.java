package de.canitzp.tumat.network;

import de.canitzp.tumat.TUMAT;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author canitzp
 */
/*
public class PacketUpdateEnergy implements IMessage, IMessageHandler<PacketUpdateEnergy, IMessage>{

    BlockPos tilePos;
    EnumFacing side;

    public PacketUpdateEnergy(){
    }

    public PacketUpdateEnergy(BlockPos tilePos, EnumFacing side){
        this.tilePos = tilePos;
        this.side = side;
    }

    @Override
    public void fromBytes(ByteBuf buf){
        PacketBuffer buffer = new PacketBuffer(buf);
        this.tilePos = buffer.readBlockPos();
        this.side = EnumFacing.values()[buffer.readInt()];
    }

    @Override
    public IMessage onMessage(PacketUpdateEnergy message, MessageContext ctx){
        World world = ctx.getServerHandler().player.getEntityWorld();
        TileEntity tile = world.getTileEntity(message.tilePos);
        if(tile != null && message.side != null) {
            try {
                if (TeslaUtils.isTeslaHolder(tile, message.side)) {
                    return new PacketUpdateEnergyClient(message.tilePos, message.side, TeslaUtils.getStoredPower(tile, message.side));
                }
            } catch (NullPointerException e) {
                if (world.getTotalWorldTime() % 100 == 0) {
                    TUMAT.logger.error("An error occurred while requesting the energy", e);
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public void toBytes(ByteBuf buf){
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeBlockPos(this.tilePos);
        buffer.writeInt(this.side.ordinal());
    }

    public static class PacketUpdateEnergyClient implements IMessage, IMessageHandler<PacketUpdateEnergyClient, IMessage>{

        BlockPos tilePos;
        EnumFacing side;
        long energy;

        public PacketUpdateEnergyClient(){
        }

        public PacketUpdateEnergyClient(BlockPos pos, EnumFacing side, long energy){
            this.tilePos = pos;
            this.side = side;
            this.energy = energy;
        }

        @Override
        public void fromBytes(ByteBuf buf){
            PacketBuffer buffer = new PacketBuffer(buf);
            this.tilePos = buffer.readBlockPos();
            this.side = EnumFacing.values()[buffer.readInt()];
            this.energy = buffer.readLong();
        }

        @Override
        public void toBytes(ByteBuf buf){
            PacketBuffer buffer = new PacketBuffer(buf);
            buffer.writeBlockPos(this.tilePos);
            buffer.writeInt(this.side.ordinal());
            buffer.writeLong(this.energy);
        }

        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(PacketUpdateEnergyClient message, MessageContext ctx){
            Minecraft.getMinecraft().addScheduledTask(new Runnable(){
                @SideOnly(Side.CLIENT)
                @Override
                public void run(){
                    World world = Minecraft.getMinecraft().world;
                    TileEntity tile = world.getTileEntity(message.tilePos);
                    if(tile != null) {
                        long toStore = message.energy - TeslaUtils.getStoredPower(tile, message.side);
                        if (toStore >= 0) {
                            TeslaUtils.givePower(tile, message.side, toStore, false);
                        } else {
                            TeslaUtils.takePower(tile, message.side, toStore, false);
                        }
                    }
                }
            });
            return null;
        }
    }

}
*/