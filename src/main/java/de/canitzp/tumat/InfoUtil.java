package de.canitzp.tumat;

import de.canitzp.tumat.configuration.cats.ConfigString;
import de.canitzp.tumat.local.L10n;
import de.canitzp.tumat.network.NetworkHandler;
import de.canitzp.tumat.network.PacketUpdateTileEntity;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * @author canitzp
 */
@SuppressWarnings("ConstantConditions")
@SideOnly(Side.CLIENT)
public class InfoUtil{

    private static final HashMap<String, String> cachedModNames = new HashMap<>();

    public static String getBlockName(IBlockState state){
        Item itemBlock = Item.getItemFromBlock(state.getBlock());
        if(itemBlock != null && itemBlock != Items.AIR){
            return getItemName(new ItemStack(itemBlock, 1, getMetaFromBlock(state)));
        } else {
            return state.getBlock().getLocalizedName();
        }
    }

    public static String getItemName(ItemStack stack){
        return !stack.isEmpty() ? stack.getRarity().color + getDebugAddition(stack, stack.getDisplayName()) : "<Unknown>";
    }

    public static List<String> getDescription(ItemStack stack){
        if(!stack.isEmpty()){
            List<String> desc = new ArrayList<>();
            if((getStackVisibility(stack) & 32) == 0){
                try{
                    stack.getItem().addInformation(stack, Minecraft.getMinecraft().world, desc, Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
                } catch(Exception ignore){}
            }
            desc.addAll(getDescriptionAddition(stack));
            if(!desc.isEmpty()){
                List<String> s = new ArrayList<>();
                for(String s1 : desc){
                    s.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(s1, 200));
                }
                if(s.size() > 2 && !Minecraft.getMinecraft().player.isSneaking()){
                    List<String> cached = new ArrayList<>();
                    for(int i = 0; i <= 1; i++){
                        cached.add(s.get(i));
                    }
                    cached.add(L10n.SNEAKFORMORE);
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
            return ConfigString.MOD_NAME_FORMAT.value + getModName(entry.getRegistryName().getNamespace());
        }
        return ConfigString.MOD_NAME_FORMAT.value + "<Unknown>";
    }

    public static String getModName(Entity entity){
        ResourceLocation entityLoc = EntityList.getKey(entity);
        if(entityLoc != null){
            return ConfigString.MOD_NAME_FORMAT.value + getModName(entityLoc.getNamespace());
        }
        return ConfigString.MOD_NAME_FORMAT.value + "Minecraft";
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
                        strings.add(TextFormatting.ITALIC + I18n.format("item.dyed"));
                    }
                }
                if (nbttagcompound.getTagId("Lore") == 9) {
                    NBTTagList nbttaglist3 = nbttagcompound.getTagList("Lore", 8);

                    if (!nbttaglist3.isEmpty()) {
                        for (int l1 = 0; l1 < nbttaglist3.tagCount(); ++l1) {
                            strings.add(TextFormatting.DARK_PURPLE + "" + TextFormatting.ITALIC + nbttaglist3.getStringTagAt(l1));
                        }
                    }
                }
            }
        }
        return strings;
    }

    public static ItemStack getItemStackInSlot(TileEntity inventory, EnumFacing side, int slot){
        if(inventory.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)){
            return inventory.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).getStackInSlot(slot);
        }
        return ItemStack.EMPTY;
    }

    /**
     * Renders the specified text to the screen, center-aligned.
     */
    public static void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color){
        if(text == null){
            text = "TUMAT NPE Error";
        }
        fontRendererIn.drawStringWithShadow(text, (float) (x - fontRendererIn.getStringWidth(text) / 2), (float) y, color);
    }

    public static String getEntityName(Entity entity){
        String defaultName = entity.getName();
        if(defaultName.endsWith(".name")){
            String[] array = defaultName.split("\\.");
            defaultName = StringUtils.capitalize(array[array.length - 2]);
        }
        return defaultName;
    }

    public static void syncTileEntity(TileEntity tile, boolean shouldCalculate, String... nbtKeys){
        if(shouldCalculate){
            NetworkHandler.network.sendToServer(new PacketUpdateTileEntity(tile.getPos(), nbtKeys));
        }
    }

    public static int getMetaFromBlock(IBlockState state){
        int meta = 0;
        try {
            meta = state.getBlock().getMetaFromState(state);
        } catch (Exception ignored){
            ignored.printStackTrace();
        } // To avoid bugs with doubled plants from OreFlowers since there isn't a size check #7
        return meta;
    }

    public static ItemStack newStackFromBlock(World world, BlockPos pos, IBlockState state, @Nullable EntityPlayerSP player, @Nullable RayTraceResult trace){
        return state.getBlock().getPickBlock(state, trace, world, pos, player);
    }

}
