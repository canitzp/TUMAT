package de.canitzp.tumat.integration;

import de.canitzp.tumat.InfoUtil;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.EnergyComponent;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import de.canitzp.tumat.local.L10n;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class ElectricalUnits implements IWorldRenderer{

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        if(ConfigBoolean.SHOW_EU.value && tileEntity instanceof IEnergyStorage){
            InfoUtil.syncTileEntity(tileEntity, shouldCalculate, "components");
            int current = ((IEnergyStorage) tileEntity).getStored();
            int capacity = ((IEnergyStorage) tileEntity).getCapacity();
            if(capacity > 0){
                component.add(new EnergyComponent(current, capacity, "EU", TextFormatting.YELLOW), TooltipComponent.Priority.HIGH);
            }
        }
        return component;
    }

    @Override
    public TooltipComponent renderBlock(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        IBlockState state = world.getBlockState(pos);
        if(ConfigBoolean.SHOW_HARVESTABILITY.value && state.getBlock() instanceof IWrenchable){
            TextComponent.createOneLine(component, L10n.IC2_WRENCHABLE, TextFormatting.GOLD);
        }
        return component;
    }

    @Override
    public boolean shouldBeActive(){
        return ConfigBoolean.SHOW_EU.value || ConfigBoolean.SHOW_HARVESTABILITY.value;
    }

}
