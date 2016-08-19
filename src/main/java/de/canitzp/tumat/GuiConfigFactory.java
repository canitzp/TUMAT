package de.canitzp.tumat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author canitzp
 */
public class GuiConfigFactory implements IModGuiFactory{
    @Override
    public void initialize(Minecraft minecraftInstance){}

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass(){
        return GuiConfig.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories(){
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element){
        return null;
    }

    public static class GuiConfig extends net.minecraftforge.fml.client.config.GuiConfig{
        public GuiConfig(GuiScreen parentScreen){
            super(parentScreen, getConfigElements(), TUMAT.MODID, false, false, TUMAT.MODNAME);
        }
        private static List<IConfigElement> getConfigElements(){
            List<IConfigElement> list = new ArrayList<>();
            for(String catName : Config.config.getCategoryNames()){
                list.addAll(new ConfigElement(Config.config.getCategory(catName)).getChildElements());
            }
            return list;
        }
    }

}
