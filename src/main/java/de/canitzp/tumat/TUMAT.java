package de.canitzp.tumat;

import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TUMATApi;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.integration.ActuallyAdditions;
import de.canitzp.tumat.integration.Tesla;
import de.canitzp.tumat.integration.Vanilla;
import de.canitzp.tumat.network.NetworkHandler;
import de.canitzp.tumat.network.PacketSendServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
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

    @SideOnly(Side.CLIENT)
    private void loadIntegrations(){
        TUMATApi.registerRenderComponent(Vanilla.class);
        if(Loader.isModLoaded("tesla")){
            logger.info("[Integration] Loading Tesla integration");
            TUMATApi.registerRenderComponent(Tesla.class);
        }
        if(Loader.isModLoaded("actuallyadditions")){
            logger.info("[Integration] Loading ActuallyAdditions integration");
            TUMATApi.registerRenderComponent(ActuallyAdditions.class);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void gameOverlayRenderEvent(RenderGameOverlayEvent.Post event){
        if(Config.shouldRenderOverlay && event.getType().equals(RenderGameOverlayEvent.ElementType.ALL)){
            Minecraft mc = Minecraft.getMinecraft();
            if(mc.currentScreen == null || TUMATApi.getAllowedGuis().contains(mc.currentScreen.getClass())){
                try{
                    RenderOverlay.render(mc.theWorld, mc.thePlayer, event.getResolution(), mc.fontRendererObj, event.getType(), event.getPartialTicks(), mc.theWorld.getTotalWorldTime() % 3 == 0);
                } catch(Exception e){
                    TooltipComponent.drawCenteredString(mc.fontRendererObj, "<ERROR>", event.getResolution().getScaledWidth() / 2 + (int) Config.x, (int) Config.y, 0xFFFFFF);
                    if(mc.theWorld.getTotalWorldTime() % 100 == 0){
                        logger.error("An Error occurred while rendering the tooltip.", e);
                    }
                }
            }
        }
    }

    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public static void serverJoinEvent(EntityJoinWorldEvent event){
        if(Config.serverControl){
            Entity entity = event.getEntity();
            if(entity instanceof EntityPlayerMP){
                NetworkHandler.network.sendTo(new PacketSendServerConfig(), (EntityPlayerMP) entity);
            }
        }
    }


    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if(event.getModID().equals(TUMAT.MODID)){
            Config.init();
            Config.config.save();
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onLoggout(PlayerEvent.PlayerLoggedOutEvent event){
        if(event.player.isServerWorld()){
            Config.config.load();
            Config.init();
        }
    }

}
