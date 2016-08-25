package de.canitzp.tumat;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/**
 * @author canitzp
 */
public class GuiTUMAT extends GuiScreen{

    @Override
    public void initGui(){
        super.initGui();
        this.addButton(new GuiButton(456, this.mc.displayWidth / 2, this.mc.displayHeight - 20, "+"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawDefaultBackground();
    }
}
