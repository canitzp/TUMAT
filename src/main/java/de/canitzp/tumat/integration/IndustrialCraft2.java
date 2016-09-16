package de.canitzp.tumat.integration;

import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.EnergyComponent;
import ic2.api.tile.IEnergyStorage;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * @author canitzp
 */
public class IndustrialCraft2 implements IWorldRenderer{

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        if(tileEntity instanceof IEnergyStorage){
            int current = ((IEnergyStorage) tileEntity).getStored();
            int capacity = ((IEnergyStorage) tileEntity).getCapacity();
            if(capacity > 0){
                component.addOneLineRenderer(new EnergyComponent(current, capacity, "EU"));
            }
        }
        return null;
    }
}
