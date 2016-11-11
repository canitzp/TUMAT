package de.canitzp.tumat.integration;

import cofh.api.energy.IEnergyHandler;
import de.canitzp.tumat.Config;
import de.canitzp.tumat.TUMAT;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;

/**
 * @author canitzp
 */
public class RedstoneFlux implements IWorldRenderer{

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        if(ConfigBoolean.SHOW_TESLA.value && TUMAT.Energy.TESLA.isActive){
            if(tileEntity.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, side) || tileEntity.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side) || tileEntity.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, side)){
                return component;
            }
        }
        if(ConfigBoolean.SHOW_EU.value && TUMAT.Energy.EU.isActive){
            if(tileEntity instanceof ic2.api.tile.IEnergyStorage){
                return component;
            }
        }
        if(ConfigBoolean.SHOW_RF.value && TUMAT.Energy.RF.isActive) {
            if (tileEntity instanceof IEnergyHandler) {
                TooltipComponent.syncTileEntity(tileEntity, shouldCalculate, "Energy");
                try {
                    int stored = ((IEnergyHandler) tileEntity).getEnergyStored(side);
                    int max = ((IEnergyHandler) tileEntity).getMaxEnergyStored(side);
                    if (max > 0) {
                        component.addOneLineRenderer(new TextComponent(TextFormatting.RED.toString() + stored + "/" + max + "RF"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return component;
    }

    @Override
    public boolean shouldBeActive(){
        return ConfigBoolean.SHOW_RF.value;
    }
}
