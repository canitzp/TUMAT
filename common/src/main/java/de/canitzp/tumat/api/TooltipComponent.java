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

import java.util.*;

/**
 * This is the main collection for all things that get rendered with TUMAT
 * @author canitzp
 */
@Environment(EnvType.CLIENT)
public class TooltipComponent{

    private MutableComponent name;
    private Deque<MutableComponent> high = new ArrayDeque<>();
    private Deque<MutableComponent> low = new ArrayDeque<>();
    private MutableComponent modName;
    private IconRenderer iconRenderer;
    private int length = 0, height = 0;

    public TooltipComponent setName(MutableComponent name){
        this.name = name;
        return this;
    }

    public TooltipComponent setModName(MutableComponent modName){
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
                this.low.addLast(render);
                break;
            }
            case HIGH:{
                this.high.addLast(render);
                break;
            }
        }
        return this;
    }
    
    public void render(PoseStack pose, Font font, float x, float y){
        List<MutableComponent> finished = new LinkedList<>();
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

    public TooltipComponent setIconRenderer(IconRenderer renderer){
        this.iconRenderer = renderer;
        return this;
    }

    public IconRenderer getIconRenderer() {
        return iconRenderer;
    }

    public enum Priority{
        HIGH,LOW
    }
}
