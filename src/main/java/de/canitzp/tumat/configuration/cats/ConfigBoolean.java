package de.canitzp.tumat.configuration.cats;

/**
 * @author canitzp
 */
public enum ConfigBoolean {

    SHOULD_TUMAT_RENDER("ShouldRenderOverlay", "The main on or off switch. Set this to false to deactivate the rendering.", true, ConfigCats.GENERAL),
    SERVER_CONTROL("ServerControl", "Should the Server control the tooltips for the clients. At the client this config changes nothing.", false, ConfigCats.GENERAL),

    SHOW_BLOCKS("ShowBlocks", "If true TUMAT shows tooltips for Blocks.", true, ConfigCats.GENERAL),
    SHOW_ENTITIES("ShowEntities", "If true TUMAT shows tooltips for Entities.", true, ConfigCats.GENERAL),
    SHOW_FLUIDS("ShowFluids", "If true TUMAT shows tooltips for Fluids.", true, ConfigCats.GENERAL),
    SHOW_DROPPED_ITEMS("ShowDroppedItems", "If true TUMAT shows tooltips for dropped Items.", true, ConfigCats.GENERAL),
    SHOW_TILES("ShowTileEntities", "If true TUMAT shows tooltips for Tile Entities.", true, ConfigCats.GENERAL),
    SHOW_DESCRIPTION("ShowDescription", "If true TUMAT shows the normal item description for Blocks", true, ConfigCats.GENERAL),

    SHOW_TESLA("ShowEnergyTesla", "If true TUMAT will try to show Tesla energy.", true, ConfigCats.MODULE),
    SHOW_RF("ShowEnergyRF", "If true TUMAT will try to show RF (Redstone Flux) energy.", true, ConfigCats.MODULE),
    SHOW_EU("ShowEnergyEU", "If true TUMAT will try to show EU (Energy Units) energy.", true, ConfigCats.MODULE),
    SHOW_FU("ShowEnergyFU", "If true TUMAT will try to show FU (Forge Units) energy.", true, ConfigCats.MODULE),

    SHOW_SPECIAL_TILE_STATS("ShowSpecialTileEntityInformation", "If true TUMAT shows special information about tile entities (manually added tooltips).", false, ConfigCats.MODULE),
    SHOW_REDSTONE_STRENGTH("ShowRedstoneStrength", "If true TUMAT shows the strength of redstone.", false, ConfigCats.MODULE),
    SHOW_LIGHT_LEVEL("ShowLightLevel", "If true TUMAT shows the light level of blocks or light sources", false, ConfigCats.MODULE),
    SHOW_PLANT_GROWTH_STATUS("ShowPlantGrowthStatus", "If true TUMAT shows the growth status of plants.", true, ConfigCats.MODULE),
    SHOW_HARVESTABILITY("ShowHarvestability", "If true TUMAT shows the harvestability tooltip.", false, ConfigCats.MODULE),
    SHOW_FLUID_TANKS("ShowFluidTanks", "If true TUMAT shows the content of fluid tanks", true, ConfigCats.MODULE),

    SHOW_SLOT_NUMBERS("ShowSlotNumbers", "If true TUMAT shows the slot numbers in inventories while pressing ctrl", true, ConfigCats.MODULE);

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
