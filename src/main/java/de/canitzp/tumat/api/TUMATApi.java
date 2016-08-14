package de.canitzp.tumat.api;

import java.util.ArrayList;
import java.util.List;

/**
 * @author canitzp
 */
public class TUMATApi{

    private static List<IWorldRenderer> registeredComponents = new ArrayList<IWorldRenderer>();

    public static void registerRenderComponent(IWorldRenderer component){
        if(!registeredComponents.contains(component)){
            registeredComponents.add(component);
        }
    }

    public static void registerRenderComponent(Class<? extends IWorldRenderer> component){
        try{
            registerRenderComponent(component.newInstance());
        } catch(InstantiationException e){
            e.printStackTrace();
        } catch(IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public static List<IWorldRenderer> getRegisteredComponents(){
        return registeredComponents;
    }
}
