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

    static{
        network = NetworkRegistry.INSTANCE.newSimpleChannel(TUMAT.MODID);
    }

    public static void init(){
        network.registerMessage(PacketSendServerConfig.class, PacketSendServerConfig.class, 0, Side.CLIENT);
        network.registerMessage(PacketUpdateTileEntity.class, PacketUpdateTileEntity.class, 1, Side.SERVER);
        network.registerMessage(PacketUpdateTileEntity.PacketTileEntityToClient.class, PacketUpdateTileEntity.PacketTileEntityToClient.class, 2, Side.CLIENT);

        network.registerMessage(PacketSyncEnergyClient.class, PacketSyncEnergyClient.class, 5, Side.CLIENT);
        network.registerMessage(PacketSyncEnergyServer.class, PacketSyncEnergyServer.class, 6, Side.SERVER);
    }

}
