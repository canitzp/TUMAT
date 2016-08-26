package de.canitzp.tumat.api;

import de.canitzp.tumat.RenderOverlay;
import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.network.NetworkHandler;
import de.canitzp.tumat.network.PacketUpdateTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * @author canitzp
 */
public class TooltipComponent{

    private List<List<IComponentRender>> objects = new ArrayList<>();
    private List<IComponentRender> currentObjects = new ArrayList<>();
    private boolean shouldShowModName = true;

    public TooltipComponent addRenderer(IComponentRender render){
        this.currentObjects.add(render);
        return this;
    }

    public TooltipComponent addOneLineRenderer(IComponentRender renderer){
        return this.addRenderer(renderer).newLine();
    }

    public TooltipComponent newLine(){
        if(!this.currentObjects.isEmpty()){
            this.objects.add(new ArrayList<>(this.currentObjects));
            this.currentObjects.clear();
        }
        return this;
    }

    public List<List<IComponentRender>> endComponent(){
        this.objects.add(new ArrayList<>(this.currentObjects));
        return this.objects;
    }

    public TooltipComponent addOwnModName(){
        this.shouldShowModName = false;
        return this;
    }

    public boolean shouldShowModName(){
        return shouldShowModName;
    }

    public TooltipComponent clear(){
        this.objects.clear();
        this.currentObjects.clear();
        return this;
    }

    /**
     * Renders the specified text to the screen, center-aligned. Args : renderer, string, x, y, color
     */
    public static void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color){
        if(text == null){
            text = "TUMAT NPE Error";
        }
        fontRendererIn.drawStringWithShadow(text, (float) (x - fontRendererIn.getStringWidth(text) / 2), (float) y, color);
    }

    public static String getBlockName(IBlockState state){
        Item itemBlock = Item.getItemFromBlock(state.getBlock());
        if(itemBlock != null){
            ItemStack stack = new ItemStack(itemBlock, 1, state.getBlock().getMetaFromState(state));
            String name = getName(stack, RenderOverlay.remapMappings).getKey();
            return name != null ? name : getItemStackDisplayString(stack);
        } else {
            return state.getBlock().getLocalizedName();
        }
    }

    public static String getItemStackDisplayString(ItemStack stack){
        String displayString = getName(stack, RenderOverlay.remapMappings).getKey();
        if(displayString == null){
            displayString = stack.getDisplayName();
        } else {
            //Maybe add this later if I know how to remove and readd this at last/first render tick
            //stack.setStackDisplayName(displayString);
        }
        return displayString;
    }

    public static String getEntityName(Entity entity){
        String defaultName = entity.getName();
        if(defaultName.endsWith(".name")){
            String[] array = defaultName.split("\\.");
            defaultName = StringUtils.capitalize(array[array.length-2]);
        }
        return defaultName;
    }

    public static void syncTileEntity(TileEntity tile, boolean shouldCalculate, String... nbtKeys){
        if(shouldCalculate){
            NetworkHandler.network.sendToServer(new PacketUpdateTileEntity(tile.getPos(), nbtKeys));
        }
    }

    public static Pair<String, Pair<String, String[]>> getName(ItemStack stack, Map<ItemStack, Pair<String, Pair<String, String[]>>> map){
        for(ItemStack s : map.keySet()){
            if(ItemStack.areItemsEqual(s, stack)){
                return map.get(s);
            }
        }
        return Pair.of(null, Pair.of(null, null));
    }

    public static void showModNameSpecial(Block block, TooltipComponent component){
        if(block != null){
            component.addOneLineRenderer(new TextComponent(RenderOverlay.modNameFormat + RenderOverlay.getModName(block.getRegistryName().getResourceDomain())));
        }
    }

    public static String[] getDescription(ItemStack stack){
        String[] strings = getName(stack, RenderOverlay.remapMappings).getValue().getValue();
        if(strings == null || strings.length == 0){
            List<String> tooltip = new ArrayList<>();
            stack.getItem().addInformation(stack, null, tooltip, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
            if(!tooltip.isEmpty()){
                strings = (String[]) tooltip.toArray();
            }
        }
        if(strings != null && strings.length > 0){
            return strings;
        }
        return null;
    }

    public static String getAdvancedName(ItemStack stack){
        String s = " (";
        String s1 = ")";

        int i = Item.getIdFromItem(stack.getItem());
        if (stack.getHasSubtypes())
        {
            s = s + String.format("#%04d/%d%s", i, stack.getItemDamage(), s1);
        }
        else
        {
            s = s + String.format("#%04d%s", i, s1);
        }
        return s;
    }

}
