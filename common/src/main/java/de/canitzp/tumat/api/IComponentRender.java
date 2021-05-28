package de.canitzp.tumat.api;

import de.canitzp.tumat.api.components.DescriptionComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;

/**
 * If you override this, you can give a TooltipComponent the overridden class as custom renderer.
 * @author canitzp
 */
@Environment(EnvType.CLIENT)
public interface IComponentRender{

    /**
     * The general method call. Gets called every 3 ticks, independent of the fps.
     * @param fontRenderer The FontRender object
     * @param x The x position of the text
     * @param y The y position of the text
     * @param color The color of the text, default is 0xFFFFFF
     */
    void render(Font fontRenderer, int x, int y, int color);

    /**
     * This defines how long the line is, to calculate the background width.
     * @param fontRenderer The FontRender object
     * @return The length in pixels.
     * @see Font#width(String)
     */
    int getLength(Font fontRenderer);

    /**
     * This defines how many lines the tooltips needs
     * @param fontRenderer The FontRender object
     * @return The Amount of lines.
     */
    default int getLines(Font fontRenderer){return 1;}

    /**
     * This defines which size the height of one line is.
     * @param fontRenderer The FontRender object
     * @return The height per line.
     * @see DescriptionComponent
     */
    default int getHeightPerLine(Font fontRenderer){return fontRenderer.lineHeight + 1;}

}
