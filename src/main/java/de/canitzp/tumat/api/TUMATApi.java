package de.canitzp.tumat.api;

import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;

/**
 * @author canitzp
 */
public class TUMATApi{

    private static List<IWorldRenderer> registeredComponents = new ArrayList<IWorldRenderer>();
    public static List<Class<? extends GuiScreen>> guisWhereToRender = new ArrayList<>();

    public static void registerRenderComponent(IWorldRenderer component){
        if(!registeredComponents.contains(component)){
            registeredComponents.add(component);
        }
    }

    public static void registerRenderComponent(Class<? extends IWorldRenderer> component){
        try{
            registerRenderComponent(component.newInstance());
        } catch(InstantiationException | IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public static List<IWorldRenderer> getRegisteredComponents(){
        return registeredComponents;
    }

    public static void addGuiWhereToRender(Class<? extends GuiScreen> gui){
        if(!guisWhereToRender.contains(gui)){
            guisWhereToRender.add(gui);
        }
    }

}
