package de.canitzp.tumat.integration;

import de.canitzp.tumat.Config;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.ReMapper;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import de.ellpeck.actuallyadditions.mod.blocks.InitBlocks;
import de.ellpeck.actuallyadditions.mod.tile.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class ActuallyAdditions implements IWorldRenderer{

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        if(tileEntity instanceof TileEntityAtomicReconstructor){
            ItemStack lens = ((IInventory) tileEntity).getStackInSlot(0);
            String s;
            if(lens != null){
                s = "Lens: " + lens.getDisplayName();
            } else {
                s = "No Lens";
            }
            component.addOneLineRenderer(new TextComponent(TextFormatting.YELLOW + s));
        } else if(tileEntity instanceof TileEntityCompost){
            TooltipComponent.syncTileEntity(tileEntity, shouldCalculate && ((IInventory) tileEntity).getStackInSlot(0) != null, "ConversionTime");
            int time = ((TileEntityCompost) tileEntity).conversionTime;
            component.addOneLineRenderer(new TextComponent(TextFormatting.AQUA.toString() + time + "/3000 Ticks"));
        } else if(tileEntity instanceof TileEntityDisplayStand){
            ItemStack stack = ((IInventory) tileEntity).getStackInSlot(0);
            if(stack != null){
                component.addOneLineRenderer(new TextComponent(TextFormatting.AQUA.toString() + stack.getDisplayName()));
            }
        } else if(tileEntity instanceof TileEntitySmileyCloud){
            if(!Objects.equals(((TileEntitySmileyCloud) tileEntity).name, ""))
                component.addOneLineRenderer(new TextComponent(TextFormatting.AQUA.toString() + ((TileEntitySmileyCloud) tileEntity).name));
        }
        return component;
    }

    @Override
    public boolean shouldBeActive(){
        return Config.showSpecialAbilities;
    }
}
