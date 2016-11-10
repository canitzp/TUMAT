package de.canitzp.tumat.configuration.cats;

/**
 * @author canitzp
 */
public enum ConfigCats {

    GENERAL("general", "For the general config options of TUMAT"),
    MODULE("module", "What should TUMAT show you");

    public String name, desc;

    ConfigCats(String name, String desc){
        this.name = name;
        this.desc = desc;
    }
}
