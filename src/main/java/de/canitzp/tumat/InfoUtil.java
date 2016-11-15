package de.canitzp.tumat;

import de.canitzp.tumat.api.ReMapper;
import de.canitzp.tumat.configuration.cats.ConfigString;
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
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * @author canitzp
 */
@SuppressWarnings("ConstantConditions")
public class InfoUtil{

    private static final HashMap<String, String> cachedModNames = new HashMap<>();

    public static String getBlockName(IBlockState state){
        Item itemBlock = Item.getItemFromBlock(state.getBlock());
        if(itemBlock != null){
            return getItemName(new ItemStack(itemBlock, 1, state.getBlock().getMetaFromState(state)));
        } else {
            return state.getBlock().getLocalizedName();
        }
    }

    public static String getItemName(ItemStack stack){
        return stack != null ? stack.getRarity().rarityColor + getDebugAddition(stack, stack.getDisplayName()) : "<Unknown>";
    }

    public static List<String> getDescription(ItemStack stack){
        if(stack != null && stack.getItem() != null){
            List<String> desc = new ArrayList<>();
            if((getStackVisibility(stack) & 32) == 0){
                try{
                    stack.getItem().addInformation(stack, Minecraft.getMinecraft().thePlayer, desc, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
                } catch(Exception ignore){}
            }
            desc.addAll(getDescriptionAddition(stack));
            if(!desc.isEmpty()){
                List<String> s = new ArrayList<>();
                for(String s1 : desc){
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
                return s;
            }
        }
        return Collections.emptyList();
    }

    private static String getModName(String modid){
        if (cachedModNames.containsKey(modid)) {
            return cachedModNames.get(modid);
        } else {
            if("minecraft".equals(modid) || "Minecraft".equals(modid)){
                return "Minecraft";
            }
            for(ModContainer mod : Loader.instance().getActiveModList()){
                if(mod.getModId().toLowerCase().equals(modid)){
                    String name = mod.getName();
                    cachedModNames.put(modid, name);
                    return name;
                }
            }
            cachedModNames.put(modid, StringUtils.capitalize(modid));
            return StringUtils.capitalize(modid);
        }
    }

    public static String getModName(IForgeRegistryEntry entry){
        if(entry != null && entry.getRegistryName() != null) {
            return ConfigString.MOD_NAME_FORMAT.value + getModName(entry.getRegistryName().getResourceDomain());
        }
        return ConfigString.MOD_NAME_FORMAT.value + "<Unknown>";
    }

    public static String getModName(Entity entity){
        String entityName = EntityList.getEntityString(entity);
        if(entityName != null){
            String[] array = entityName.split("\\.");
            if(array.length >= 2) {
                entityName = getModName(array[0]);
            } else {
                entityName = "Minecraft";
            }
        } else {
            entityName = "Minecraft";
        }
        return ConfigString.MOD_NAME_FORMAT.value + getModName(entityName);
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
