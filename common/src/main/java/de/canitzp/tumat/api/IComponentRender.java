package de.canitzp.tumat.api;

import de.canitzp.tumat.api.components.DescriptionComponent;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * If you override this, you can give a TooltipComponent the overridden class as custom renderer.
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public interface IComponentRender{

    /**
     * The general method call. Gets called every 3 ticks, independent of the fps.
     * @param fontRenderer The FontRender object
     * @param x The x position of the text
     * @param y The y position of the text
     * @param color The color of the text, default is 0xFFFFFF
     */
    void render(FontRenderer fontRenderer, int x, int y, int color);

    /**
     * This defines how long the line is, to calculate the background width.
     * @param fontRenderer The FontRender object
     * @return The length in pixels.
     * @see FontRenderer#getStringWidth(String)
     */
    int getLength(FontRenderer fontRenderer);

    /**
     * This defines how many lines the tooltips needs
     * @param fontRenderer The FontRender object
     * @return The Amount of lines.
     */
    default int getLines(FontRenderer fontRenderer){return 1;}

    /**
     * This defines which size the height of one line is.
     * @param fontRenderer The FontRender object
     * @return The height per line.
     * @see DescriptionComponent
     */
    default int getHeightPerLine(FontRenderer fontRenderer){return fontRenderer.FONT_HEIGHT + 1;}

}
