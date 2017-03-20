package de.canitzp.tumat.integration;

import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import de.canitzp.tumat.local.L10n;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
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
                        TextComponent.createOneLine(component, L10n.getInventoryFreeSlots(freeSlots, handler.getSlots()), TextFormatting.GOLD);
                        if(handler.getSlots() > 1000){
                            TextComponent.createOneLine(component, L10n.INVENTORY_LAG_WARN, TextFormatting.RED);
                        }
                    } else if(handler.getSlots() > 1000){
                        TextComponent.createOneLine(component, L10n.INVENTORY_OVER_1000, TextFormatting.GOLD);
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
