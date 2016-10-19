package de.canitzp.tumat.api;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * @author canitzp
 */
@SideOnly(Side.CLIENT)
public class TUMATApi{

    public static List<Class<? extends GuiScreen>> allowedGuis = new ArrayList<>();
    private static List<IWorldRenderer> registeredComponents = new ArrayList<>();

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

    @SafeVarargs
    public static void allowGuiToRenderOverlay(Class<? extends GuiScreen>... guis){
        for(Class<? extends GuiScreen> gui : guis){
            if(!allowedGuis.contains(gui)){
                allowedGuis.add(gui);
            }
        }
    }

    public static List<IWorldRenderer> getRegisteredComponents(){
        return registeredComponents;
    }

    public static List<Class<? extends GuiScreen>> getAllowedGuis(){
        return allowedGuis;
    }

}
