package de.canitzp.tumat.configuration.cats;

/**
 * @author canitzp
 */
public enum ConfigBooleans {

    SHOULD_TUMAT_RENDER("ShouldRenderOverlay", "The main on or off switch. Set this to false to deactivate the rendering.", true, ConfigCats.GENERAL),
    //TODO implement
    SERVER_CONTROL("ServerControl", "Should the Server control the tooltips for the clients. At the client this config changes nothing.", false, ConfigCats.GENERAL),

    //TODO implement
    SHOW_BLOCKS("ShowBlocks", "", true, ConfigCats.MODULE),
    //TODO implement
    SHOW_ENTITIES("ShowEntities", "", true, ConfigCats.MODULE),
    //TODO implement
    SHOW_FLUIDS("ShowFluids", "", true, ConfigCats.MODULE),
    //TODO implement
    SHOW_DROPPED_ITEMS("ShowDroppedItems", "", true, ConfigCats.MODULE),
    //TODO implement
    SHOW_TILES("ShowTileEntities", "", true, ConfigCats.MODULE),

    //TODO implement
    SHOW_TESLA("ShowEnergyTesla", "", true, ConfigCats.MODULE),
    //TODO implement
    SHOW_RF("ShowEnergyRF", "", true, ConfigCats.MODULE),
    //TODO implement
    SHOW_EU("ShowEnergyEU", "", true, ConfigCats.MODULE),
    //TODO implement
    SHOW_FU("ShowEnergyFU", "", true, ConfigCats.MODULE),

    //TODO implement
    SHOW_SPECIAL_TILE_STATS("ShowSpecialTileEntityInformation", "", false, ConfigCats.MODULE),
    //TODO implement
    SHOW_REDSTONE_STRENGTH("ShowRedstoneStrength", "", false, ConfigCats.MODULE),
    //TODO implement
    SHOW_LIGHT_LEVEL("ShowLightLevel", "", false, ConfigCats.MODULE),
    //TODO implement
    SHOW_PLANT_GROWTH_STATUS("ShowPlantGrowthStatus", "", true, ConfigCats.MODULE),
    //TODO implement
    SHOW_HARVESTABILITY("ShowHarvestability", "", false, ConfigCats.MODULE),

    SHOW_SLOT_NUMBERS("ShowSlotNumbers", "", true, ConfigCats.MODULE);

    public String name, desc;
    public boolean defaultValue, value;
    public ConfigCats category;

    ConfigBooleans(String name, String desc, boolean defaultValue, ConfigCats category){
        this.name = name;
        this.desc = desc;
        this.defaultValue = defaultValue;
        this.category = category;
    }

}
