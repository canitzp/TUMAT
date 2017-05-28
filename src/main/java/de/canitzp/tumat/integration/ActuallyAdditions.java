package de.canitzp.tumat.integration;

import de.canitzp.tumat.InfoUtil;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import de.ellpeck.actuallyadditions.mod.tile.TileEntityCompost;
import de.ellpeck.actuallyadditions.mod.tile.TileEntityDisplayStand;
import de.ellpeck.actuallyadditions.mod.tile.TileEntitySmileyCloud;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class ActuallyAdditions implements IWorldRenderer{

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        if(tileEntity instanceof TileEntityCompost){
            InfoUtil.syncTileEntity(tileEntity, shouldCalculate && InfoUtil.getItemStackInSlot(tileEntity, side, 0) != ItemStack.EMPTY, "ConversionTime");
            int time = ((TileEntityCompost) tileEntity).conversionTime;
            component.add(new TextComponent(TextFormatting.AQUA.toString() + time + "/3000 Ticks"), TooltipComponent.Priority.HIGH);
        } else if(tileEntity instanceof TileEntityDisplayStand){
            ItemStack stack = InfoUtil.getItemStackInSlot(tileEntity, side, 0);
            if(stack != ItemStack.EMPTY){
                component.add(new TextComponent(TextFormatting.AQUA.toString() + stack.getDisplayName()), TooltipComponent.Priority.HIGH);
            }
        } else if(tileEntity instanceof TileEntitySmileyCloud){
            if(!Objects.equals(((TileEntitySmileyCloud) tileEntity).name, ""))
                component.add(new TextComponent(TextFormatting.AQUA.toString() + ((TileEntitySmileyCloud) tileEntity).name), TooltipComponent.Priority.HIGH);
        }
        return component;
    }

    @Override
    public boolean shouldBeActive(){
        return ConfigBoolean.SHOW_SPECIAL_TILE_STATS.value;
    }

    @Nonnull
    @Override
    public Map<String, String> getEnergyColor(){
        Map<String, String> map = new HashMap<>();
        map.put("actuallyadditions", "CF");
        return map;
    }

}
