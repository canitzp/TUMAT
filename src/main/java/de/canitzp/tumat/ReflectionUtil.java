package de.canitzp.tumat;

import java.lang.reflect.Field;

public class ReflectionUtil{
    
    public static <T, E> T getInstanceOfField(Class<E> classToLook, Class<T> fieldType, E instance, String fieldName){
        for(Field field : classToLook.getDeclaredFields()){
            if(field != null && fieldName.equals(field.getName())){
                if(!field.isAccessible()){
                    field.setAccessible(true);
                }
                try{
                    Object o = field.get(instance);
                    if(fieldType.isAssignableFrom(o.getClass())){
                        return (T) o;
                    } else {
                        return null;
                    }
                }catch(IllegalAccessException e){
                    System.out.println("The field '" + fieldName + "' in class '" + classToLook.getName() + "' couldn't be accessed!");
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
}
