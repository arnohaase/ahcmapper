package com.ajjpj.ahcmapper.mappingdef.builtin.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.AhcMapperWorker.ResultHandler;
import com.ajjpj.ahcmapper.core.WorkItem;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperPath;
import com.ajjpj.ahcmapper.core.diff.AhcMapperDiffBuilder;
import com.ajjpj.ahcmapper.core.diff.builder.AhcMapperElementAddedDiffItem;
import com.ajjpj.ahcmapper.core.diff.builder.AhcMapperElementRemovedDiffItem;
import com.ajjpj.ahcmapper.core.equivalence.AhcMapperEquivalenceStrategy;
import com.ajjpj.ahcmapper.mappingdef.AhcCacheableObjectMappingDef;



public class SetObjectMappingDef extends AhcCacheableObjectMappingDef<Object, Set<Object>> {
    @Override
    public boolean canHandle(Class<?> sourceClass, Class<?> sourceElementClass, Class<?> targetClass, Class<?> targetElementClass) {
        final boolean sourceClassCondition = sourceClass.isArray() || Collection.class.isAssignableFrom(sourceClass);
        final boolean targetClassCondition = Set.class.isAssignableFrom(targetClass);
        
        return sourceClassCondition && targetClassCondition;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void doDiff(Object source1, Object source2, Class<? extends Object> sourceClass, Class<?> sourceElementClass, 
            Class<? extends Set<Object>> targetClass, Class<?> targetElementClass, 
                    AhcMapperPath targetPath, AhcMapperDiffBuilder diff, AhcMapperWorker worker) throws Exception {
        final Collection<?> sourceColl1 = asCollection(source1);
        final Collection<?> sourceColl2 = asCollection(source2);
        
        final IdentityHashMap<Object, Object> matches = (IdentityHashMap<Object,Object>) worker.getEquivalenceStrategy().findEquivalentInstances(sourceColl1, sourceColl2, (Class) sourceElementClass, (Class) sourceElementClass);
        
        final Collection<?> removedSource = new HashSet<Object>(sourceColl1);
        removedSource.removeAll(matches.keySet());
        
        final Collection<?> addedSource = new HashSet<Object>(sourceColl2);
        addedSource.removeAll(matches.values());
        
        for(Object o1: matches.keySet()) {
            worker.diff(targetPath, "element", o1, matches.get(o1), sourceElementClass, null, targetElementClass, null, diff, true);
        }
        
        if(addedSource.size() > 0 || removedSource.size() > 0) {
            final Set<?> targetColl1 = mapToTargetColl(sourceColl1, sourceElementClass, targetElementClass, worker.getEquivalenceStrategy());
            final Set<?> targetColl2 = mapToTargetColl(sourceColl2, sourceElementClass, targetElementClass, worker.getEquivalenceStrategy());

            for(Object added: addedSource) {
                final Object targetEquivalenceMarker = worker.getEquivalenceStrategy().getTargetEquivalenceMarker(added, (Class) sourceElementClass, targetElementClass);
                diff.addDiffItem(source1, source2, new AhcMapperElementAddedDiffItem(targetColl1, targetColl2, targetEquivalenceMarker));
                worker.diff(targetPath, "element", null, added, sourceElementClass, null, targetElementClass, null, diff, true, null, null, true);
            }
            for(Object removed: removedSource) {
                final Object targetEquivalenceMarker = worker.getEquivalenceStrategy().getTargetEquivalenceMarker(removed, (Class) sourceElementClass, targetElementClass);
                diff.addDiffItem(source1, source2, new AhcMapperElementRemovedDiffItem(targetColl1, targetColl2, targetEquivalenceMarker));
                worker.diff(targetPath, "element", removed, null, sourceElementClass, null, targetElementClass, null, diff, true, null, null, true);
            }
        }
        
        //TODO diff child elements that were added / removed
        //TODO extract abstract collection superclass
        //TODO warn if target collection is smaller than source collection
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <S> Set<?> mapToTargetColl (Collection<?> sourceColl, Class<?> sourceElementClass, Class<?> targetElementClass, AhcMapperEquivalenceStrategy equiv) throws Exception {
        final Set<Object> result = new HashSet<Object>();
        for(Object source: sourceColl) {
            result.add(equiv.getTargetEquivalenceMarker(source, (Class) sourceElementClass, targetElementClass));
        }
        return result;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void map(Object source, Class<?> sourceElementClass, final Set<Object> target, Class<?> targetElementClass, AhcMapperPath path, AhcMapperWorker worker) throws Exception {
        final Collection<?> sourceColl = asCollection(source);
        
        final IdentityHashMap<Object, Object> equiv = (IdentityHashMap<Object,Object>) worker.getEquivalenceStrategy().findEquivalentInstances(sourceColl, target, (Class) sourceElementClass, (Class) targetElementClass);
        
        final IdentityHashMap<Object, Object> shadowList = new IdentityHashMap<Object, Object>();
        int index = 0;
        for (Object sourceElement: sourceColl) {
            final ResultHandler<Object> resultHandler = new ResultHandler<Object>() {
                @Override
                public void handle(Object targetObject) throws Exception {
                    shadowList.put(targetObject, targetObject);
                }
            };
            
            worker.map(path, String.valueOf(index), sourceElement, sourceElementClass, null, equiv.get(sourceElement), targetElementClass, null, resultHandler, true);
            index++;
        }

        worker.scheduleWorkItem(new WorkItem() {
            @Override
            public void run() throws Exception {
                merge(shadowList, target);
            }
        }, true);
    }

    private void merge(IdentityHashMap<Object, Object> shadowList, Set<Object> target) {
        // It is not sufficient for the target set to contain an object that is *equal* to the mapped
        //  object; rather, we require the mapped object itself to be in the target set. We rely on
        //  IdentityHashMap.contains() to test for identity to achieve this.
        final List<Object> toBeRemoved = new ArrayList<Object>();
        
        for(Object o: target) {
            if(! shadowList.containsKey(o)) {
                toBeRemoved.add(o);
            }
        }
        // We do not use Iterator.remove because it is an optional operation
        target.removeAll(toBeRemoved);
        
        // Now all elements in the target collection are actual results of mapping. We only
        //  have to add the missing elements
        target.addAll(shadowList.keySet());
    }
    
    //TODO List: replace 'equal' entries with 'same' entries
    
    private Collection<?> asCollection (Object o) {
        if (o instanceof Collection <?>) {
            return (Collection<?>) o;
        }
        
        final Object[] arr = (Object[]) o;
        return Arrays.asList(arr);
    }
}
