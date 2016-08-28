package de.canitzp.tumat.api;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public interface IWorldRenderer{

    default TooltipComponent renderBlock(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        return component;
    }

    default TooltipComponent renderEntity(WorldClient world, EntityPlayerSP player, Entity entity, TooltipComponent component, boolean shouldCalculate){
        return component;
    }

    default TooltipComponent renderMiss(WorldClient world, EntityPlayerSP player, RayTraceResult trace, TooltipComponent component, boolean shouldCalculate){
        return component;
    }

    default TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        return component;
    }

    default TooltipComponent renderLivingEntity(WorldClient world, EntityPlayerSP player, EntityLivingBase entity, TooltipComponent component, boolean shouldCalculate){
        return component;
    }

    default TooltipComponent renderEntityItem(WorldClient world, EntityPlayerSP player, EntityItem entity, ItemStack stack, TooltipComponent component, boolean shouldCalculate){
        return component;
    }

    default void remap(ReMapper<ItemStack, String, String, String[]> reMapper){}

    default boolean shouldBeActive(){return true;}

}
