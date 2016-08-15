package de.canitzp.tumat.api;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public interface IWorldRenderer{

    default TooltipComponent renderBlock(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        return null;
    }

    default TooltipComponent renderEntity(WorldClient world, EntityPlayerSP player, Entity entity, TooltipComponent component){
        return null;
    }

    default TooltipComponent renderMiss(WorldClient world, EntityPlayerSP player, RayTraceResult trace, TooltipComponent component){
        return null;
    }

    default TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        return null;
    }

    default TooltipComponent renderLivingEntity(WorldClient world, EntityPlayerSP player, EntityLivingBase entity, TooltipComponent component){
        return null;
    }

}
