package de.canitzp.tumat.network;

import de.canitzp.tumat.TUMAT;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author canitzp
 */
public class NetworkHandler{

    public static final SimpleNetworkWrapper network;

    static {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(TUMAT.MODID);
    }

    public static void init(){
        network.registerMessage(PacketSendServerConfig.class, PacketSendServerConfig.class, 0, Side.CLIENT);
    }

}
