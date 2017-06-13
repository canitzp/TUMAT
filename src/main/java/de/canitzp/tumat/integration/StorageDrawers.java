package de.canitzp.tumat.integration;

import de.canitzp.tumat.api.IWorldRenderer;

/**
 * @author canitzp
 */
public class StorageDrawers implements IWorldRenderer {

    /*
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
                    component.add(new DescriptionComponent(lines), TooltipComponent.Priority.HIGH);
                }
            } else {
                component.add(new TextComponent(L10n.SNEAKFORMORE).setScale(0.8F).setFormat(TextFormatting.GRAY), TooltipComponent.Priority.HIGH);
            }
        }
        return component;
    }
    */

}
