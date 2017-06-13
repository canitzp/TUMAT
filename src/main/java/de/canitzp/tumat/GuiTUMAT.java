package de.canitzp.tumat;

import com.google.common.collect.Lists;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.configuration.ConfigHandler;
import de.canitzp.tumat.configuration.cats.ConfigFloat;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class GuiTUMAT extends GuiScreen{

    private static List<String> helpHovering = Lists.newArrayList(
            "This Gui is work in progress and get's new features soon.",
            "(Scaling, Direct Move areas, ...)",
            "Now you can use the arrows at the middle of the screen,",
            "to move the tooltip.",
            "Press 'LeftShift' while clicking on the buttons to",
            "move it only half as far than normal and press 'LeftControl'",
            "to move it only 1/4 as far.");
    private ResourceLocation location = new ResourceLocation(TUMAT.MODID, "textures/gui/tumat_elements.png");
    private TooltipComponent component;
    private List<PutItHere> directMove = new ArrayList<>();

    public GuiTUMAT(){
        Block block = Block.REGISTRY.getRandomObject(new Random(System.currentTimeMillis()));
        component = new TooltipComponent();
        component.setName(new TextComponent(InfoUtil.getBlockName(block.getDefaultState())));
        component.add(new TextComponent("Power: " + TextFormatting.RED + "6"), TooltipComponent.Priority.HIGH);
        component.setModName(new TextComponent(InfoUtil.getModName(block)));
        //directMove.add(new PutItHere(15, 15));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.mc.getTextureManager().bindTexture(this.location);
        this.drawTexturedModalRect(width / 2 - 13, height / 2 - 13, 0, 0, 27, 27);
        for(PutItHere directMover : this.directMove){
            directMover.render(this.mc.fontRenderer);
        }
        RenderOverlay.renderComponents(this.mc.fontRenderer, this.component);
        this.mc.fontRenderer.drawString("?", 2, 2, 0xFFFFFF);
        if(mouseX <= 7 && mouseY <= 11){
            this.drawHoveringText(helpHovering, mouseX + 2, mouseY + 18);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
        if(mouseX >= (width / 2 - 13) && mouseX <= (width / 2 - 13 + 27) && mouseY >= (height / 2 - 13) && mouseY <= (height / 2 - 13 + 27)){
            if(mouseY >= (height / 2 - 4) && mouseY <= (height / 2 + 4)){
                if(mouseX <= (width / 2 - 4)){
                    ConfigFloat.OFFSET_X.saveNewValue(ConfigFloat.OFFSET_X.value -= getMoveFactor());
                } else if(mouseX >= (width / 2 + 4)){
                    ConfigFloat.OFFSET_X.saveNewValue(ConfigFloat.OFFSET_X.value += getMoveFactor());
                }
            }
            if(mouseX >= (width / 2 - 4) && mouseX <= (width / 2 + 4)){
                if(mouseY <= (height / 2 - 4)){
                    ConfigFloat.OFFSET_Y.saveNewValue(ConfigFloat.OFFSET_Y.value -= getMoveFactor());
                } else if(mouseY >= (height / 2 + 4)){
                    ConfigFloat.OFFSET_Y.saveNewValue(ConfigFloat.OFFSET_Y.value += getMoveFactor());
                }
            }
        } else {
            /*
            int[] intray = null;
            for(PutItHere directMover : this.directMove){
                intray = directMover.onMouseClick(this.mc.fontRenderer, mouseX, mouseY, this.component.getLength());
                if(intray != null){
                    break;
                }
            }
            if(intray != null){
                ConfigFloat.OFFSET_X.saveNewValue(getPercentageX(intray[0]));
                ConfigFloat.OFFSET_Y.saveNewValue(getPercentageY(intray[1]));
            }
            */
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onGuiClosed(){
        ConfigHandler.defineConfigs();
        super.onGuiClosed();
    }

    private float getMoveFactor(){
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 0.5F : Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) ? 0.25F : 1.0F;
    }

    public static int getPercentageX(int x){
        return (int) (x / (Minecraft.getMinecraft().displayWidth * 1.0F) * 100) * new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
    }

    public static int getPercentageY(int y){
        return (int) (y / (Minecraft.getMinecraft().displayHeight * 1.0F) * 100) * new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
    }

    public static int getXFromPercantage(){
        return (int) ((ConfigFloat.OFFSET_X.value / 100.00F) * Minecraft.getMinecraft().displayWidth) / new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
    }

    public static int getYFromPercantage(){
        return (int) (ConfigFloat.OFFSET_Y.value / 100F * Minecraft.getMinecraft().displayHeight) / new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
    }

    public static class PutItHere extends Gui{
        private int x, y, textX = 27, textY = 0, width = 100, height = 27;

        public PutItHere(int x, int y){
            this.x = x;
            this.y = y;
        }

        public void render(FontRenderer renderer){
            this.drawTexturedModalRect(this.x, this.y, this.textX, this.textY, this.width, this.height);
            GlStateManager.pushAttrib();
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5, 0.5, 0.5);
            InfoUtil.drawCenteredString(renderer, "Click here for direct moving.", this.x + this.width + 9, this.y + this.height + 9, MapColor.GRAY.colorValue);
            GlStateManager.popMatrix();
            GlStateManager.popAttrib();
        }

        public int[] onMouseClick(FontRenderer renderer, int mouseX, int mouseY, int length){
            if(mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.width){
                int newX = this.x + (this.width / 2);
                return new int[]{newX, this.y + (this.height / 2)};
            }
            return null;
        }
    }

}
