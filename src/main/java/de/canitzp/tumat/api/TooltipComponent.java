package de.canitzp.tumat.api;

import de.canitzp.tumat.RenderOverlay;
import de.canitzp.tumat.network.NetworkHandler;
import de.canitzp.tumat.network.PacketUpdateTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author canitzp
 */
public class TooltipComponent{

    private List<List<IComponentRender>> objects = new ArrayList<>();
    private List<IComponentRender> currentObjects = new ArrayList<>();

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


    /**
     * Renders the specified text to the screen, center-aligned. Args : renderer, string, x, y, color
     */
    public static void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color){
        if(text == null){
            text = "TUMAT NPE Error";
        }
        fontRendererIn.drawStringWithShadow(text, (float)(x - fontRendererIn.getStringWidth(text) / 2), (float)y, color);
    }

    public static String getBlockName(IBlockState state){
        if(!RenderOverlay.remapMappings.containsKey(state.getBlock()) || (RenderOverlay.remapMappings.containsKey(state.getBlock()) && RenderOverlay.remapMappings.get(state.getBlock()).getKey() == null)){
            Item itemBlock = Item.getItemFromBlock(state.getBlock());
            if(itemBlock != null){
                return itemBlock.getItemStackDisplayName(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)));
            } else {
                return state.getBlock().getLocalizedName();
            }
        }
        return RenderOverlay.remapMappings.get(state.getBlock()).getKey();
    }

    public static String getEntityName(Entity entity){
        String defaultName = entity.getDisplayName().getFormattedText();
        if(defaultName.endsWith(".name")){
            String[] array = defaultName.split(".");
            defaultName = StringUtils.capitalize(array[array.length-2]);
        }
        return defaultName;
    }

    public static void syncTileEntity(TileEntity tile, boolean shouldCalculate, String... nbtKeys){
        if(shouldCalculate){
            NetworkHandler.network.sendToServer(new PacketUpdateTileEntity(tile.getPos(), nbtKeys));
        }
    }

}
