package de.canitzp.tumat;

import de.canitzp.tumat.api.TUMATApi;
import de.canitzp.tumat.integration.*;
import de.canitzp.tumat.network.NetworkHandler;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModAPIManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author canitzp
 */
@Mod(name = TUMAT.MODNAME, modid = TUMAT.MODID, version = TUMAT.MODVERSION, guiFactory = "de.canitzp.tumat.GuiConfigFactory")
public class TUMAT{

    public static final String MODNAME = "TUMAT";
    public static final String MODID = "tumat";
    public static final String MODVERSION = "@VERSION@";
    public static final Logger logger = LogManager.getLogger(MODNAME);
    @Mod.Instance(MODID)
    public static TUMAT instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        logger.info("[PreInit] Started " + MODNAME + " Version " + MODVERSION);
        logger.info("[PreInit] Load config");
        Config.init(event);
        NetworkHandler.init();
        if(event.getSide().isClient()){
            logger.info("[PreInit] Load client stuff");
            loadIntegrations();
            TUMATApi.allowGuiToRenderOverlay(GuiInventory.class, GuiContainerCreative.class, GuiIngameMenu.class, GuiChat.class);
        }
        MinecraftForge.EVENT_BUS.register(TUMAT.class);
        logger.info("[PreInit] Completed loading");
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
    @SideOnly(Side.CLIENT)
    private void loadIntegrations(){
        //Energy:
        Energy.FU.isActive = true;
        if(Loader.isModLoaded("tesla")){
            logger.info("[Integration] Loading Tesla integration");
            Energy.set(Energy.TESLA);
            TUMATApi.registerRenderComponent(Tesla.class);
        }
        if(ModAPIManager.INSTANCE.hasAPI("CoFHAPI|energy")){
            logger.info("[Integration] Loading RedstoneFlux integration");
            Energy.set(Energy.RF);
            TUMATApi.registerRenderComponent(RedstoneFlux.class);
        }
        if(Loader.isModLoaded("IC2") || isClassLoaded("ic2/api/tile/IEnergyStorage")){
            logger.info("[Integration] Loading IndustrialCraft 2 integration");
            Energy.set(Energy.EU);
            TUMATApi.registerRenderComponent(IndustrialCraft2.class);
        }

        //Tanks:
        TUMATApi.registerRenderComponent(FluidHandler.class);

        //More:
        TUMATApi.registerRenderComponent(Vanilla.class);
        if(Loader.isModLoaded("actuallyadditions")){
            logger.info("[Integration] Loading ActuallyAdditions integration");
            TUMATApi.registerRenderComponent(ActuallyAdditions.class);
        }
        if(Loader.isModLoaded("chiselsandbits")){
            logger.info("[Integration] Loading ChiselAndBits integration");
            TUMATApi.registerRenderComponent(ChiselsAndBits.class);
        }

        //Harvestability:
        TUMATApi.registerRenderComponent(Harvestability.class);
    }

    private static boolean isClassLoaded(String className){
        try{
            Class.forName(className.replace("/", "."));
            return true;
        } catch(ClassNotFoundException e){
        }
        return false;
    }

    public enum Energy{
        TESLA,
        RF,
        FU,
        EU;

        public static Energy mainEnergy = FU;
        public boolean isActive;

        public static void set(Energy energy){
            energy.isActive = true;
            if(mainEnergy.ordinal() < energy.ordinal()){
                mainEnergy = energy;
            }
        }
    }

}
