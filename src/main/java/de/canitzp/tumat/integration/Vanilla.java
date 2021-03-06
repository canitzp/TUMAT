package de.canitzp.tumat.integration;

import de.canitzp.tumat.InfoUtil;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.ColoredText;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import de.canitzp.tumat.local.L10n;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Method;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class Vanilla implements IWorldRenderer{

    private static final Method getAgeProperty = ReflectionHelper.findMethod(BlockCrops.class, "getAgeProperty", "func_185524_e");

    @Override
    public TooltipComponent renderBlock(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        IBlockState state = world.getBlockState(pos);
        //Plants:
        if(state.getBlock() instanceof IPlantable){
            IBlockState plant = ((IPlantable) state.getBlock()).getPlant(world, pos);
            if(plant != null){
                if(ConfigBoolean.SHOW_PLANT_GROWTH_STATUS.value && plant.getBlock() instanceof BlockCrops){
                    try {
                        PropertyInteger prop = (PropertyInteger) getAgeProperty.invoke(plant.getBlock());
                        int plantStatus = state.getValue(prop);
                        float growStatus = Math.round((plantStatus / (prop.getAllowedValues().size()-1 * 1.0F) * 100F) * 100.00F) / 100.00F;
                        ColoredText.createOneLine(component, L10n.getVanillaGrowRate(String.valueOf(growStatus)), ColoredText.Colors.BROWN_PLANT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(plant.getBlock() instanceof BlockDoublePlant && InfoUtil.hasProperty(plant, BlockDoublePlant.HALF)){
                    BlockDoublePlant.EnumBlockHalf half = plant.getValue(BlockDoublePlant.HALF);
                    if(half == BlockDoublePlant.EnumBlockHalf.UPPER){
                        IBlockState down = world.getBlockState(pos.down());
                        component.setName(new TextComponent(InfoUtil.getBlockName(down)));
                    }
                }
            }
        }

        if(ConfigBoolean.SHOW_REDSTONE_STRENGTH.value) {
            int power = state.getWeakPower(world, pos, side);
            if (power > 0 || state.getBlock() instanceof BlockRedstoneWire) {
                ColoredText.createOneLine(component, L10n.getVanillaRedstoneStrength(power), ColoredText.Colors.RED_REDSTONE);
            }
            if(state.getBlock() instanceof BlockRedstoneRepeater){
                ColoredText.createOneLine(component, L10n.getVanillaRedstoneDelay(state.getValue(BlockRedstoneRepeater.DELAY)), ColoredText.Colors.RED_REDSTONE);
                if(state.getValue(BlockRedstoneRepeater.LOCKED)){
                    TextComponent.createOneLine(component, L10n.REDSTONE_LOCKED, TextFormatting.GRAY);
                }
            }
        }

        if(ConfigBoolean.SHOW_LIGHT_LEVEL.value){
            boolean isBlockLightSource = state.getLightValue(world, pos) != 0;
            if(!isBlockLightSource && !world.getBlockState(pos.up()).isFullCube() && !world.getBlockState(pos.up()).isFullBlock()){
                int lightLevel = world.getLightFor(EnumSkyBlock.BLOCK, pos.up());
                TextFormatting canMobsSpawn = world.getWorldTime() % 24000 >= 13000 && lightLevel <= 7 && state.getBlock().canCreatureSpawn(state, world, pos, EntityLiving.SpawnPlacementType.ON_GROUND) ? TextFormatting.RED : TextFormatting.YELLOW;
                TextComponent.createOneLine(component, L10n.getVanillaLight(lightLevel), canMobsSpawn);
            }
            if(isBlockLightSource){
                int lightValue = state.getLightValue(world, pos);
                TextComponent.createOneLine(component, L10n.getVanillaLightSource(lightValue), TextFormatting.YELLOW);
            }
        }

        return component;
    }

    @Override
    public TooltipComponent renderEntity(WorldClient world, EntityPlayerSP player, Entity entity, TooltipComponent component, boolean shouldCalculate) {
        if(entity instanceof MultiPartEntityPart){
            component.setName(new TextComponent(EntityList.getTranslationName(EntityList.getKey(EntityDragon.class))));
        }
        return component;
    }
}
