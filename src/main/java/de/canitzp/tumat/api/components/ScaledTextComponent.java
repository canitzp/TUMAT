package de.canitzp.tumat.api.components;

import de.canitzp.tumat.api.TooltipComponent;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;

/**
 * @author canitzp
 */
public class ScaledTextComponent extends TextComponent {

    public float scale;

    public ScaledTextComponent(String displayString, float scale) {
        super(displayString);
        this.scale = scale;
    }

    @Override
    public void render(FontRenderer fontRenderer, int x, int y, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(this.scale, this.scale, 0);
        super.render(fontRenderer, 0, 0, color);
        GlStateManager.popMatrix();
    }

    @Override
    public int getHeightPerLine(FontRenderer fontRenderer) {
        return Math.round(this.scale * 10);
    }

    public static void createOneLine(TooltipComponent component, float scale, String displayString, TextFormatting... formatting){
        component.addOneLineRenderer(new ScaledTextComponent(displayString, scale).setFormat(formatting));
    }

}
