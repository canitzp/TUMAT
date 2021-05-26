package de.canitzp.tumat.api.components;

import de.canitzp.tumat.api.IComponentRender;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;

/**
 * This class is for a energy line, but is only used for IC2
 * @author canitzp
 */
public class EnergyComponent implements IComponentRender{

    private TextComponent text;

    public EnergyComponent(int currentEnergy, int maxEnergy, String energyString, TextFormatting... forms){
        this(String.valueOf(currentEnergy), String.valueOf(maxEnergy), energyString, forms);
    }

    public EnergyComponent(String currentEnergy, String maxEnergy, String energyString, TextFormatting... forms){
        this.text = new TextComponent(currentEnergy + "/" + maxEnergy + " " + energyString).setFormat(forms);
    }

    @Override
    public void render(FontRenderer fontRenderer, int x, int y, int color){
        this.text.render(fontRenderer, x, y, color);
    }

    @Override
    public int getLength(FontRenderer fontRenderer){
        return this.text.getLength(fontRenderer);
    }
}
