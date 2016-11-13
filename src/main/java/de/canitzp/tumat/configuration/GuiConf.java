package de.canitzp.tumat.configuration;

import de.canitzp.tumat.TUMAT;
import de.canitzp.tumat.configuration.cats.ConfigCats;
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
        for(int i = 0; i < ConfigCats.values().length; i++){
            ConfigCats cat = ConfigCats.values()[i];
            ConfigHandler.config.setCategoryComment(cat.name, cat.desc);
            list.add(new ConfigElement(ConfigHandler.config.getCategory(cat.name)));
        }
        return list;
    }

}
