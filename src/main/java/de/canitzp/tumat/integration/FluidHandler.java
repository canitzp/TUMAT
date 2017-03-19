package de.canitzp.tumat.integration;

import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author canitzp
 */
@SuppressWarnings("ConstantConditions")
@SideOnly(Side.CLIENT)
public class FluidHandler implements IWorldRenderer{

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        if(tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)){
            IFluidHandler handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
            if(handler != null){
                IFluidTankProperties[] props = handler.getTankProperties();
                if(props != null){
                    for(IFluidTankProperties tank : props){
                        if(tank != null && tank.getContents() != null){
                            FluidStack fluidStack = tank.getContents();
                            component.addOneLineRenderer(new TextComponent(fluidStack.amount + "mB / " + tank.getCapacity() + "mB " + fluidStack.getLocalizedName()));
                        }
                    }
                }
            }
        }
        return component;
    }

    @Override
    public boolean shouldBeActive() {
        return ConfigBoolean.SHOW_FLUID_TANKS.value;
    }
}
