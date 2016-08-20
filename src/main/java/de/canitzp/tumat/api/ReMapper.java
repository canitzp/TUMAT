package de.canitzp.tumat.api;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * @author canitzp
 */
public class ReMapper<T, K, M>{

    private Map<T, Pair<K, M>> nameChangedElements = new HashMap<>();

    public ReMapper<T, K, M> remapName(T object, K newName){
        M modName = null;
        if(this.nameChangedElements.containsKey(object)){
            modName = this.nameChangedElements.get(object).getValue();
        }
        nameChangedElements.put(object, Pair.of(newName, modName));
        return this;
    }

    public ReMapper<T, K, M> remapModName(T object, M newModName){
        K name = null;
        if(this.nameChangedElements.containsKey(object)){
            name = this.nameChangedElements.get(object).getKey();
        }
        nameChangedElements.put(object, Pair.of(name, newModName));
        return this;
    }

    public ReMapper<T, K, M> remap(T object, K newName, M newModName){
        this.nameChangedElements.put(object, Pair.of(newName, newModName));
        return this;
    }

    public ReMapper<T, K, M> removeMapping(T object){
        this.nameChangedElements.remove(object);
        return this;
    }

    public Map<T, Pair<K, M>> getRemappedElements(){
        return nameChangedElements;
    }

    public Map<T, Pair<K, M>> mergeRemappedElementsWithExisting(Map<T, Pair<K, M>> existing){
        for(Map.Entry<T, Pair<K, M>> entry : nameChangedElements.entrySet()){
            K name = null;
            M modName = null;
            if(existing.containsKey(entry.getKey())){
                name = existing.get(entry.getKey()).getKey();
                modName = existing.get(entry.getKey()).getValue();
            }
            if(entry.getValue().getKey() != null){
                name = entry.getValue().getKey();
            }
            if(entry.getValue().getValue() != null){
                modName = entry.getValue().getValue();
            }
            existing.put(entry.getKey(), Pair.of(name, modName));
        }
        return existing;
    }

}
