package de.canitzp.tumat.api.components;

import de.canitzp.tumat.InfoUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;

/**
 * This class is if you need to color a text with a hex value. (#AARRGGBB - AA isn't working)
 * @author canitzp
 */
public class ColoredText extends TextComponent{

    protected int color;

    public ColoredText(String displayString, int color){
        super(displayString);
        this.color = color;
    }

    public ColoredText(TextComponent textComponent, int color){
        this(textComponent.displayString, color);
    }

    @Override
    public void render(FontRenderer fontRenderer, int x, int y, int color){
        InfoUtil.drawCenteredString(fontRenderer, TextFormatting.RESET.toString() + super.displayString, x, y, this.color);
    }
}
