package de.canitzp.tumat;

import de.canitzp.tumat.api.ReMapper;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;


/**
 * @author canitzp
 */
public class InfoUtil{

    private static String getNameFromStack(ItemStack stack){
        return getName(stack, RenderOverlay.remaped).getLeft();
    }

    private static String getModNameFromStack(ItemStack stack){
        return getName(stack, RenderOverlay.remaped).getMiddle();
    }

    private static String[] getDescriptionFromStack(ItemStack stack){
        return getName(stack, RenderOverlay.remaped).getRight();
    }

    public static String getBlockName(IBlockState state){
        Item itemBlock = Item.getItemFromBlock(state.getBlock());
        if(itemBlock != null){
            ItemStack stack = new ItemStack(itemBlock, 1, state.getBlock().getMetaFromState(state));
            String name = getNameFromStack(stack);
            return name != null ? stack.getRarity().rarityColor + name : getItemName(stack);
        } else {
            return state.getBlock().getLocalizedName();
        }
    }

    public static String getItemName(ItemStack stack){
        String displayString = getNameFromStack(stack);
        if(displayString == null){
            displayString = stack.getDisplayName();
        } else {
            //Maybe add this later if I know how to remove and readd this at last/first render tick
            //stack.setStackDisplayName(displayString);
        }
        return stack.getRarity().rarityColor + displayString;
    }

    public static String[] getDescription(ItemStack stack){
        String[] strings = getDescriptionFromStack(stack);
        if(stack != null && stack.getItem() != null && (strings == null || strings.length == 0)){
            List<String> tooltip = new ArrayList<>();
            try{
                stack.getItem().addInformation(stack, Minecraft.getMinecraft().thePlayer, tooltip, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
            } catch(Exception ignore){
            }
            if(!tooltip.isEmpty()){
                strings = tooltip.toArray(new String[]{});
            }
        }
        if(strings != null && strings.length > 0){
            return strings;
        }
        return null;
    }

    public static String getModNameFromBlock(Block block){
        if(block != null && block.getRegistryName() != null){
            try{
                return RenderOverlay.modNameFormat + RenderOverlay.getModName(block.getRegistryName().getResourceDomain());
            } catch(NullPointerException e){
                return "An Error occurred while rendering " + block.toString();
            }
        }
        return "<Unknown>";
    }

    public static String getModName(ItemStack stack){
        String modName = InfoUtil.getModNameFromStack(stack);
        if(modName == null && stack != null && stack.getItem() != null){
            modName = RenderOverlay.getModName(stack.getItem().getRegistryName().getResourceDomain());
        }
        return modName != null ? RenderOverlay.modNameFormat + modName : null;
    }

    public static String getModName(Entity entity){
        String entityName = EntityList.getEntityString(entity);
        String[] array = entityName.split("\\.");
        if(array.length >= 2){
            entityName = RenderOverlay.getModName(array[0]);
        } else {
            entityName = "Minecraft";
        }
        return RenderOverlay.modNameFormat + RenderOverlay.getModName(entityName);
    }

    public static Triple<String, String, String[]> getName(ItemStack stack, ReMapper<ItemStack, String, String, String[]> remapper){
        for(ItemStack s : remapper.getKeys()){
            if(ItemStack.areItemsEqual(s, stack)){
                return remapper.getValue(s);
            }
        }
        return Triple.of(null, null, null);
    }

    public static boolean hasProperty(IBlockState state, IProperty<?> property){
        return state.getProperties().get(property) != null;
    }

}
