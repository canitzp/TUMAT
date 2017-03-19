package de.canitzp.tumat.integration;

import de.canitzp.tumat.api.IWorldRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class TinkersConstruct implements IWorldRenderer {

    /*
    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate) {
        if(tileEntity instanceof IProgress){
            float progress = ((IProgress) tileEntity).getProgress();
            if(progress != 0){
                component.addOneLineRenderer(new TextComponent("Progress: " + Util.dfPercent.format(progress)));
            }
        }
        return component;
    }

    @Override
    public int getGuiLeftOffset(GuiContainer gui) {
        if(gui instanceof GuiMultiModule){
            return ((GuiMultiModule) gui).cornerX;
        }
        return 0;
    }

    @Override
    public int getGuiTopOffset(GuiContainer gui) {
        if(gui instanceof GuiMultiModule){
            return ((GuiMultiModule) gui).cornerY;
        }
        return 0;
    }

    @Override
    public boolean shouldBeActive() {
        return ConfigBoolean.SHOW_SPECIAL_TILE_STATS.value;
    }
    */

}
