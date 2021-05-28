package de.canitzp.tumat;

import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TUMATApi;
import de.canitzp.tumat.integration.*;
import de.canitzp.tumat.network.NetworkHandler;
import me.shedaniel.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author canitzp
 */
public class TUMAT{

    public static final String MODNAME = "TUMAT";
    public static final String MODID = "tumat";
    public static final Logger logger = LogManager.getLogger(MODNAME);

    public static void init() {
        logger.info("[Init] Load config");
        //ConfigHandler.preInit(event);
        logger.info("[Init] Load network stuff");
        NetworkHandler.init();
        if(Platform.getEnv() == EnvType.CLIENT){
            logger.info("[Init] Registering events");
            TUMATEvents.initClient();
            logger.info("[Init] Load integrations");
            loadIntegrations();
            TUMATApi.allowGuiToRenderOverlay(InventoryScreen.class, CreativeModeInventoryScreen.class, ChatScreen.class);
            logger.info("[Init] Initializing all IWorldRenderer");
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                renderer.init();
            }
        }
        logger.info("[Init] Completed loading");
    }

    /**
     * Order to show stats:
     * 1. Name and Description of the Object
     * 2. Energy of the Object
     * 3. Fluids of the Object
     * 4. Light level of the Object
     * 5. Harvestability of the Object
     * 6. Modname
     */
    @Environment(EnvType.CLIENT)
    private static void loadIntegrations(){
        //Energy:

        //Tanks:

        //Inventory

        //More:
        TUMATApi.registerRenderComponent(Vanilla.class);

        //Harvestability:
        TUMATApi.registerRenderComponent(Harvestability.class);
    }

}
