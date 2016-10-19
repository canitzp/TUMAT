package de.canitzp.tumat.api;

import de.canitzp.tumat.api.components.TextComponent;
import de.canitzp.tumat.network.NetworkHandler;
import de.canitzp.tumat.network.PacketUpdateTileEntity;
import net.minecraft.client.Minecraft;
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
    private int length = 0;

    private TextComponent modName;

    private TooltipComponent addRenderer(IComponentRender render){
        if(render != null){
            if(this.length < render.getLength(Minecraft.getMinecraft().fontRendererObj)){
                this.length = render.getLength(Minecraft.getMinecraft().fontRendererObj);
            }
            this.currentObjects.add(render);
        }
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
        newLine();
        addOneLineRenderer(this.modName);
        this.objects.add(new ArrayList<>(this.currentObjects));
        return this.objects;
    }

    public TooltipComponent clear(){
        this.objects.clear();
        this.currentObjects.clear();
        this.modName = null;
        return this;
    }

    public TooltipComponent setModName(String s){
        if(modName == null){
            this.modName = new TextComponent(s);
        }
        return this;
    }

    public int getLength(){
        return this.length;
    }

    public TooltipComponent setFirst(List<IComponentRender> components){
        this.objects.set(0, components);
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

    public static String getAdvancedName(ItemStack stack){
        String s = " (";
        String s1 = ")";

        int i = Item.getIdFromItem(stack.getItem());
        if(stack.getHasSubtypes()){
            s = s + String.format("#%04d/%d%s", i, stack.getItemDamage(), s1);
        } else {
            s = s + String.format("#%04d%s", i, s1);
        }
        return s;
    }

}
