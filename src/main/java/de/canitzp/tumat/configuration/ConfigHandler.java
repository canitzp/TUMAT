package de.canitzp.tumat.configuration;

import de.canitzp.tumat.TUMAT;
import de.canitzp.tumat.configuration.cats.ConfigBoolean;
import de.canitzp.tumat.configuration.cats.ConfigFloat;
import de.canitzp.tumat.configuration.cats.ConfigString;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author canitzp
 */
public class ConfigHandler {

    public static final String CONFIG_VERSION = "1";
    public static final Map<String, String[]> compatibleConfigs = new HashMap<>();
    public static Configuration config;
    private static boolean isDirty;

    public static void preInit(FMLPreInitializationEvent event){
        compatibleConfigs.put("1", new String[]{"1"});
        config = new Configuration(event.getSuggestedConfigurationFile(), CONFIG_VERSION);
        if(!CONFIG_VERSION.equals(config.getLoadedConfigVersion())){
            String loaded = config.getLoadedConfigVersion();
            boolean isCompatible = false;
            if(loaded != null && compatibleConfigs.containsKey(loaded)){
                for(String compatVersion : compatibleConfigs.get(loaded)){
                    if(compatVersion.equals(loaded)){
                        isCompatible = true;
                        break;
                    }
                }
            }
            if(!isCompatible){
                TUMAT.logger.error("You're loading a to old or undefined TUMAT config file! TUMAT creates a new one and backups the old one!");
                File backup = new File(config.getConfigFile().getParentFile(), TUMAT.MODID + "_backup.cfg");
                try {
                    FileUtils.copyFile(config.getConfigFile(), backup);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                config.getConfigFile().delete();
                config.load();
            }
        }
        defineConfigs();
    }

    public static void defineConfigs(){
        defineValues();
        if(isDirty || config.hasChanged()){
            config.save();
            isDirty = false;
        }
    }

    public static void defineValues(){
        for(ConfigBoolean conf : ConfigBoolean.values()){
            conf.value = config.getBoolean(conf.name, conf.category.name, conf.defaultValue, conf.desc);
        }
        for(ConfigFloat conf : ConfigFloat.values()){
            if(!conf.dirty){
                conf.value = config.getFloat(conf.name, conf.category.name, conf.defaultValue, conf.minValue, conf.maxValue, conf.desc);
            } else {
                isDirty = true;
                conf.dirty = false;
                config.getCategory(conf.category.name).put(conf.name, new Property(conf.name, Float.toString(conf.value), Property.Type.STRING));
            }
        }
        for(ConfigString conf : ConfigString.values()){
            conf.value = config.getString(conf.name, conf.category.name, conf.defaultValue, conf.desc);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void readFromServerNBT(NBTTagCompound nbt){
        if(!nbt.isEmpty()){
            for(ConfigBoolean conf : ConfigBoolean.values()){
                if(nbt.hasKey(conf.name, 1)){ // 1 means byte and a boolean is saved as byte in a nbt compound
                    conf.value = nbt.getBoolean(conf.name);
                }
            }
        }
    }

    public static NBTTagCompound writeToNBT(){
        NBTTagCompound nbt = new NBTTagCompound();
        for(ConfigBoolean conf : ConfigBoolean.values()){
            if(conf.shouldSync){
                nbt.setBoolean(conf.name, conf.value);
            }
        }
        return nbt;
    }

}
