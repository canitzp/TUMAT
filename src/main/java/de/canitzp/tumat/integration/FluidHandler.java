package de.canitzp.tumat.integration;

import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

/**
 * @author canitzp
 */
@SuppressWarnings("ConstantConditions")
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
                            component.addOneLineRenderer(new TextComponent(tank.getContents().amount + "mB / " + tank.getCapacity() + "mB " + tank.getContents().getLocalizedName()));
                        }
                    }
                }
            }
        }
        return component;
    }

}
