package de.canitzp.tumat;

import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUnicodeGlyphButton;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.fml.client.config.GuiUtils.RESET_CHAR;

/**
 * @author canitzp
 */
public class GuiTUMAT extends GuiScreen{

    private List<TooltipComponent> testComps = new ArrayList<>();
    private int relX, relY;

    public GuiTUMAT(){
        TooltipComponent component = new TooltipComponent();
        component.addOneLineRenderer(new TextComponent(TextFormatting.RED.toString() + TextFormatting.BOLD.toString() + "-Line 1-"));
        component.addOneLineRenderer(new TextComponent(TextFormatting.GREEN.toString() + TextFormatting.ITALIC.toString() + "-----Line 2-----"));
        component.addOneLineRenderer(new TextComponent(TextFormatting.BLUE.toString() + TextFormatting.UNDERLINE.toString() + "----------Line 3----------"));
        component.addOneLineRenderer(new TextComponent(TextFormatting.YELLOW.toString() + TextFormatting.OBFUSCATED.toString() + "---------------Line 4---------------"));
        testComps.add(component);
    }

    @Override
    public void initGui(){
        super.initGui();
        int resetWidth = mc.fontRendererObj.getStringWidth("" +  mc.fontRendererObj.getStringWidth(GuiUtils.RESET_CHAR) * 2 + 20);
        addButton(new GuiButton(0, this.width/2 - 40, this.height/2 - 10, 20, 20, "+"));
        addButton(new GuiButton(1, this.width/2 + 20, this.height/2 - 10, 20, 20, "-"));
        addButton(new GuiUnicodeGlyphButton(2, 0, 0, resetWidth, 20, "", GuiUtils.RESET_CHAR, 2.0F));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id){
            case 0: {
                Config.scale += 0.1F;
                break;
            }
            case 1: {
                Config.scale -= 0.1F;
                break;
            }
            case 2: {
                Config.x = 50;
                Config.y = 0;
                Config.scale = 1.0F;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        TooltipComponent.drawCenteredString(this.mc.fontRendererObj, "Scale:", this.width/2, this.height/2 - 4, 0xFFFFFF);
        RenderOverlay.renderComponents(this.mc.fontRendererObj, new ScaledResolution(this.mc), this.width / 2 + (int)Config.x, (int) Config.y, this.testComps);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        relX = mouseX - getXFromPercantage((int) (Config.x));
        relY = mouseY - getYFromPercantage((int) (Config.y));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        Config.x = getPercentageX(mouseX - relX);
        Config.y = getPercentageY(mouseY - relY);
    }

    @Override
    public void onGuiClosed() {
        Config.toSaveX = Config.x;
        Config.toSaveY = Config.y;
        Config.toSaveScale = Config.scale;
        Config.markDirty = true;
        super.onGuiClosed();
    }

    public static int getPercentageX(int x){
        return (int) ((int) (x / (Minecraft.getMinecraft().displayWidth * 1.0F) * 100) * new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor());
    }

    public static int getPercentageY(int y){
        return (int) ((int) (y / (Minecraft.getMinecraft().displayHeight * 1.0F) * 100) * new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor());
    }

    public static int getXFromPercantage(float percantage){
        return (int) ((int) ((percantage / 100.00F) * Minecraft.getMinecraft().displayWidth) / new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor());
    }

    public static int getYFromPercantage(float percantage){
        return (int) ((int) (percantage / 100F * Minecraft.getMinecraft().displayHeight) / new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor());
    }

}
