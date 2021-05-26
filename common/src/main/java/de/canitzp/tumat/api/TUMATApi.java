package de.canitzp.tumat.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.List;


/**
 * This is the main API class for TUMAT
 *
 * The TUMAT api is not for a bundled usage!
 *
 * @author canitzp
 */
@Environment(EnvType.CLIENT)
public class TUMATApi{

    public static List<Class<? extends Screen>> allowedGuis = new ArrayList<>();
    private static List<IWorldRenderer> registeredComponents = new ArrayList<>();

    /**
     * Here you can add your own IWorldRenderer
     * @param components Your own renderer
     */
    @SafeVarargs
    public static void registerRenderComponent(Class<? extends IWorldRenderer>... components){
        for(Class<? extends IWorldRenderer> renderer : components){
            try{
                IWorldRenderer renderer1 = renderer.newInstance();
                if(!registeredComponents.contains(renderer1)){
                    registeredComponents.add(renderer1);
                }
            } catch(InstantiationException | IllegalAccessException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Here you can add gui classes that TUMAT renders over
     * @param guis The classes
     */
    @SafeVarargs
    public static void allowGuiToRenderOverlay(Class<? extends Screen>... guis){
        for(Class<? extends Screen> gui : guis){
            if(!allowedGuis.contains(gui)){
                allowedGuis.add(gui);
            }
        }
    }

    public static List<IWorldRenderer> getRegisteredComponents(){
        return registeredComponents;
    }

    public static List<Class<? extends Screen>> getAllowedGuis(){
        return allowedGuis;
    }

}
