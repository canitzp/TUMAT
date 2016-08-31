package de.canitzp.tumat;

import de.canitzp.tumat.api.TUMATApi;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.integration.ActuallyAdditions;
import de.canitzp.tumat.integration.RedstoneFlux;
import de.canitzp.tumat.integration.Tesla;
import de.canitzp.tumat.integration.Vanilla;
import de.canitzp.tumat.network.NetworkHandler;
import de.canitzp.tumat.network.PacketSendServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModAPIManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
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

    @Mod.Instance(MODID)
    public static TUMAT instance;

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
        if(ModAPIManager.INSTANCE.hasAPI("CoFHAPI|energy")){
            logger.info("[Integration] Loading RedstoneFlux integration");
            TUMATApi.registerRenderComponent(RedstoneFlux.class);
        }
        if(Loader.isModLoaded("actuallyadditions")){
            logger.info("[Integration] Loading ActuallyAdditions integration");
            TUMATApi.registerRenderComponent(ActuallyAdditions.class);
        }
        if(Loader.isModLoaded("tconstruct")){
            Vanilla.isTinkersConstructLoaded = true;
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void gameOverlayRenderEvent(RenderGameOverlayEvent.Post event){
        if(Config.shouldRenderOverlay && event.getType().equals(RenderGameOverlayEvent.ElementType.HOTBAR)){
            Minecraft mc = Minecraft.getMinecraft();
            if(mc.currentScreen == null || TUMATApi.getAllowedGuis().contains(mc.currentScreen.getClass())){
                try{
                    RenderOverlay.render(mc.theWorld, mc.thePlayer, event.getResolution(), mc.fontRendererObj, event.getType(), event.getPartialTicks(), mc.theWorld.getTotalWorldTime() % 3 == 0);
                } catch(Exception e){
                    TooltipComponent.drawCenteredString(mc.fontRendererObj, "<ERROR>", event.getResolution().getScaledWidth() / 2 + (int) Config.x, (int) Config.y, 0xFFFFFF);
                    if(mc.theWorld.getTotalWorldTime() % 100 == 0){
                        logger.error("An Error occurred while rendering the tooltip.", e);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SideOnly(Side.CLIENT)
    public static void tooltipRenderEvent(ItemTooltipEvent event){
        String s = InfoUtil.getItemName(event.getItemStack());
        if(s != null){
            event.getToolTip().clear();
            event.getToolTip().add(s + (Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? TextFormatting.RESET + TooltipComponent.getAdvancedName(event.getItemStack()) : ""));
            String[] desc = InfoUtil.getDescription(event.getItemStack());
            if(desc != null){
                for(String desc1 : desc){
                    event.getToolTip().add(TextFormatting.GRAY + desc1);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SideOnly(Side.CLIENT)
    public static void tooltipRenderEventModName(ItemTooltipEvent event){
        String s = InfoUtil.getModName(event.getItemStack());
        if(s == null){
            s = RenderOverlay.getModName(event.getItemStack().getItem().getRegistryName().getResourceDomain());
        }
        event.getToolTip().add(RenderOverlay.modNameFormat + s);
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

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void openInventory(GuiScreenEvent.InitGuiEvent event){
        if(event.getGui().getClass().equals(GuiInventory.class) || event.getGui().getClass().equals(GuiContainerCreative.class)){
            String s = "TUMAT";
            event.getButtonList().add(new GuiButton(963, 0, 0, Minecraft.getMinecraft().fontRendererObj.getStringWidth(s) + 20, 20, s));
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void buttonPressInventory(GuiScreenEvent.ActionPerformedEvent event) {
        if (event.getGui().getClass().equals(GuiInventory.class) || event.getGui().getClass().equals(GuiContainerCreative.class)) {
            if(event.getButton().id == 963){
                Minecraft.getMinecraft().displayGuiScreen(new GuiTUMAT());
            }
        }
    }

}
