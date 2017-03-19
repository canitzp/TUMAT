package de.canitzp.tumat.integration;

import de.canitzp.tumat.api.IWorldRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class Tesla implements IWorldRenderer{

    /*
    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        if(tileEntity.hasCapability(CapabilityEnergy.ENERGY, side) && tileEntity.getCapability(CapabilityEnergy.ENERGY, side) != null){
            return component;
        }

        if(tileEntity.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, side)){
            long currentEnergy = tileEntity.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, side).getStoredPower();
            long capacity = tileEntity.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, side).getCapacity();

            NetworkHandler.network.sendToServer(new PacketUpdateEnergy(tileEntity.getPos(), side));

            if(capacity > 0){
                component.addOneLineRenderer(new TextComponent(TextFormatting.AQUA + TeslaUtils.getDisplayableTeslaCount(currentEnergy) + "/" + TeslaUtils.getDisplayableTeslaCount(capacity)));
            }
        } else {
            for(EnumFacing facing : EnumFacing.values()){
                if(facing != side){
                    if(tileEntity.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, facing)){
                        component.addOneLineRenderer(new TextComponent(TextFormatting.AQUA + "Found Tesla holder at side " + StringUtils.capitalize(facing.getName())));
                    }
                }
            }
        }
        return component;
    }

    @Override
    public TooltipComponent renderEntityItem(WorldClient world, EntityPlayerSP player, EntityItem entity, ItemStack stack, TooltipComponent component, boolean shouldCalculate){
        if(stack.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, null)){
            long currentEnergy = stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null).getStoredPower();
            long capacity = stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null).getCapacity();
            if(capacity > 0){
                component.addOneLineRenderer(new TextComponent(TextFormatting.AQUA + TeslaUtils.getDisplayableTeslaCount(currentEnergy) + "/" + TeslaUtils.getDisplayableTeslaCount(capacity)));
            }
        }
        return component;
    }

    @Override
    public boolean shouldBeActive() {
        return ConfigBoolean.SHOW_TESLA.value;
    }
    */

}
