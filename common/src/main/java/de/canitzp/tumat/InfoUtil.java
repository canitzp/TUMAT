package de.canitzp.tumat;

import com.mojang.blaze3d.vertex.PoseStack;
import de.canitzp.tumat.configuration.cats.ConfigString;
import de.canitzp.tumat.local.L10n;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * @author canitzp
 */
@Environment(EnvType.CLIENT)
public class InfoUtil{

    private static final HashMap<String, String> cachedModNames = new HashMap<>();

    public static MutableComponent getBlockName(BlockState state){
        Item itemBlock = state.getBlock().asItem();
        if(itemBlock != null && itemBlock != Items.AIR){
            return getItemName(new ItemStack(itemBlock, 1));
        } else {
            return state.getBlock().getName();
        }
    }

    public static MutableComponent getItemName(ItemStack stack){
        return !stack.isEmpty() ? getDebugAddition(stack, (MutableComponent) stack.getDisplayName()).withStyle(stack.getRarity().color) : new TextComponent("<Unknown>");
    }

    public static List<FormattedText> getDescription(ItemStack stack){
        if(!stack.isEmpty()){
            List<Component> desc = new ArrayList<>();
            if((getStackVisibility(stack) & 32) == 0){
                try{
                    stack.getItem().appendHoverText(stack, Minecraft.getInstance().level, desc, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
                } catch(Exception ignore){}
            }
            desc.addAll(getDescriptionAddition(stack));
            if(!desc.isEmpty()){
                List<FormattedText> formattedDesc = new ArrayList<>();
                for(Component component : desc){
                    formattedDesc.addAll(Minecraft.getInstance().font.getSplitter().splitLines(component, 200, Style.EMPTY));
                }
                if(formattedDesc.size() > 2 && !Minecraft.getInstance().player.isShiftKeyDown()){
                    List<FormattedText> cached = new ArrayList<>();
                    for(int i = 0; i <= 1; i++){
                        cached.add(formattedDesc.get(i));
                    }
                    cached.add(new TranslatableComponent(L10n.SNEAKFORMORE));
                    formattedDesc.clear();
                    formattedDesc.addAll(cached);
                }
                return formattedDesc;
            }
        }
        return Collections.emptyList();
    }

    public static MutableComponent getModName(ItemStack stack){
        if(!stack.isEmpty()){
            ResourceLocation itemKey = Registry.ITEM.getKey(stack.getItem());
            // todo find modname for modid, instead of returning the modid!
            return new TextComponent(itemKey.getNamespace());
        }
        return new TextComponent("Empty").withStyle(Style.EMPTY.withItalic(true));
    }

    public static MutableComponent getModName(Entity entity){
        if(entity != null){
            ResourceLocation entityTypeKey = Registry.ENTITY_TYPE.getKey(entity.getType());
            // todo find modname for modid, instead of returning the modid!
            return new TextComponent(entityTypeKey.getNamespace());
        }
        return new TextComponent("<Unknown>");
    }

    public static boolean hasProperty(BlockState state, Property<?> property){
        return state.getProperties().contains(property);
    }

    public static MutableComponent getDebugAddition(ItemStack stack, MutableComponent s){
        if(Minecraft.getInstance().options.advancedItemTooltips){
            ResourceLocation itemKey = Registry.ITEM.getKey(stack.getItem());
            s.append(String.format(" (#%s)", itemKey.toString()));
        }
        return s;
    }

    public static int getStackVisibility(ItemStack stack){
        if (stack.hasTag() && stack.getTag().contains("HideFlags", 99)) {
            return stack.getTag().getInt("HideFlags");
        }
        return 0;
    }

    public static List<Component> getDescriptionAddition(ItemStack stack) {
        List<Component> strings = new ArrayList<>();
        if (stack.hasTag()) {
            CompoundTag nbt = stack.getTag();
            int visibilityFlag = getStackVisibility(stack);
            if ((visibilityFlag & 1) == 0) {
                ListTag nbttaglist = stack.getEnchantmentTags();
                if (nbttaglist != null) {
                    for (int j = 0; j < nbttaglist.size(); ++j) {
                        int k = nbttaglist.getCompound(j).getShort("id");
                        int l = nbttaglist.getCompound(j).getShort("lvl");
                        if (Enchantment.byId(k) != null) {
                            strings.add(Enchantment.byId(k).getFullname(l));
                        }
                    }
                }
            }

            if (nbt.contains("display", 10)) {
                CompoundTag nbttagcompound = nbt.getCompound("display");
                if (nbttagcompound.contains("color", 3)) {
                    if (Minecraft.getInstance().options.advancedItemTooltips) {
                        strings.add(new TextComponent("Color: #" + String.format("%06X", nbttagcompound.getInt("color"))));
                    } else {
                        strings.add(new TranslatableComponent("item.dyed").withStyle(Style.EMPTY.withItalic(true)));
                    }
                }
                if (nbttagcompound.getTagType("Lore") == 9) {
                    ListTag nbttaglist3 = nbttagcompound.getList("Lore", 8);

                    if (!nbttaglist3.isEmpty()) {
                        for (int l1 = 0; l1 < nbttaglist3.size(); ++l1) {
                            strings.add(new TextComponent(nbttaglist3.getString(l1)).withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.DARK_PURPLE)));
                        }
                    }
                }
            }
        }
        return strings;
    }

    /**
     * Renders the specified text to the screen, center-aligned.
     */
    public static void drawCenteredString(PoseStack pose, Font font, String text, int x, int y, int color){
        if(text == null){
            text = "TUMAT NPE Error";
        }
        font.drawShadow(pose, text, (float) (x - font.width(text) / 2), (float) y, color);
    }

    public static String getEntityName(Entity entity){
        String defaultName = entity.getName().getString();
        if(defaultName.endsWith(".name")){
            String[] array = defaultName.split("\\.");
            defaultName = StringUtils.capitalize(array[array.length - 2]);
        }
        return defaultName;
    }

}
