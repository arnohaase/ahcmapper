package com.ajjpj.ahcmapper.core;

import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperPath;
import com.ajjpj.ahcmapper.core.diff.AhcMapperDiffBuilder;

public interface AhcObjectMappingDef<S, T> {
    boolean canHandle(Class<?> sourceClass, Class<?> sourceElementClass, Class<?> targetClass, Class<?> targetElementClass);
    boolean isCacheable ();
    
    T map (S source, Class<? extends S> sourceClass, Class<?> sourceElementClass, T target, Class<? extends T> targetClass, Class<?> targetElementClass, AhcMapperPath path, AhcMapperWorker worker) throws Exception;
    void diff(S source1, S source2, Class<? extends S> sourceClass, Class<?> sourceElementClass, Class<? extends T> targetClass, Class<?> targetElementClass, 
            AhcMapperPath targetPath, AhcMapperDiffBuilder diff, AhcMapperWorker worker) throws Exception;
}
