package de.canitzp.tumat.integration;

import de.canitzp.tumat.api.IWorldRenderer;

/**
 * @author canitzp
 */
public class PamsHarvestCraft implements IWorldRenderer {

    /*
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
    */
}
