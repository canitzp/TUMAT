package de.canitzp.tumat.api.components;

import de.canitzp.tumat.InfoUtil;
import de.canitzp.tumat.api.IComponentRender;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is for block or item descriptions.
 * @author canitzp
 */
public class DescriptionComponent implements IComponentRender {

    private List<TextComponent> lines = new ArrayList<>();

    public DescriptionComponent(List<String> desc){
        for(String line : desc){
            this.lines.add(new ScaledTextComponent(line, 0.8F).setFormat(ChatFormatting.GRAY));
        }
    }

    public DescriptionComponent(ItemStack stack){
        this(InfoUtil.getDescription(stack));
    }

    @Override
    public void render(Font fontRenderer, int x, int y, int color) {
        for (int i = 0; i < lines.size(); i++) {
            TextComponent line = lines.get(i);
            line.render(fontRenderer, x, y + (getHeightPerLine(fontRenderer) * i), color);
        }
    }

    @Override
    public int getLength(Font fontRenderer) {
        int max = 0;
        for(TextComponent text : lines){
            if(text.getLength(fontRenderer) > max){
                max = text.getLength(fontRenderer);
            }
        }
        return max;
    }

    @Override
    public int getLines(Font fontRenderer) {
        return this.lines.size();
    }

    @Override
    public int getHeightPerLine(Font fontRenderer) {
        return 8;
    }
}
