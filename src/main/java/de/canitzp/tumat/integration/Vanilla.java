package de.canitzp.tumat.integration;

import de.canitzp.tumat.InfoUtil;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import net.minecraft.block.BlockBeetroot;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.IPlantable;

/**
 * @author canitzp
 */
public class Vanilla implements IWorldRenderer{

    @Override
    public TooltipComponent renderBlock(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        IBlockState state = world.getBlockState(pos);
        //Plants:
        if(state.getBlock() instanceof IPlantable){
            IBlockState plant = ((IPlantable) state.getBlock()).getPlant(world, pos);
            if(plant != null){
                if(ConfigBoolean.SHOW_PLANT_GROWTH_STATUS.value && plant.getBlock() instanceof BlockCrops && InfoUtil.hasProperty(plant, BlockCrops.AGE)){
                    int plantStatus = plant.getValue(BlockCrops.AGE);
                    float growStatus = Math.round((plantStatus / 7F * 100F) * 100.00F) / 100.00F;
                    component.addOneLineRenderer(new TextComponent("Grow status: " + growStatus + "%").setFormat(TextFormatting.YELLOW));
                }
                if(plant.getBlock() instanceof BlockDoublePlant && InfoUtil.hasProperty(plant, BlockDoublePlant.HALF)){
                    BlockDoublePlant.EnumBlockHalf half = plant.getValue(BlockDoublePlant.HALF);
                    if(half == BlockDoublePlant.EnumBlockHalf.UPPER){
                        IBlockState down = world.getBlockState(pos.down());
                        component.clear();
                        component.addOneLineRenderer(new TextComponent(InfoUtil.getBlockName(down)));
                    }
                }
            }
        }
        if(ConfigBoolean.SHOW_PLANT_GROWTH_STATUS.value && state.getBlock() instanceof BlockBeetroot && InfoUtil.hasProperty(state, BlockBeetroot.BEETROOT_AGE)){
            int plantStatus = state.getValue(BlockBeetroot.BEETROOT_AGE);
            float growStatus = Math.round((plantStatus / 3F * 100F) * 100.00F) / 100.00F;
            component.addOneLineRenderer(new TextComponent("Grow status: " + growStatus + "%").setFormat(TextFormatting.YELLOW));
        }

        if(ConfigBoolean.SHOW_REDSTONE_STRENGTH.value) {
            int power = state.getWeakPower(world, pos, side);
            if (power > 0 || state.getBlock() instanceof BlockRedstoneWire) {
                component.addOneLineRenderer(new TextComponent("Power: " + TextFormatting.DARK_RED + power));
            }
        }

        if(ConfigBoolean.SHOW_LIGHT_LEVEL.value){
            boolean isBlockLightSource = state.getLightValue(world, pos) != 0;
            if(!isBlockLightSource && !world.getBlockState(pos.up()).isFullCube() && !world.getBlockState(pos.up()).isFullBlock()){
                int lightLevel = world.getLightFor(EnumSkyBlock.BLOCK, pos.up());
                String canMobsSpawn = world.getWorldTime() % 24000 >= 13000 && lightLevel <= 7 && state.getBlock().canCreatureSpawn(state, world, pos, EntityLiving.SpawnPlacementType.ON_GROUND) ? TextFormatting.RED.toString() : TextFormatting.YELLOW.toString();
                component.addOneLineRenderer(new TextComponent("Light: " + canMobsSpawn + lightLevel));
            }
            if(isBlockLightSource){
                int lightValue = state.getLightValue(world, pos);
                component.addOneLineRenderer(new TextComponent("Light source: " + TextFormatting.YELLOW + lightValue));
            }
        }

        return component;
    }

}
