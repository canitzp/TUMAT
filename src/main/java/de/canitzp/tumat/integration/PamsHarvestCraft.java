/*package de.canitzp.tumat.integration;

import com.pam.harvestcraft.blocks.growables.PamCropGrowable;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import de.canitzp.tumat.local.L10n;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * @author canitzp
 */
/*
public class PamsHarvestCraft implements IWorldRenderer {

    @Override
    public TooltipComponent renderBlock(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, TooltipComponent component, boolean shouldCalculate) {
        IBlockState state = world.getBlockState(pos);
        if(ConfigBoolean.SHOW_PLANT_GROWTH_STATUS.value && state.getBlock() instanceof PamCropGrowable && !(state.getBlock() instanceof BlockCrops)){
            PropertyInteger stage = ((PamCropGrowable) state.getBlock()).getAgeProperty();
            float growStatus = Math.round(((state.getValue(stage) * 1.0F / ((PamCropGrowable) state.getBlock()).getMatureAge() * 1.0F) * 100F) * 100.00F) / 100.00F;
            component.add(new TextComponent(L10n.getVanillaGrowRate(String.valueOf(growStatus))).setColor(0x00A97D15), TooltipComponent.Priority.HIGH);
        }
        return component;
    }

}
*/