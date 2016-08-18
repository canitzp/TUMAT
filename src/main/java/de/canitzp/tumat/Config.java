package de.canitzp.tumat;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author canitzp
 */
public class Config{

    public static boolean shouldRenderOverlay;
    public static float distanceToRenderSurvival, distanceToRenderCreative;
    public static boolean showEntityItems;

    private static Configuration config;

    public static void init(FMLPreInitializationEvent event){
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        init();
        config.save();
    }

    private static void init(){
        String cat = "general";
        shouldRenderOverlay = config.getBoolean("ShouldRenderOverlay", cat, true, "The main on or off switch. Set this to false to deactivate the rendering.");
        cat = "distances";
        distanceToRenderSurvival = config.getFloat("DistanceOfViewSurvival", cat, 4.5F, 0F, 192F, "The distance a player in survival can see the blocks. Can be increased up to 12 chunks (192 blocks).");
        distanceToRenderCreative = config.getFloat("DistanceOfViewCreative", cat, 5F, 0F, 192F, "The distance a player in creative can see the blocks. Can be increased up to 12 chunks (192 blocks).");
        cat = "rendering";
        showEntityItems = config.getBoolean("ShowEntityItem", cat, true, "Should be the tooltip be activated for EntityItems");
    }

    @SideOnly(Side.CLIENT)
    public static void loadConfigFromServer(NBTTagCompound nbt){
        if(nbt != null){
            shouldRenderOverlay = nbt.getBoolean("shouldRenderOverlay");
            distanceToRenderSurvival = nbt.getFloat("distanceToRenderSurvival");
            distanceToRenderCreative = nbt.getFloat("distanceToRenderCreative");
            showEntityItems = nbt.getBoolean("showEntityItems");
        }
    }

    @SideOnly(Side.SERVER)
    public static NBTTagCompound sendConfigToClient(){
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("shouldRenderOverlay", shouldRenderOverlay);
        nbt.setFloat("distanceToRenderSurvival", distanceToRenderSurvival);
        nbt.setFloat("distanceToRenderCreative", distanceToRenderCreative);
        nbt.setBoolean("showEntityItems", showEntityItems);
        return nbt;
    }

}
