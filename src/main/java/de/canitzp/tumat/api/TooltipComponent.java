package de.canitzp.tumat.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
        fontRendererIn.drawStringWithShadow(text, (float)(x - fontRendererIn.getStringWidth(text) / 2), (float)y, color);
    }

    public static String getBlockName(IBlockState state){
        Item itemBlock = Item.getItemFromBlock(state.getBlock());
        if(itemBlock != null){
            return itemBlock.getItemStackDisplayName(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)));
        } else {
            return state.getBlock().getLocalizedName();
        }
    }

}
