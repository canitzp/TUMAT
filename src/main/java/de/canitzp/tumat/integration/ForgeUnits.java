package de.canitzp.tumat.integration;

import de.canitzp.tumat.Config;
import de.canitzp.tumat.TUMAT;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.energy.CapabilityEnergy;

/**
 * @author canitzp
 */
public class ForgeUnits implements IWorldRenderer{

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        if(TUMAT.Energy.TESLA.isActive){
            if(tileEntity.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, side) || tileEntity.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side) || tileEntity.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, side)){
                return component;
            }
        }
        if(TUMAT.Energy.EU.isActive){
            if(tileEntity instanceof ic2.api.tile.IEnergyStorage){
                return component;
            }
        }
        if(TUMAT.Energy.RF.isActive){
            if(tileEntity instanceof cofh.api.energy.IEnergyHandler){
                return component;
            }
        }

        if(tileEntity.hasCapability(CapabilityEnergy.ENERGY, side)){
            int energy = tileEntity.getCapability(CapabilityEnergy.ENERGY, side).getEnergyStored();
            int cap = tileEntity.getCapability(CapabilityEnergy.ENERGY, side).getMaxEnergyStored();
            if(cap > 0){
                component.addOneLineRenderer(new TextComponent(energy + " ForgeUnits /" + cap + " ForgeUnits").setFormat(TextFormatting.RED));
            }
        }

        return component;
    }

    @Override
    public boolean shouldBeActive(){
        return Config.showEnergy;
    }
}
