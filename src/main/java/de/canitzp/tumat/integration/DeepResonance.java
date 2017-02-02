package de.canitzp.tumat.integration;

import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * @author canitzp
 */
public class DeepResonance implements IWorldRenderer{

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        /*
        if(tileEntity instanceof TileTank){
            Map<EnumFacing, TileTank.Mode> settings = ((TileTank) tileEntity).getSettings();
            component.addOneLineRenderer(new TextComponent("Mode: " + settings.get(side).func_176610_l()));
            FluidStack fluidStack = ((TileTank) tileEntity).getFluid();
            System.out.println(fluidStack);
            if(fluidStack != null && fluidStack.getFluid() != null){
                component.addOneLineRenderer(new TextComponent(fluidStack.amount + "mB/" + ((TileTank) tileEntity).getCapacity() + "mB " + fluidStack.getLocalizedName()));
            }
        }
        */
        return null;
    }
}
