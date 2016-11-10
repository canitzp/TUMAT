package de.canitzp.tumat.configuration;

import de.canitzp.tumat.Config;
import de.canitzp.tumat.GuiConfigFactory;
import de.canitzp.tumat.TUMAT;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author canitzp
 */
public class GuiConf extends GuiConfig {

    public GuiConf(GuiScreen parentScreen) {
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
