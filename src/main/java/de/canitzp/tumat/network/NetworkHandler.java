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
        network.registerMessage(PacketUpdateEnergy.class, PacketUpdateEnergy.class, 3, Side.SERVER);
        network.registerMessage(PacketUpdateEnergy.PacketUpdateEnergyClient.class, PacketUpdateEnergy.PacketUpdateEnergyClient.class, 4, Side.CLIENT);
    }

}
