package com.ajjpj.ahcmapper.mappingdef;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.AhcObjectMappingDef;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperPath;
import com.ajjpj.ahcmapper.core.diff.AhcMapperDiffBuilder;


public abstract class AhcCacheableObjectMappingDef<S, T> implements AhcObjectMappingDef<S, T>{
    @Override
    public final T map(S source, Class<? extends S> sourceClass, Class<?> sourceElementClass, T oldTarget, Class<? extends T> targetClass, Class<?> targetElementClass, AhcMapperPath path, AhcMapperWorker worker) throws Exception {
        if(source == null) {
            return null;
        }
        
        final T newTarget = worker.createOrProvideTargetInstance(source, oldTarget, targetClass);
        map(source, sourceElementClass, newTarget, targetElementClass, path, worker);
        
        return newTarget;
    }
    
    public final boolean isCacheable() {
        return true;
    }
    
    public abstract void map (S source, Class<?> sourceElementClass, T target, Class<?> targetElementClass, AhcMapperPath path, AhcMapperWorker worker) throws Exception;
    
    @Override
    public final void diff(S source1, S source2, Class<? extends S> sourceClass, Class<?> sourceElementClass, Class<? extends T> targetClass, Class<?> targetElementClass, 
            AhcMapperPath targetPath, AhcMapperDiffBuilder diff, AhcMapperWorker worker) throws Exception {
//        if(source1 != null &&  source2 != null) {
            doDiff(source1, source2, sourceClass, sourceElementClass, targetClass, targetElementClass, targetPath, diff, worker);
//        }
    }
    
    public abstract void doDiff(S source1, S source2, Class<? extends S> sourceClass, Class<?> sourceElementClass, Class<? extends T> targetClass, Class<?> targetElementClass, 
            AhcMapperPath targetPath, AhcMapperDiffBuilder diff, AhcMapperWorker worker) throws Exception;
}
