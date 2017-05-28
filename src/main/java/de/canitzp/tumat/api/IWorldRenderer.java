package de.canitzp.tumat.api;

import de.canitzp.tumat.IconRenderer;
import de.canitzp.tumat.InfoUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public interface IWorldRenderer{

    /**
     * This method gets called if the player is looking at a block, within a certain distance
     * @param world The world
     * @param player The player
     * @param pos The looking position
     * @param side The side
     * @param component The current TooltipComponent
     * @param shouldCalculate Should be calculated within this method
     * @return A modified TooltipComponent
     */
    default TooltipComponent renderBlock(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        return component;
    }

    /**
     * This method gets called if the player is looking at a not living and not item entity, within a certain distance
     * @param world The world
     * @param player The player
     * @param entity The entity the player is looking at
     * @param component The current TooltipComponent
     * @param shouldCalculate Should be calculated within this method
     * @return A modified TooltipComponent
     */
    default TooltipComponent renderEntity(WorldClient world, EntityPlayerSP player, Entity entity, TooltipComponent component, boolean shouldCalculate){
        return component;
    }

    /**
     * This method gets called if the player is looking at nothing or special blocks like fluids
     * @param world The world
     * @param player The player
     * @param trace The clean RayTraceResult TUMAT works with
     * @param component The current TooltipComponent
     * @param shouldCalculate Should be calculated within this method
     * @return A modified TooltipComponent
     */
    default TooltipComponent renderMiss(WorldClient world, EntityPlayerSP player, RayTraceResult trace, TooltipComponent component, boolean shouldCalculate){
        return component;
    }

    /**
     * This method gets called if the player is looking at a block that have a active tile entity, within a certain distance
     * @param world The world
     * @param player The player
     * @param tileEntity The tileEntity
     * @param side The side
     * @param component The current TooltipComponent
     * @param shouldCalculate Should be calculated within this method
     * @return A modified TooltipComponent
     */
    default TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        return component;
    }

    /**
     * This method gets called if the player is looking at a living entity, within a certain distance
     * @param world The world
     * @param player The player
     * @param entity The entity the player is looking at
     * @param component The current TooltipComponent
     * @param shouldCalculate Should be calculated within this method
     * @return A modified TooltipComponent
     */
    default TooltipComponent renderLivingEntity(WorldClient world, EntityPlayerSP player, EntityLivingBase entity, TooltipComponent component, boolean shouldCalculate){
        return component;
    }

    /**
     * This method gets called if the player is looking at a item entity, within a certain distance
     * @param world The world
     * @param player The player
     * @param entity The entity the player is looking at
     * @param component The current TooltipComponent
     * @param shouldCalculate Should be calculated within this method
     * @return A modified TooltipComponent
     */
    default TooltipComponent renderEntityItem(WorldClient world, EntityPlayerSP player, EntityItem entity, ItemStack stack, TooltipComponent component, boolean shouldCalculate){
        return component;
    }

    //TODO add javadoc
    @Nullable
    default IconRenderer getIconRenderObject(WorldClient world, EntityPlayerSP player, BlockPos pos, EnumFacing side, RayTraceResult trace, boolean shouldCalculate){
        IBlockState state = world.getBlockState(pos);
        return new IconRenderer(InfoUtil.newStackFromBlock(world, pos, state, player, trace));
    }

    /**
     * @return Should the renderer be active. Per example to check a config option.
     */
    default boolean shouldBeActive(){
        return true;
    }

    /**
     * This is for the slot overlay, cause some guis are shifted to a side
     * @param gui The gui
     * @return How much is the gui shifted to the left
     */
    default int getGuiLeftOffset(GuiContainer gui){return 0;}

    /**
     * This is for the slot overlay, cause some guis are shifted to a side
     * @param gui The gui
     * @return How much is the gui shifted to the top
     */
    default int getGuiTopOffset(GuiContainer gui){return 0;}

    /**
     * @return a map of the modid and the color for the energy
     */
    @Nonnull
    default Map<String, String> getEnergyColor(){return new HashMap<>();}

    /**
     * This method gets called inside the TUMAT postInit.
     */
    default void init(){}

}
