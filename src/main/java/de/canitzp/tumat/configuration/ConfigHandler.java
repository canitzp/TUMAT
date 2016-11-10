package de.canitzp.tumat.configuration;

import com.google.common.collect.Maps;
import de.canitzp.tumat.TUMAT;
import de.canitzp.tumat.configuration.cats.ConfigBooleans;
import de.canitzp.tumat.configuration.cats.ConfigCats;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author canitzp
 */
@Mod.EventBusSubscriber
public class ConfigHandler {

    public static final String CONFIG_VERSION = "1";
    public static final Map<String, String[]> compatibleConfigs = new HashMap<>();
    public static Configuration config;

    public static void preInit(FMLPreInitializationEvent event){
        compatibleConfigs.put("1", new String[]{"1", "4"});
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
        if(config.hasChanged()){
            config.save();
        }
    }

    public static void defineValues(){
        for(ConfigBooleans conf : ConfigBooleans.values()){
            conf.value = config.getBoolean(conf.name, conf.category.name, conf.defaultValue, conf.desc);
        }
    }

    @SubscribeEvent
    public static void onConfigSave(ConfigChangedEvent.OnConfigChangedEvent event){
        if(TUMAT.MODID.equals(event.getModID())){
            defineConfigs();
        }
    }

}
