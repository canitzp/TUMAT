package de.canitzp.tumat.integration;

import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.darkhax.tesla.lib.TeslaUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;

/**
 * @author canitzp
 */
public class Tesla implements IWorldRenderer{

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        if(tileEntity.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, side)){
            long currentEnergy = tileEntity.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, side).getStoredPower();
            long capacity = tileEntity.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, side).getCapacity();
            component.addRenderer(new TextComponent(TextFormatting.AQUA + TeslaUtils.getDisplayableTeslaCount(currentEnergy) + "/" + TeslaUtils.getDisplayableTeslaCount(capacity)));
        }
        return component;
    }

}
