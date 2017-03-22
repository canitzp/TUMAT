package de.canitzp.tumat.integration;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.inventory.DrawerItemHandler;
import de.canitzp.tumat.InfoUtil;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.DescriptionComponent;
import de.canitzp.tumat.api.components.ScaledTextComponent;
import de.canitzp.tumat.local.L10n;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author canitzp
 */
public class StorageDrawers implements IWorldRenderer {

    @Override
    public TooltipComponent renderTileEntity(WorldClient world, EntityPlayerSP player, TileEntity tileEntity, EnumFacing side, TooltipComponent component, boolean shouldCalculate) {
        if(tileEntity instanceof TileEntityDrawers && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)){
            if(player.isSneaking()){
                IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
                if(handler instanceof DrawerItemHandler){
                    List<String> lines = new ArrayList<>();
                    for(int i = 1; i < handler.getSlots(); i++){
                        ItemStack stack = handler.getStackInSlot(i);
                        if(!stack.isEmpty()){
                            lines.add(L10n.getStorageDrawersContent(i, InfoUtil.getItemName(stack)));
                        } else {
                            lines.add(L10n.getStorageDrawersContent(i, TextFormatting.WHITE + L10n.EMPTY));
                        }
                    }
                    component.addRenderer(new DescriptionComponent(lines));
                }
            } else {
                ScaledTextComponent.createOneLine(component, 0.8F, L10n.SNEAKFORMORE, TextFormatting.GRAY);
            }
        }
        return component;
    }

}
