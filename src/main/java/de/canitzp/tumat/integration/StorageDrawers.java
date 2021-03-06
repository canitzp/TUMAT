package de.canitzp.tumat.integration;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import de.canitzp.tumat.InfoUtil;
import de.canitzp.tumat.api.IWorldRenderer;
import de.canitzp.tumat.api.TooltipComponent;
import de.canitzp.tumat.api.components.DescriptionComponent;
import de.canitzp.tumat.api.components.TextComponent;
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
        if(player.isSneaking() && tileEntity instanceof TileEntityDrawers){
            if(tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)){
                IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
                if(handler != null){
                    List<String> lines = new ArrayList<>();
                    for(int i = 1; i < handler.getSlots(); i++){
                        ItemStack stack = handler.getStackInSlot(i);
                        if(!stack.isEmpty()){
                            lines.add(L10n.getStorageDrawersContent(i, InfoUtil.getItemName(stack)));
                        } else {
                            lines.add(L10n.getStorageDrawersContent(i, TextFormatting.WHITE + L10n.EMPTY));
                        }
                    }
                    component.add(new DescriptionComponent(lines), TooltipComponent.Priority.HIGH);
                }
            } else {
                component.add(new TextComponent(L10n.SNEAKFORMORE).setScale(0.8F).setFormat(TextFormatting.GRAY), TooltipComponent.Priority.HIGH);
            }
        }
        return component;
    }


}
