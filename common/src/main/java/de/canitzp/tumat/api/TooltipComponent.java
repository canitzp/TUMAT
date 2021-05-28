package de.canitzp.tumat.api;

import com.mojang.blaze3d.vertex.PoseStack;
import de.canitzp.tumat.IconRenderer;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import de.canitzp.tumat.configuration.cats.ConfigFloat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the main collection for all things that get rendered with TUMAT
 * @author canitzp
 */
@Environment(EnvType.CLIENT)
public class TooltipComponent{

    private TextComponent name;
    private List<MutableComponent> high = new ArrayList<>();
    private List<MutableComponent> low = new ArrayList<>();
    private TextComponent modName;
    private IconRenderer iconRenderer;
    private int length = 0, height = 0;

    public TooltipComponent setName(TextComponent name){
        this.name = name;
        return this;
    }

    public TooltipComponent setModName(TextComponent modName){
        this.modName = modName;
        return this;
    }

    public TooltipComponent add(MutableComponent render, Priority priority){
        Font fontRenderer = Minecraft.getInstance().font;
        int length = fontRenderer.width(render);
        if(this.length < length){
            this.length = length;
        }
        this.height += fontRenderer.lineHeight;
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
    
    public void render(PoseStack pose, Font font, float x, float y){
        List<MutableComponent> finished = new ArrayList<>();
        finished.add(this.name);
        finished.addAll(this.high);
        finished.addAll(this.low);
        finished.add(this.modName);
    
        pose.pushPose();
        int maxLength = 0;
        for(int i = 0; i < finished.size(); i++){
            MutableComponent component = finished.get(0);
            pose.scale(ConfigFloat.SCALE.value, ConfigFloat.SCALE.value, ConfigFloat.SCALE.value);
            int length = font.draw(pose, component, x, y + ((i * 10)), 0xFFFFFF);
            if(length > maxLength){
                maxLength = length;
            }
        }
        if(ConfigBoolean.SHOW_BACKGROUND.value && this.iconRenderer != null && this.iconRenderer.shouldRender()){
            this.iconRenderer.render(pose, Math.round(x - (maxLength / 2.0F)) - 22, Math.round(y + (finished.size() / 2.0F)) - 10);
        }
        pose.popPose();
    }

    @Deprecated
    public Finished close(){
        List<MutableComponent> list = new ArrayList<>();
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
        private List<MutableComponent> list;
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
