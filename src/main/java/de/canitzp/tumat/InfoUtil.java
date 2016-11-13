package de.canitzp.tumat;

import de.canitzp.tumat.api.ReMapper;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;


/**
 * @author canitzp
 */
public class InfoUtil{

    private static Triple<String, String, String[]> getName(ItemStack stack){
        if(Loader.instance().hasReachedState(LoaderState.AVAILABLE)){
            return getName(stack, RenderOverlay.remaped);
        }
        return Triple.of(null, null, null);
    }

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
        }
        return stack.getRarity().rarityColor + getDebugAddition(stack, displayString);
    }

    public static String[] getDescription(ItemStack stack){
        String[] strings = getDescriptionFromStack(stack);
        if(stack != null && stack.getItem() != null && (strings == null || strings.length == 0)){
            List<String> tooltip = new ArrayList<>();
            if((getStackVisibility(stack) & 32) == 0){
                try{
                    stack.getItem().addInformation(stack, Minecraft.getMinecraft().thePlayer, tooltip, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
                } catch(Exception ignore){}
            }
            tooltip.addAll(getDescriptionAddition(stack));
            if(!tooltip.isEmpty()){
                List<String> s = new ArrayList<>();
                for(String s1 : tooltip){
                    s.addAll(Minecraft.getMinecraft().fontRendererObj.listFormattedStringToWidth(s1, 200));
                }
                if(s.size() > 5){
                    List<String> cached = new ArrayList<>();
                    for(int i = 0; i <= 5; i++){
                        cached.add(s.get(i));
                    }
                    cached.add("To many information to show.");
                    s.clear();
                    s.addAll(cached);
                }
                strings = s.toArray(new String[]{});
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
        if(entityName != null){
            String[] array = entityName.split("\\.");
            if(array.length >= 2) {
                entityName = RenderOverlay.getModName(array[0]);
            } else {
                entityName = "Minecraft";
            }
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

    public static String getDebugAddition(ItemStack stack, String s){
        if(Minecraft.getMinecraft().gameSettings.advancedItemTooltips){
            int i = Item.getIdFromItem(stack.getItem());
            if (stack.getHasSubtypes()) {
                s += String.format("%s#%04d/%d%s", " (", i, stack.getItemDamage(), ")");
            } else {
                s += String.format("%s#%04d%s", " (", i, ")");
            }
        } else if(stack.getItem() instanceof ItemMap){
            s += " #" + stack.getItemDamage();
        }
        return s;
    }

    public static int getStackVisibility(ItemStack stack){
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("HideFlags", 99)) {
            return stack.getTagCompound().getInteger("HideFlags");
        }
        return 0;
    }

    public static List<String> getDescriptionAddition(ItemStack stack) {
        List<String> strings = new ArrayList<>();
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            int visibilityFlag = getStackVisibility(stack);
            if ((visibilityFlag & 1) == 0) {
                NBTTagList nbttaglist = stack.getEnchantmentTagList();
                if (nbttaglist != null) {
                    for (int j = 0; j < nbttaglist.tagCount(); ++j) {
                        int k = nbttaglist.getCompoundTagAt(j).getShort("id");
                        int l = nbttaglist.getCompoundTagAt(j).getShort("lvl");
                        if (Enchantment.getEnchantmentByID(k) != null) {
                            strings.add(Enchantment.getEnchantmentByID(k).getTranslatedName(l));
                        }
                    }
                }
            }

            if (nbt.hasKey("display", 10)) {
                NBTTagCompound nbttagcompound = nbt.getCompoundTag("display");
                if (nbttagcompound.hasKey("color", 3)) {
                    if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips) {
                        strings.add("Color: #" + String.format("%06X", nbttagcompound.getInteger("color")));
                    } else {
                        strings.add(TextFormatting.ITALIC + I18n.translateToLocal("item.dyed"));
                    }
                }
                if (nbttagcompound.getTagId("Lore") == 9) {
                    NBTTagList nbttaglist3 = nbttagcompound.getTagList("Lore", 8);

                    if (!nbttaglist3.hasNoTags()) {
                        for (int l1 = 0; l1 < nbttaglist3.tagCount(); ++l1) {
                            strings.add(TextFormatting.DARK_PURPLE + "" + TextFormatting.ITALIC + nbttaglist3.getStringTagAt(l1));
                        }
                    }
                }
            }
        }
        return strings;
    }

}
