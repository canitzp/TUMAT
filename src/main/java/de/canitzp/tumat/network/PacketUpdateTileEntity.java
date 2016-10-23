package de.canitzp.tumat.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

/**
 * @author canitzp
 */
public class PacketUpdateTileEntity implements IMessage, IMessageHandler<PacketUpdateTileEntity, IMessage>{

    private BlockPos pos;
    private String[] nbtKeys;

    public PacketUpdateTileEntity(){
    }

    public PacketUpdateTileEntity(BlockPos pos, String... nbtKeys){
        this.pos = pos;
        this.nbtKeys = nbtKeys;
    }

    @Override
    public void fromBytes(ByteBuf buf){
        PacketBuffer buffer = new PacketBuffer(buf);
        this.pos = buffer.readBlockPos();
        int size = buffer.readInt();
        String[] nbtKeys = new String[size];
        for(int i = 0; i < size; i++){
            nbtKeys[i] = buffer.readStringFromBuffer(Short.MAX_VALUE);
        }
        this.nbtKeys = nbtKeys;
    }

    @Override
    public IMessage onMessage(PacketUpdateTileEntity message, MessageContext ctx){
        if(message.pos != null){
            World world = ctx.getServerHandler().playerEntity.getEntityWorld();
            TileEntity tileEntity = world.getTileEntity(message.pos);
            if(tileEntity != null){
                NBTTagCompound oldTileEntityNBT = tileEntity.writeToNBT(new NBTTagCompound());
                NBTTagCompound sendNBT = new NBTTagCompound();
                for(String key : message.nbtKeys){
                    if(oldTileEntityNBT.hasKey(key)){
                        sendNBT.setTag(key, oldTileEntityNBT.getTag(key));
                    }
                }
                return new PacketTileEntityToClient(message.pos, sendNBT);
            }
        }
        return null;
    }    @Override
    public void toBytes(ByteBuf buf){
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.nbtKeys.length);
        for(String s : this.nbtKeys){
            buffer.writeString(s);
        }
    }

    public static class PacketTileEntityToClient implements IMessage, IMessageHandler<PacketTileEntityToClient, IMessage>{

        private BlockPos pos;
        private NBTTagCompound nbt;

        public PacketTileEntityToClient(){
        }

        public PacketTileEntityToClient(BlockPos pos, NBTTagCompound nbt){
            this.pos = pos;
            this.nbt = nbt;
        }

        @Override
        public void fromBytes(ByteBuf buf){
            PacketBuffer buffer = new PacketBuffer(buf);
            this.pos = buffer.readBlockPos();
            try{
                this.nbt = buffer.readNBTTagCompoundFromBuffer();
            } catch(IOException e){
                e.printStackTrace();
            }
        }

        @Override
        public void toBytes(ByteBuf buf){
            PacketBuffer buffer = new PacketBuffer(buf);
            buffer.writeBlockPos(this.pos);
            buffer.writeNBTTagCompoundToBuffer(this.nbt);
        }

        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(PacketTileEntityToClient message, MessageContext ctx){
            Minecraft.getMinecraft().addScheduledTask(new Runnable(){
                @SideOnly(Side.CLIENT)
                @Override
                public void run(){
                    World world = Minecraft.getMinecraft().theWorld;
                    TileEntity tileEntity = world.getTileEntity(message.pos);
                    if(tileEntity != null){
                        NBTTagCompound oldNBT = tileEntity.writeToNBT(new NBTTagCompound());
                        oldNBT.merge(message.nbt);
                        tileEntity.readFromNBT(oldNBT);
                    }
                }
            });
            return null;
        }
    }


}
