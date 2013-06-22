package com.ajjpj.ahcmapper.core;

import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperPath;
import com.ajjpj.ahcmapper.core.diff.AhcMapperDiffBuilder;
import com.ajjpj.ahcmapper.core.equivalence.AhcMapperEquivalenceStrategy;


public interface AhcMapperWorker {
    <S, T> void map(AhcMapperPath path, String segmentIdentifier, 
            S source, Class<? extends S> sourceClass, Class<?> sourceElementClass, 
            T target, Class<? extends T> targetClass, Class<?> targetElementClass, 
            ResultHandler<T> resultHandler, boolean isPrimary) throws Exception;

    void scheduleWorkItem(WorkItem workItem, boolean isPrimary);
    
    <S, T> T createOrProvideTargetInstance(S source, T oldTarget, Class<? extends T> targetClass) throws Exception;
    AhcMapperEquivalenceStrategy getEquivalenceStrategy();
    
    interface ResultHandler <T> {
        void handle(T targetObject) throws Exception;
        
        ResultHandler<Object> NULL_HANDLER = new ResultHandler<Object>() {
            @Override
            public void handle(Object targetObject) throws Exception {
            }
        };
    }

    <S, T> void diff(AhcMapperPath targetPath, String segmentIdentifier, 
            S source1, S source2, Class<? extends S> sourceClass, Class<?> sourceElementClass, 
            Class<? extends T> targetClass, Class<?> targetElementClass, 
            AhcMapperDiffBuilder diff, boolean isPrimary) throws Exception;

    /**
     * For the (rare) situation that one (or both) target value can not be obtained by mapping
     *  a source value, e.g. because there is no parent source object and the caller provides a 
     *  default target value.
     */
    <S, T> void diff(AhcMapperPath targetPath, String segmentIdentifier, 
            S source1, S source2, Class<? extends S> sourceClass, Class<?> sourceElementClass, 
            Class<? extends T> targetClass, Class<?> targetElementClass, 
            AhcMapperDiffBuilder diff, boolean isPrimary,
            T optionalTarget1, T optionalTarget2) throws Exception;

    /**
     * Collections add their special 'element added / removed' diff items - the generic 'ref changed'
     *  diff items are superfluous in those situations.
     */
    <S, T> void diff(AhcMapperPath targetPath, String segmentIdentifier,
                     S source1, S source2, Class<? extends S> sourceClass, Class<?> sourceElementClass,
                     Class<? extends T> targetClass, Class<?> targetElementClass,
                     AhcMapperDiffBuilder diff, boolean isPrimary,
                     T optionalTarget1, T optionalTarget2,
                     boolean suppressRefChangeCheck) throws Exception;
}
