package de.canitzp.tumat.api.components;

import de.canitzp.tumat.InfoUtil;
import de.canitzp.tumat.api.IComponentRender;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is for block or item descriptions.
 * @author canitzp
 */
public class DescriptionComponent implements IComponentRender {

    public List<TextComponent> lines = new ArrayList<>();

    public DescriptionComponent(String[] desc){
        if(desc != null && !(desc.length <= 0)){
            for(String s : desc){
                lines.add(new ScaledTextComponent(s, 0.8F).setFormat(TextFormatting.GRAY));
            }
        }
    }

    public DescriptionComponent(List<String> desc){
        this(desc.toArray(new String[]{}));
    }

    public DescriptionComponent(ItemStack stack){
        this(InfoUtil.getDescription(stack));
    }

    @Override
    public void render(FontRenderer fontRenderer, int x, int y, int color) {
        for (int i = 0; i < lines.size(); i++) {
            TextComponent line = lines.get(i);
            line.render(fontRenderer, x, y + (8*i), color);
        }
    }

    @Override
    public int getLength(FontRenderer fontRenderer) {
        int max = 0;
        for(TextComponent text : lines){
            if(text.getLength(fontRenderer) > max){
                max = text.getLength(fontRenderer);
            }
        }
        return max;
    }

    @Override
    public int getLines(FontRenderer fontRenderer) {
        return this.lines.size();
    }

    @Override
    public int getHeightPerLine(FontRenderer fontRenderer) {
        return 8;
    }
}
