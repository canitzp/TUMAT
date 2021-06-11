package de.canitzp.tumat.api;

import de.canitzp.tumat.IconRenderer;
import de.canitzp.tumat.InfoUtil;
import me.shedaniel.architectury.event.events.BlockEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author canitzp
 */
@Environment(EnvType.CLIENT)
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
    default TooltipComponent renderBlock(ClientLevel world, LocalPlayer player, BlockPos pos, Direction side, TooltipComponent component, boolean shouldCalculate){
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
    default TooltipComponent renderEntity(ClientLevel world, LocalPlayer player, Entity entity, TooltipComponent component, boolean shouldCalculate){
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
    default TooltipComponent renderMiss(ClientLevel world, LocalPlayer player, HitResult trace, TooltipComponent component, boolean shouldCalculate){
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
    default TooltipComponent renderTileEntity(ClientLevel world, LocalPlayer player, BlockEntity tileEntity, Direction side, TooltipComponent component, boolean shouldCalculate){
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
    default TooltipComponent renderLivingEntity(ClientLevel world, LocalPlayer player, LivingEntity entity, TooltipComponent component, boolean shouldCalculate){
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
    default TooltipComponent renderEntityItem(ClientLevel world, LocalPlayer player, ItemEntity entity, ItemStack stack, TooltipComponent component, boolean shouldCalculate){
        return component;
    }

    //TODO add javadoc
    @Nullable
    default IconRenderer getIconRenderObject(ClientLevel world, LocalPlayer player, BlockPos pos, Direction side, HitResult trace, boolean shouldCalculate){
        BlockState state = world.getBlockState(pos);
        return new IconRenderer(state.getBlock().asItem().getDefaultInstance());
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
    default int getGuiLeftOffset(ContainerScreen gui){return 0;}

    /**
     * This is for the slot overlay, cause some guis are shifted to a side
     * @param gui The gui
     * @return How much is the gui shifted to the top
     */
    default int getGuiTopOffset(ContainerScreen gui){return 0;}

    /**
     * @return a map of the modid and the color for the energy
     */
    default Map<String, String> getEnergyColor(){return new HashMap<>();}

    /**
     * This method gets called inside the TUMAT postInit.
     */
    default void init(){}

    /**
     * This is made cause iChunUtil does add a entity INTO the player
     * @return a list of all entity classes names that should be ignored
     */
    default List<String> getInvisibleEntities(){
        return new ArrayList<>();
    }

}
