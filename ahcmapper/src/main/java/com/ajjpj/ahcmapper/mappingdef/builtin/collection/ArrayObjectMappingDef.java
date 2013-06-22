package com.ajjpj.ahcmapper.mappingdef.builtin.collection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.AhcObjectMappingDef;
import com.ajjpj.ahcmapper.core.WorkItem;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperPath;
import com.ajjpj.ahcmapper.core.diff.AhcMapperDiffBuilder;


public class ArrayObjectMappingDef implements AhcObjectMappingDef<Object, Object> {

    @Override
    public boolean canHandle(Class<?> sourceClass, Class<?> sourceElementClass, Class<?> targetClass, Class<?> targetElementClass) {
        final boolean sourceClassCondition = sourceClass.isArray() || Collection.class.isAssignableFrom(sourceClass);
        final boolean targetClassCondition = targetClass.isArray();
        
        return sourceClassCondition && targetClassCondition;
    }

    @Override
    public boolean isCacheable() {
        return true;
    }

    @Override
    public void diff(Object source1, Object source2, Class<?> sourceClass, Class<?> sourceElementClass, Class<?> targetClass, Class<?> targetElementClass,
            AhcMapperPath targetPath, AhcMapperDiffBuilder diff, AhcMapperWorker worker) throws Exception {
        
        worker.diff(targetPath.tail(), targetPath.head(), source1, source2, sourceClass, sourceElementClass, List.class, targetClass.getComponentType(), diff, true);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object map(Object source, Class<?> sourceClass, Class<?> sourceElementClass, 
            Object target, Class<?> targetClass, Class<?> targetElementClass, 
            AhcMapperPath path, final AhcMapperWorker worker) throws Exception {
        
        if(source == null) {
            return null;
        }
        
        final Object newTarget = createTarget(source, sourceClass, sourceElementClass, target, targetClass, targetElementClass);
        final AtomicReference<List<Object>> resultHolder = new AtomicReference<List<Object>>();
        
        final AhcMapperWorker.ResultHandler<List<Object>> resultHandler = new AhcMapperWorker.ResultHandler<List<Object>>() {
            @Override
            public void handle(List<Object> targetObject) throws Exception {
                resultHolder.set(targetObject);

                // scheduling here ensures that it the new item is run only after the elements were added to the list by the list mapping
                worker.scheduleWorkItem(new WorkItem() {
                    @Override
                    public void run() throws Exception {
                        if(resultHolder.get().size() != Array.getLength(newTarget)) {
                            throw new IllegalStateException("list mapping reduced number of entries - inconsistency mapping to array");
                        }
                        for(int i=0; i<resultHolder.get().size(); i++) {
                            Array.set(newTarget, i, resultHolder.get().get(i)); //TODO primitive arrays
                        }
                    }
                }, true);
            }
        };

        worker.map(path.tail(), path.head(), 
                source, sourceClass, sourceElementClass, new ArrayList<Object>(Array.getLength(newTarget)), (Class) List.class, targetElementClass, 
                resultHandler, true);
        
        return newTarget;
    }

    private Object createTarget(Object source, Class<?> sourceClass, Class<?> sourceElementClass, Object target, Class<?> targetClass, Class<?> targetElementClass) {
        if(sourceClass.isArray()) {
            if(target != null && 
                    sourceClass.equals(targetClass) && 
                    sourceClass.getComponentType() == targetClass.getComponentType() &&
                    Array.getLength(source) == Array.getLength(target)) {
                return target;
            }
            return Array.newInstance(targetElementClass, Array.getLength(source));
        }
        else {
            if(target != null && 
                    sourceClass.getComponentType().equals(targetClass.getComponentType()) &&
                    ((Collection<?>) source).size() == Array.getLength(target)) {
                return target;
            }
            return Array.newInstance(targetElementClass, ((Collection<?>) source).size());
        }
    }
}
