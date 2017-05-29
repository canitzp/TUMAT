package de.canitzp.tumat.integration;

import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.client.gui.GuiMultiModule;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tileentity.IProgress;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class TinkersConstruct implements IWorldRenderer {

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate) {
        if(tileEntity instanceof IProgress){
            float progress = ((IProgress) tileEntity).getProgress();
            if(progress != 0){
                component.add(new TextComponent("Progress: " + Util.dfPercent.format(progress)), TooltipComponent.Priority.HIGH);
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

}
