package de.canitzp.tumat.api;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author canitzp
 */
public class ReMapper<T, K, M, E>{

    private Map<T, Triple<K, M, E>> nameChangedElements = new HashMap<>();

    public ReMapper<T, K, M, E> remapFirst(T object, K value){
        M currentMiddle = null;
        E currentRight = null;
        if(this.nameChangedElements.containsKey(object)){
            currentMiddle = this.nameChangedElements.get(object).getMiddle();
            currentRight = this.nameChangedElements.get(object).getRight();
        }
        nameChangedElements.put(object, Triple.of(value, currentMiddle, currentRight));
        return this;
    }

    public ReMapper<T, K, M, E> remapSecond(T object, M value){
        K currentLeft = null;
        E currentRight = null;
        if(this.nameChangedElements.containsKey(object)){
            currentLeft = this.nameChangedElements.get(object).getLeft();
            currentRight = this.nameChangedElements.get(object).getRight();
        }
        nameChangedElements.put(object, Triple.of(currentLeft, value, currentRight));
        return this;
    }

    public ReMapper<T, K, M, E> remap(T object, K left, M middle, E right){
        this.nameChangedElements.put(object, Triple.of(left, middle, right));
        return this;
    }

    public ReMapper<T, K, M, E> removeMapping(T object){
        this.nameChangedElements.remove(object);
        return this;
    }

    public Map<T, Triple<K, M, E>> mergeRemappedElementsWithExisting(Map<T, Triple<K, M, E>> existing){
        for(Map.Entry<T, Triple<K, M, E>> entry : nameChangedElements.entrySet()){
            K left = null;
            M middle = null;
            E right = null;
            if(existing.containsKey(entry.getKey())){
                left = existing.get(entry.getKey()).getLeft();
                middle = existing.get(entry.getKey()).getMiddle();
                right = existing.get(entry.getKey()).getRight();
            }
            if(entry.getValue().getLeft() != null){
                left = entry.getValue().getLeft();
            }
            if(entry.getValue().getMiddle() != null){
                middle = entry.getValue().getMiddle();
            }
            if(entry.getValue().getRight() != null){
                right = entry.getValue().getRight();
            }
            existing.put(entry.getKey(), Triple.of(left, middle, right));
        }
        return existing;
    }

    public Set<T> getKeys(){
        return this.nameChangedElements.keySet();
    }

    public Triple<K, M, E> getValue(T key){
        return this.nameChangedElements.containsKey(key) ? this.nameChangedElements.get(key) : Triple.of(null, null, null);
    }

}
