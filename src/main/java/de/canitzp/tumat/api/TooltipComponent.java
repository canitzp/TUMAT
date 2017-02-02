package de.canitzp.tumat.api;

import de.canitzp.tumat.api.components.TextComponent;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the main collection for all things that get rendered with TUMAT
 * @author canitzp
 */
public class TooltipComponent{

    private List<List<IComponentRender>> objects = new ArrayList<>();
    private List<IComponentRender> currentObjects = new ArrayList<>();
    private int length = 0;

    private TextComponent modName;

    private TooltipComponent addRenderer(IComponentRender render){
        if(render != null){
            if(this.length < render.getLength(Minecraft.getMinecraft().fontRenderer)){
                this.length = render.getLength(Minecraft.getMinecraft().fontRenderer);
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

}
