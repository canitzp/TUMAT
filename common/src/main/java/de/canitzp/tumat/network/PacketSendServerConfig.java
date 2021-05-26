package de.canitzp.tumat.network;

import de.canitzp.tumat.configuration.ConfigHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author canitzp
 */
public class PacketSendServerConfig implements IMessage, IMessageHandler<PacketSendServerConfig, IMessage>{

    private NBTTagCompound values;

    public PacketSendServerConfig(){
        this.values = ConfigHandler.writeToNBT();
    }

    @Override
    public void fromBytes(ByteBuf buf){
        this.values = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf){
        ByteBufUtils.writeTag(buf, this.values);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(PacketSendServerConfig message, MessageContext ctx){
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @SideOnly(Side.CLIENT)
            @Override
            public void run() {
                ConfigHandler.readFromServerNBT(message.values);
            }
        });
        return null;
    }
}
