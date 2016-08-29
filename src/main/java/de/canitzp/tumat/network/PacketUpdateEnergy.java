package de.canitzp.tumat.network;

import cofh.api.energy.IEnergyHandler;
import de.canitzp.tumat.integration.Tesla;
import io.netty.buffer.ByteBuf;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.darkhax.tesla.lib.TeslaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModAPIManager;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

/**
 * @author canitzp
 */
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
    public void toBytes(ByteBuf buf){
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeBlockPos(this.tilePos);
        buffer.writeInt(this.side.ordinal());
    }

    @Override
    public IMessage onMessage(PacketUpdateEnergy message, MessageContext ctx){
        World world = ctx.getServerHandler().playerEntity.worldObj;
        TileEntity tile = world.getTileEntity(message.tilePos);
        if(tile != null){
            if(Loader.isModLoaded("tesla")){
                if(TeslaUtils.isTeslaHolder(tile, message.side)){
                    return new PacketUpdateEnergyClient(message.tilePos, message.side, TeslaUtils.getStoredPower(tile, side));
                }
            } else if(ModAPIManager.INSTANCE.hasAPI("CoFHAPI|energy")){
                if(tile instanceof IEnergyHandler){
                    NBTTagCompound nbt = new NBTTagCompound();
                    tile.readFromNBT(nbt);
                    return new PacketUpdateEnergyClient(message.tilePos, message.side, nbt.getInteger("Energy"));
                }
            }
        }
        return null;
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
            World world = Minecraft.getMinecraft().theWorld;
            TileEntity tile = world.getTileEntity(message.tilePos);
            if(tile != null){
                if(Loader.isModLoaded("tesla")){
                    TeslaUtils.givePower(tile, message.side, message.energy, false);
                } else if(ModAPIManager.INSTANCE.hasAPI("CoFHAPI|energy")){
                    NBTTagCompound old = new NBTTagCompound();
                    tile.readFromNBT(old);
                    old.setInteger("Energy", (int) message.energy);
                    tile.writeToNBT(old);
                }
            }
            return null;
        }
    }
}
