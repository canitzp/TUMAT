package de.canitzp.tumat.configuration.cats;

import net.minecraft.util.text.TextFormatting;

/**
 * @author canitzp
 */
public enum ConfigString {

    MOD_NAME_FORMAT("ModNameFormat", "How should TUMAT show the mod name", TextFormatting.BLUE.toString() + TextFormatting.ITALIC.toString(), ConfigCats.RENDERING),
    BACKGROUND_COLOR("BackgroundColor", "What should be the color of the TUMAT background. The format is HEX AARRGGBB", "0x806A9BC3", ConfigCats.RENDERING);

    public String name, desc;
    public String defaultValue, value;
    public ConfigCats category;

    ConfigString(String name, String desc, String defaultValue, ConfigCats category){
        this.name = name;
        this.desc = desc;
        this.defaultValue = defaultValue;
        this.category = category;
    }

}
