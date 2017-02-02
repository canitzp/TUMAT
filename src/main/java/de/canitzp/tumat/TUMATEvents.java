package de.canitzp.tumat;

import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TUMATApi;
import de.canitzp.tumat.configuration.ConfigHandler;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import de.canitzp.tumat.network.NetworkHandler;
import de.canitzp.tumat.network.PacketSendServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

/**
 * @author canitzp
 */
@Mod.EventBusSubscriber
public class TUMATEvents{

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void gameOverlayRenderEvent(RenderGameOverlayEvent.Post event){
        if(ConfigBoolean.SHOULD_TUMAT_RENDER.value && event.getType().equals(RenderGameOverlayEvent.ElementType.HOTBAR)){
            Minecraft mc = Minecraft.getMinecraft();
            if(mc.currentScreen == null || TUMATApi.getAllowedGuis().contains(mc.currentScreen.getClass())){
                try{
                    if(mc.currentScreen == null && !TUMAT.MODVERSION.contains(".") && ConfigBoolean.SHOW_FU.value){
                        String tumat = TextFormatting.AQUA.toString() + "T" + TextFormatting.GREEN.toString() + "U" + TextFormatting.RED.toString() + "M" + TextFormatting.YELLOW.toString() + "A" + TextFormatting.AQUA.toString() + "T" + TextFormatting.RESET.toString();
                        String buildText = tumat + " build " + TUMAT.MODVERSION;
                        GlStateManager.pushMatrix();
                        GlStateManager.scale(0.4F, 0.4F, 0.4F);
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
                        Minecraft.getMinecraft().fontRenderer.drawString(buildText, 2, 2, 0x80FFFFFF);
                        GlStateManager.popMatrix();
                    }
                    RenderOverlay.render(mc.world, mc.player, event.getResolution(), mc.fontRenderer, event.getType(), event.getPartialTicks(), mc.world.getTotalWorldTime() % 3 == 0);
                } catch(Exception e){
                    InfoUtil.drawCenteredString(mc.fontRenderer, "<ERROR>", GuiTUMAT.getXFromPercantage(), GuiTUMAT.getYFromPercantage(), 0xFFFFFF);
                    if(mc.world.getTotalWorldTime() % 100 == 0){
                        TUMAT.logger.error("An Error occurred while rendering the tooltip.", e);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onConfigSave(ConfigChangedEvent.OnConfigChangedEvent event){
        if(TUMAT.MODID.equals(event.getModID())){
            ConfigHandler.defineConfigs();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SideOnly(Side.CLIENT)
    public static void tooltipRenderEventModName(ItemTooltipEvent event){
        event.getToolTip().add(InfoUtil.getModName(event.getItemStack().getItem()));
    }

    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public static void serverJoinEvent(EntityJoinWorldEvent event){
        if(ConfigBoolean.SERVER_CONTROL.value){
            Entity entity = event.getEntity();
            if(entity instanceof EntityPlayerMP){
                NetworkHandler.network.sendTo(new PacketSendServerConfig(), (EntityPlayerMP) entity);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onLoggout(PlayerEvent.PlayerLoggedOutEvent event){
        if(event.player.isServerWorld()){
            ConfigHandler.defineConfigs();
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void openInventory(GuiScreenEvent.InitGuiEvent event){
        if(event.getGui().getClass().equals(GuiOptions.class)){
            String s = "TUMAT";
            event.getButtonList().add(new GuiButton(963, 0, 0, Minecraft.getMinecraft().fontRenderer.getStringWidth(s) + 20, 20, s));
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void buttonPressInventory(GuiScreenEvent.ActionPerformedEvent event){
        if(event.getGui().getClass().equals(GuiOptions.class)){
            if(event.getButton().id == 963){
                Minecraft.getMinecraft().displayGuiScreen(new GuiTUMAT());
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent()
    public static void renderGuiContainer(GuiScreenEvent.DrawScreenEvent.Post event){
        if(ConfigBoolean.SHOW_SLOT_NUMBERS.value && event.getGui() instanceof GuiContainer){
            if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)){
                FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
                GuiContainer gui = (GuiContainer) event.getGui();
                int guiLeft = 0;
                int guiTop = 0;
                for(IWorldRenderer rend : TUMATApi.getRegisteredComponents()){
                    int i = rend.getGuiLeftOffset(gui);
                    if(i != 0){
                        guiLeft = i;
                    }
                    int j = rend.getGuiTopOffset(gui);
                    if(j != 0){
                        guiTop = j;
                    }
                }
                if(guiLeft == 0){
                    guiLeft = ((int)ReflectionHelper.getPrivateValue(GuiContainer.class, gui, 4)) - 1;
                }
                if(guiTop == 0){
                    guiTop = ((int)ReflectionHelper.getPrivateValue(GuiContainer.class, gui, 5)) - 1;
                }
                GlStateManager.pushAttrib();
                GlStateManager.pushMatrix();
                GlStateManager.disableBlend();
                RenderHelper.disableStandardItemLighting();
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                for(Slot slot : gui.inventorySlots.inventorySlots){
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    renderer.drawString(String.valueOf(slot.getSlotIndex()), 2 * (slot.xPos + guiLeft), 2 * (slot.yPos + guiTop), 0xFFFFFF, false);
                }
                RenderHelper.enableStandardItemLighting();
                GlStateManager.enableBlend();
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }
        }
    }

}
