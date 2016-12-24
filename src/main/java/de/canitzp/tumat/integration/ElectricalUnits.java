package de.canitzp.tumat.integration;

import de.canitzp.tumat.InfoUtil;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.EnergyComponent;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

/**
 * @author canitzp
 */
public class ElectricalUnits implements IWorldRenderer{

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        if(ConfigBoolean.SHOW_EU.value && tileEntity instanceof IEnergyStorage){
            InfoUtil.syncTileEntity(tileEntity, shouldCalculate, "components");
            int current = ((IEnergyStorage) tileEntity).getStored();
            int capacity = ((IEnergyStorage) tileEntity).getCapacity();
            if(capacity > 0){
                component.addOneLineRenderer(new EnergyComponent(current, capacity, "EU", TextFormatting.YELLOW));
            }
        }
        return component;
    }

    @Override
    public TooltipComponent renderBlock(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        IBlockState state = world.getBlockState(pos);
        if(ConfigBoolean.SHOW_HARVESTABILITY.value && state.getBlock() instanceof IWrenchable){
            component.addOneLineRenderer(new TextComponent(TextFormatting.GOLD + "IC2 Wrenchable"));
        }
        return component;
    }

    @Override
    public boolean shouldBeActive(){
        return ConfigBoolean.SHOW_EU.value || ConfigBoolean.SHOW_HARVESTABILITY.value;
    }

}
