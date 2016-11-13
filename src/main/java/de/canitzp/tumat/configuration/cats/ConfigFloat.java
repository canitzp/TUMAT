package de.canitzp.tumat.configuration.cats;

/**
 * @author canitzp
 */
public enum ConfigFloat {

    DISTANCE_CREATIVE("DistanceCreative", "", 5F, 0F, 150F, ConfigCats.RENDERING),
    DISTANCE_SURVIVAL("DistanceSurvival", "", 4.5F, 0F, 150F, ConfigCats.RENDERING),
    DISTANCE_ADVENTURE("DistanceAdventure", "", 4.5F, 0F, 150F, ConfigCats.RENDERING),
    DISTANCE_SPECTATOR("DistanceSpectator", "", 0F, 0F, 150F, ConfigCats.RENDERING),

    OFFSET_X("OffsetX", "Please use the TUMAT option screen to change the position! These are percentages.", 50.0F, 0F, 100F, ConfigCats.RENDERING),
    OFFSET_Y("OffsetY", "Please use the TUMAT option screen to change the position! These are percentages.", 1.5F, 0F, 100.0F, ConfigCats.RENDERING),
    SCALE("Scale", "Please use the TUMAT option screen to change the position! These are percentages.", 1.0F, 0F, 5.0F, ConfigCats.RENDERING);

    public String name, desc;
    public float defaultValue, value, minValue, maxValue;
    public ConfigCats category;
    public boolean dirty;

    ConfigFloat(String name, String desc, float defaultValue, float minValue, float maxValue, ConfigCats category){
        this.name = name;
        this.desc = desc;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.category = category;
    }

    public void saveNewValue(float value){
        this.value = value;
        this.dirty = true;
    }

}
