package de.canitzp.tumat;

import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TUMATApi;
import de.canitzp.tumat.configuration.ConfigHandler;
import de.canitzp.tumat.integration.*;
import de.canitzp.tumat.network.NetworkHandler;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author canitzp
 */
@Mod(name = TUMAT.MODNAME, modid = TUMAT.MODID, version = TUMAT.MODVERSION, guiFactory = "de.canitzp.tumat.configuration.GuiFact", dependencies = TUMAT.DEPENDENCIES, acceptedMinecraftVersions = TUMAT.MCVERSION)
public class TUMAT{

    public static final String MODNAME = "TUMAT";
    public static final String MODID = "tumat";
    public static final String MODVERSION = "@VERSION@";
    public static final String BUILD_DATE = "@BUILD_DATE@";
    public static final String DEPENDENCIES = "before:hardcorequesting;";
    public static final String MCVERSION = "1.12";
    public static boolean DEBUG = MODVERSION.contains("DEBUG");
    public static final Logger logger = LogManager.getLogger(MODNAME);
    @Mod.Instance(MODID)
    public static TUMAT instance;

    static {
        FluidRegistry.enableUniversalBucket();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if(!DEBUG){
            DEBUG = MODVERSION.contains("SION@");
        }
        if(DEBUG){
            logger.info("[PreInit] " + MODNAME + " was launched in debug mode! Build date: " + BUILD_DATE);
        }
        logger.info("[PreInit] Started " + MODNAME + " Version " + MODVERSION);
        logger.info("[PreInit] Load config");
        ConfigHandler.preInit(event);
        logger.info("[PreInit] Load network stuff");
        NetworkHandler.init();
        if(event.getSide().isClient()){
            logger.info("[PreInit] Load integrations");
            loadIntegrations();
            TUMATApi.allowGuiToRenderOverlay(GuiInventory.class, GuiContainerCreative.class, GuiIngameMenu.class, GuiChat.class);
        }
        logger.info("[PreInit] Completed loading");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        if(event.getSide().isClient()){
            logger.info("[PostInit] Initializing all IWorldRenderer");
            for(IWorldRenderer renderer : TUMATApi.getRegisteredComponents()){
                renderer.init();
            }
        }
        logger.info("[PostInit] Completed loading");
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
        TUMATApi.registerRenderComponent(ForgeUnits.class);

        if(isClassLoaded("ic2/api/tile/IEnergyStorage")){
            logger.info("[PreInit][Integration] Loading ElectricalUnit integration");
            Energy.set(Energy.EU);
            TUMATApi.registerRenderComponent(ElectricalUnits.class);
        }

        if(Loader.isModLoaded("ic2")){
            logger.info("[Integration] Loading IndustrialCraft 2 integration");
            TUMATApi.registerRenderComponent(IndustrialCraft2.class);
        }

        //Tanks:
        TUMATApi.registerRenderComponent(FluidHandler.class);

        //Inventory
        TUMATApi.registerRenderComponent(Inventory.class);

        //More:
        TUMATApi.registerRenderComponent(Vanilla.class);
        if(Loader.isModLoaded("actuallyadditions")){
            logger.info("[PreInit][Integration] Loading Actually Additions integration");
            TUMATApi.registerRenderComponent(ActuallyAdditions.class);
        }
        if(Loader.isModLoaded("chiselsandbits")){
            logger.info("[PreInit][Integration] Loading ChiselAndBits integration");
            TUMATApi.registerRenderComponent(ChiselsAndBits.class);
        }
        if(Loader.isModLoaded("tconstruct")){
            logger.info("[Integration] Loading Tinkers Construct integration");
            TUMATApi.registerRenderComponent(TinkersConstruct.class);
        }
        if(Loader.isModLoaded("commoncapabilities")){
            logger.info("[PreInit][Integration] Loading Common Capabilities integration");
            TUMATApi.registerRenderComponent(CommonCapabilities.class);
        }
        if(Loader.isModLoaded("harvestcraft")){
            logger.info("[PreInit][Integration] Loading Pam's HarvestCraft integration");
            TUMATApi.registerRenderComponent(PamsHarvestCraft.class);
        }
        if(Loader.isModLoaded("storagedrawers")){
            logger.info("[PreInit][Integration] Loading Storage Drawers integration");
            TUMATApi.registerRenderComponent(StorageDrawers.class);
        }
        if(Loader.isModLoaded("ichunutil")){
            logger.info("[PreInit][Integration] Loading iChunUtil integration");
            TUMATApi.registerRenderComponent(IChunUtil.class);
        }

        //Harvestability:
        TUMATApi.registerRenderComponent(Harvestability.class);
    }

    private static boolean isClassLoaded(String className){
        try{
            Class.forName(className.replace("/", "."));
            return true;
        } catch(ClassNotFoundException ignored){}
        return false;
    }

    public enum Energy{
        FU,
        TESLA,
        EU;

        public static Energy mainEnergy = FU;
        public boolean isActive;

        public static void set(Energy energy){
            energy.isActive = true;
            if(mainEnergy.ordinal() > energy.ordinal()){
                mainEnergy = energy;
            }
        }
    }

}
