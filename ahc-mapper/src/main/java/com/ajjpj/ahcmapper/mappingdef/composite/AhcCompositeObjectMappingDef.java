package com.ajjpj.ahcmapper.mappingdef.composite;

import java.util.Collection;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperPath;
import com.ajjpj.ahcmapper.core.diff.AhcMapperDiffBuilder;
import com.ajjpj.ahcmapper.mappingdef.AhcCacheableObjectMappingDef;



public class AhcCompositeObjectMappingDef<S, T> extends AhcCacheableObjectMappingDef<S, T> {
    private final boolean canHandleSubclasses;
    private final Class<S> sourceClass;
    private final Class<T> targetClass;
    
    private final Collection<? extends AhcMappingPart<?, ?>> parts;

    public AhcCompositeObjectMappingDef(boolean canHandleSubclasses, Class<S> sourceClass, Class<T> targetClass, Collection<? extends AhcMappingPart<?, ?>> parts) {
        this.canHandleSubclasses = canHandleSubclasses;
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        this.parts = parts;
    }

    @Override
    public boolean canHandle(Class<?> sourceClass, Class<?> sourceElementClass, Class<?> targetClass, Class<?> targetElementClass) {
        if(canHandleSubclasses) {
            return this.sourceClass.isAssignableFrom(sourceClass) && this.targetClass.isAssignableFrom(targetClass);
        }
        else {
            return sourceClass == this.sourceClass && targetClass == this.targetClass;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void map(S source, Class<?> sourceElementClass, T target, Class<?> targetElementClass, AhcMapperPath path, AhcMapperWorker worker) throws Exception {
        for (AhcMappingPart part: parts) {
            part.map(source, target, path, worker);
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void doDiff(S source1, S source2, Class<? extends S> sourceClass, Class<?> sourceElementClass, Class<? extends T> targetClass, Class<?> targetElementClass, 
            AhcMapperPath targetPath, AhcMapperDiffBuilder diff, AhcMapperWorker worker) throws Exception {
        for(AhcMappingPart part: parts) {
            part.diff(source1, source2, targetPath, diff, worker);
        }
    }
}
