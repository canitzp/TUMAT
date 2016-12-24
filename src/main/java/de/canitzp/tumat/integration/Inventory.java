package de.canitzp.tumat.integration;

import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * @author canitzp
 */
public class Inventory implements IWorldRenderer{

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate){
        if(tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)){
            if(tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)){
                IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
                if(handler != null){
                    if((handler.getSlots() > 0 && handler.getSlots() < 1001) || (player.isSneaking() && handler.getSlots() > 0)){
                        int freeSlots = 0;
                        for(int i = 0; i < handler.getSlots(); i++){
                            if(handler.getStackInSlot(i) == ItemStack.EMPTY){
                                freeSlots++;
                            }
                        }
                        component.addOneLineRenderer(new TextComponent("Free slots: " + freeSlots + " / " + handler.getSlots()).setFormat(TextFormatting.GOLD));
                        if(handler.getSlots() > 1000){
                            component.addOneLineRenderer(new TextComponent("Showing this may produce lag!").setFormat(TextFormatting.RED));
                        }
                    } else if(handler.getSlots() > 1000){
                        component.addOneLineRenderer(new TextComponent("Over 1000 slots. Sneak to show anyway.").setFormat(TextFormatting.GOLD));
                    }
                }
            }
        }
        return component;
    }

    @Override
    public boolean shouldBeActive(){
        return ConfigBoolean.SHOW_INVENTORY_STATUS.value;
    }
}
