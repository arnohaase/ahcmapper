package com.ajjpj.ahcmapper.mappingdef.builtin.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.WorkItem;
import com.ajjpj.ahcmapper.core.AhcMapperWorker.ResultHandler;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperPath;
import com.ajjpj.ahcmapper.core.diff.AhcMapperDiffBuilder;
import com.ajjpj.ahcmapper.core.diff.builder.AhcMapperElementAddedDiffItem;
import com.ajjpj.ahcmapper.core.diff.builder.AhcMapperElementRemovedDiffItem;
import com.ajjpj.ahcmapper.core.equivalence.AhcMapperEquivalenceStrategy;
import com.ajjpj.ahcmapper.mappingdef.AhcCacheableObjectMappingDef;



public abstract class AbstractListObjectMappingDef extends AhcCacheableObjectMappingDef<Object, List<Object>> {
    @Override
    public boolean canHandle(Class<?> sourceClass, Class<?> sourceElementClass, Class<?> targetClass, Class<?> targetElementClass) {
        final boolean sourceClassCondition = sourceClass.isArray() || Collection.class.isAssignableFrom(sourceClass);
        final boolean targetClassCondition = List.class.isAssignableFrom(targetClass);
        
        return sourceClassCondition && targetClassCondition;
    }

    //TODO diff items based on index, duplicates etc.
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void doDiff(Object source1, Object source2, Class<?> sourceClass, Class<?> sourceElementClass, Class<? extends List<Object>> targetClass, Class<?> targetElementClass,
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
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <S> Set<?> mapToTargetColl (Collection<?> sourceColl, Class<?> sourceElementClass, Class<?> targetElementClass, AhcMapperEquivalenceStrategy equiv) throws Exception {
        final Set<Object> result = new HashSet<Object>();
        for(Object source: sourceColl) {
            result.add(equiv.getTargetEquivalenceMarker(source, (Class) sourceElementClass, targetElementClass));
        }
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void map(Object source, Class<?> sourceElementClass, final List<Object> target, Class<?> targetElementClass, AhcMapperPath path, AhcMapperWorker worker) throws Exception {
        final Collection<?> sourceColl = asCollection(source);
        
        final IdentityHashMap<Object, Object> equiv = (IdentityHashMap<Object,Object>) worker.getEquivalenceStrategy().findEquivalentInstances(sourceColl, target, (Class) sourceElementClass, (Class) targetElementClass);

        final List<Object> shadowList = new ArrayList<Object> ();
        int index = 0;
        for (Object sourceElement: sourceColl) {
            final ResultHandler<Object> resultHandler = new ResultHandler<Object>() {
                @Override
                public void handle(Object targetObject) throws Exception {
                    shadowList.add(targetObject);
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

    protected abstract void merge(List<Object> shadowList, List<Object> target);
    
    private Collection<?> asCollection (Object o) {
        if (o instanceof Collection <?>) {
            return (Collection<?>) o;
        }
        
        final Object[] arr = (Object[]) o;
        return Arrays.asList(arr);
    }
}
