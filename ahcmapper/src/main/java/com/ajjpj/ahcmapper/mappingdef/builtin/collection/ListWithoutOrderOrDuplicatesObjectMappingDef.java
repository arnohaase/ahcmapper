package com.ajjpj.ahcmapper.mappingdef.builtin.collection;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class ListWithoutOrderOrDuplicatesObjectMappingDef extends AbstractListObjectMappingDef {
    @Override
    protected void merge(List<Object> shadowList, List<Object> target) {
        final Set<Object> shadowSet = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());
        shadowSet.addAll(shadowList);
        
        // remove obsolete elements from target
        final Iterator<Object> removeIter = target.iterator();
        while(removeIter.hasNext()) {
            final Object cur = removeIter.next();
            if (! shadowSet.contains(cur)) { //TODO do not rely on 'contains', i.e. equals
                removeIter.remove();
            }
            else {
                shadowSet.remove(cur);
            }
        }
        
        // add new elements to target
        target.addAll(shadowSet);
    }
}
