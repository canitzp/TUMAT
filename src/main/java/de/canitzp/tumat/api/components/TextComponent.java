package de.canitzp.tumat.api.components;

import de.canitzp.tumat.api.IComponentRender;
import de.canitzp.tumat.api.TooltipComponent;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class TextComponent implements IComponentRender{

    private String displayString;

    public TextComponent(String displayString){
        this.displayString = displayString;
    }

    public TextComponent setFormat(TextFormatting... formatting){
        for(TextFormatting format : formatting){
            this.displayString = format.toString() + this.displayString;
        }
        return this;
    }

    @Override
    public void render(FontRenderer fontRenderer, int x, int y, int color){
        TooltipComponent.drawCenteredString(fontRenderer, TextFormatting.RESET.toString() + this.displayString, x, y, color);
    }

}
