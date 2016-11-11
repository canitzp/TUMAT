package de.canitzp.tumat.configuration.cats;

/**
 * @author canitzp
 */
public enum ConfigBoolean {

    SHOULD_TUMAT_RENDER("ShouldRenderOverlay", "The main on or off switch. Set this to false to deactivate the rendering.", true, ConfigCats.GENERAL),
    SERVER_CONTROL("ServerControl", "Should the Server control the tooltips for the clients. At the client this config changes nothing.", false, ConfigCats.GENERAL),

    SHOW_BLOCKS("ShowBlocks", "", true, ConfigCats.MODULE),
    SHOW_ENTITIES("ShowEntities", "", true, ConfigCats.MODULE),
    SHOW_FLUIDS("ShowFluids", "", true, ConfigCats.MODULE),
    SHOW_DROPPED_ITEMS("ShowDroppedItems", "", true, ConfigCats.MODULE),
    SHOW_TILES("ShowTileEntities", "", true, ConfigCats.MODULE),

    SHOW_TESLA("ShowEnergyTesla", "", true, ConfigCats.MODULE),
    SHOW_RF("ShowEnergyRF", "", true, ConfigCats.MODULE),
    SHOW_EU("ShowEnergyEU", "", true, ConfigCats.MODULE),
    SHOW_FU("ShowEnergyFU", "", true, ConfigCats.MODULE),

    SHOW_SPECIAL_TILE_STATS("ShowSpecialTileEntityInformation", "", false, ConfigCats.MODULE),
    SHOW_REDSTONE_STRENGTH("ShowRedstoneStrength", "", false, ConfigCats.MODULE),
    SHOW_LIGHT_LEVEL("ShowLightLevel", "", false, ConfigCats.MODULE),
    SHOW_PLANT_GROWTH_STATUS("ShowPlantGrowthStatus", "", true, ConfigCats.MODULE),
    SHOW_HARVESTABILITY("ShowHarvestability", "", false, ConfigCats.MODULE),
    SHOW_FLUID_TANKS("ShowFluidTanks", "", true, ConfigCats.MODULE),

    SHOW_SLOT_NUMBERS("ShowSlotNumbers", "", true, ConfigCats.MODULE);

    public String name, desc;
    public boolean defaultValue, value;
    public ConfigCats category;

    ConfigBoolean(String name, String desc, boolean defaultValue, ConfigCats category){
        this.name = name;
        this.desc = desc;
        this.defaultValue = defaultValue;
        this.category = category;
    }

}
