package de.canitzp.tumat;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TUMATApi;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.DescriptionComponent;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import de.canitzp.tumat.local.L10n;
import me.shedaniel.architectury.event.events.GuiEvent;
import me.shedaniel.architectury.event.events.TooltipEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;

/**
 * @author canitzp
 */
public class TUMATEvents{
    
    public static void initClient(){
        GuiEvent.RENDER_HUD.register((matrices, tickDelta) -> {
            if(ConfigBoolean.SHOULD_TUMAT_RENDER.value){
                Minecraft mc = Minecraft.getInstance();
                if(mc.screen == null || TUMATApi.getAllowedGuis().contains(mc.screen.getClass())){
                    try{
                        /*
                        if(TUMAT.DEBUG){
                        String tumat = TextFormatting.AQUA.toString() + "T" + TextFormatting.GREEN.toString() + "U" + TextFormatting.RED.toString() + "M" + TextFormatting.YELLOW.toString() + "A" + TextFormatting.AQUA.toString() + "T" + TextFormatting.RESET.toString();
                        String buildText = tumat + " build " + TUMAT.BUILD_DATE;
                        GlStateManager.pushMatrix();
                        GlStateManager.scale(0.4F, 0.4F, 0.4F);
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
                        Minecraft.getMinecraft().fontRenderer.drawString(buildText, 2, 2, 0x80FFFFFF);
                        GlStateManager.popMatrix();
                    }
                         */
                        RenderOverlay.render(mc.level, mc.player, mc.font, tickDelta, mc.level.getGameTime() % 3 == 0);
                    } catch(Exception e){
                        InfoUtil.drawCenteredString(matrices, mc.font, L10n.ERROR_TEXT, GuiTUMAT.getXFromPercantage(), GuiTUMAT.getYFromPercantage(), 0xFFFFFF);
    
                        RenderOverlay.renderComponents(mc.font, new TooltipComponent().setName(new TextComponent(L10n.ERROR_TEXT))
                                                                                              .add(new DescriptionComponent(Lists.newArrayList("")), TooltipComponent.Priority.HIGH));
                        if(mc.level.getGameTime() % 100 == 0){
                            TUMAT.logger.error("An Error occurred while rendering the tooltip.", e);
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    
        TooltipEvent.ITEM.register((stack, lines, flag) -> {
            lines.add(new net.minecraft.network.chat.TextComponent(InfoUtil.getModName(stack)));
        });
        
        GuiEvent.RENDER_POST.register((screen, matrices, mouseX, mouseY, delta) -> {
            if(ConfigBoolean.SHOW_SLOT_NUMBERS.value && screen instanceof ContainerScreen){
                if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_CONTROL)){
                    ContainerScreen gui = (ContainerScreen) screen;
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
                        guiLeft = ReflectionHelper.getGuiLeft(gui) - 1;
                    }
                    if(guiTop == 0){
                        guiTop = ReflectionHelper.getGuiTop(gui) - 1;
                    }
                    matrices.pushPose();
                    GlStateManager._disableBlend();
                    //RenderHelper.disableStandardItemLighting();
                    matrices.scale(0.5F, 0.5F, 0.5F);
                    for(Slot slot : gui.getMenu().slots){
                        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
                        Minecraft.getInstance().font.draw(matrices, String.valueOf(slot.index), 2 * (slot.x + guiLeft), 2 * (slot.y + guiTop), 0xFFFFFF);
                    }
                    //RenderHelper.enableStandardItemLighting();
                    GlStateManager._enableBlend();
                    matrices.popPose();
                }
            }
        });
    }

}
