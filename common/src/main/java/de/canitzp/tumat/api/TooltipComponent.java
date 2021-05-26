package de.canitzp.tumat.api;

import de.canitzp.tumat.IconRenderer;
import de.canitzp.tumat.api.components.TextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the main collection for all things that get rendered with TUMAT
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class TooltipComponent{

    private TextComponent name;
    private List<IComponentRender> high = new ArrayList<>();
    private List<IComponentRender> low = new ArrayList<>();
    private TextComponent modName;
    private IconRenderer iconRenderer;
    private int length = 0, height = 0;

    public TooltipComponent setName(@Nonnull TextComponent name){
        this.name = name;
        return this;
    }

    public TooltipComponent setModName(@Nonnull TextComponent modName){
        this.modName = modName;
        return this;
    }

    @Deprecated
    public TooltipComponent setModName(String s){
        return this.setModName(new TextComponent(s));
    }

    public TooltipComponent add(IComponentRender render, Priority priority){
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        if(this.length < render.getLength(fontRenderer)){
            this.length = render.getLength(fontRenderer);
        }
        this.height += render.getLines(fontRenderer) * render.getHeightPerLine(fontRenderer);
        switch (priority){
            case LOW: {
                this.low.add(render);
                break;
            }
            case HIGH:{
                this.high.add(render);
                break;
            }
        }
        return this;
    }

    public TooltipComponent clear(Priority priority){
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        switch (priority){
            case LOW: {
                for(IComponentRender render : this.low){
                    this.height -= render.getLines(fontRenderer) * render.getHeightPerLine(fontRenderer);
                }
                this.low.clear();
                break;
            }
            case HIGH:{
                for(IComponentRender render : this.high){
                    this.height -= render.getLines(fontRenderer) * render.getHeightPerLine(fontRenderer);
                }
                this.high.clear();
                break;
            }
        }
        return this;
    }

    public Finished close(){
        List<IComponentRender> list = new ArrayList<>();
        list.add(this.name);
        list.addAll(this.high);
        list.addAll(this.low);
        list.add(this.modName);
        this.height += 20;
        if(this.name != null && this.length < this.name.getLength(Minecraft.getMinecraft().fontRenderer)){
            this.length = this.name.getLength(Minecraft.getMinecraft().fontRenderer);
        }
        if(this.modName != null && this.length < this.modName.getLength(Minecraft.getMinecraft().fontRenderer)){
            this.length = this.modName.getLength(Minecraft.getMinecraft().fontRenderer);
        }
        return new Finished(this.name == null ? new ArrayList<>() : list, this.length, this.height);
    }

    public TooltipComponent setIconRenderer(IconRenderer renderer){
        this.iconRenderer = renderer;
        return this;
    }

    public IconRenderer getIconRenderer() {
        return iconRenderer;
    }

    public class Finished{
        private List<IComponentRender> list;
        private int length, height;
        public Finished(List<IComponentRender> list, int length, int height) {
            this.list = list;
            if(list.isEmpty()){
                this.length = this.height = 0;
            } else {
                this.length = length;
                this.height = height;
            }
        }
        public List<IComponentRender> getComponents() {
            return list;
        }
        public int getLength() {
            return length;
        }
        public int getHeight() {
            return height;
        }
    }

    public enum Priority{
        HIGH,LOW
    }
}
