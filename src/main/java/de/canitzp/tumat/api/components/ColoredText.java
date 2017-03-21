package de.canitzp.tumat.api.components;

import de.canitzp.tumat.InfoUtil;
import de.canitzp.tumat.api.TooltipComponent;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is if you need to color a text with a hex value. (#AARRGGBB - AA isn't working)
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class ColoredText extends TextComponent{

    protected int color;

    public ColoredText(String displayString, int color){
        super(displayString);
        this.color = color;
    }

    public ColoredText(String displayString, Colors color){
        this(displayString, color.getHex());
    }

    public ColoredText(TextComponent textComponent, int color){
        this(textComponent.displayString, color);
    }

    @Override
    public void render(FontRenderer fontRenderer, int x, int y, int color){
        InfoUtil.drawCenteredString(fontRenderer, TextFormatting.RESET.toString() + super.displayString, x, y, this.color);
    }

    public static void createOneLine(TooltipComponent component, String displayString, int color){
        component.addOneLineRenderer(new ColoredText(displayString, color));
    }

    public static void createOneLine(TooltipComponent component, String displayString, Colors color){
        createOneLine(component, displayString, color.getHex());
    }

    public enum Colors{
        BLACK(0x00000000),
        WHITE(0x00FFFFFF),
        ORANGE_BRIGHT(0x00EA723F),
        BROWN_BRIGHT(0x00965A40),
        BROWN_PLANT(0x00A97D15),
        RED_REDSTONE(0x00720000),

        ;

        /** Format 0xAARRGGBB **/
        private int hex;
        Colors(int hex){
            this.hex = hex;
        }
        public int getHex(){
            return hex;
        }
    }

}
