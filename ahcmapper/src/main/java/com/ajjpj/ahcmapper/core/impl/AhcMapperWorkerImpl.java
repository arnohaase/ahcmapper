package com.ajjpj.ahcmapper.core.impl;

import java.util.LinkedList;
import java.util.Queue;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.AhcValueMappingDef;
import com.ajjpj.ahcmapper.core.WorkItem;
import com.ajjpj.ahcmapper.core.crosscutting.AhcDeProxyStrategy;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperInstanceProvider;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperLogger;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperPath;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperUtil;
import com.ajjpj.ahcmapper.core.diff.AhcMapperDiffBuilder;
import com.ajjpj.ahcmapper.core.equivalence.AhcMapperEquivalenceStrategy;



public class AhcMapperWorkerImpl implements AhcMapperWorker {
    private static final String ROOT_SEGMENT = "root";
    
    final AhcMappingDefProvider mappingProvider;
    final AhcDeProxyStrategy deProxyStrategy;
    private final AhcMapperInstanceProvider instanceProvider;
    final ObjectIdentityCache objectIdentityCache ;
//    private final AhcMapperEqualsProvider equalsProvider;
    final AhcMapperEquivalenceStrategy equivalenceStrategy;
    final AhcMapperLogger logger; 
    
    private final Queue<WorkItem> earlyWorkQueue = new LinkedList<WorkItem>();
    private final Queue<WorkItem> lateWorkQueue  = new LinkedList<WorkItem>();
    
    public AhcMapperWorkerImpl(AhcMappingDefProvider mappingProvider, AhcDeProxyStrategy deProxyStrategy, AhcMapperInstanceProvider instanceProvider, AhcMapperEquivalenceStrategy equivalenceStrategy, AhcMapperLogger logger) {
        this.mappingProvider = mappingProvider;
        this.deProxyStrategy = deProxyStrategy;
        this.instanceProvider = instanceProvider;
        this.equivalenceStrategy = equivalenceStrategy;
        this.logger = logger;
        this.objectIdentityCache = new ObjectIdentityCache(logger);
    }

    @Override
    public void scheduleWorkItem(WorkItem workItem, boolean isEarly) {
        if(isEarly) {
            earlyWorkQueue.add(workItem);
        }
        else {
            lateWorkQueue.add(workItem);
        }
    }
    
    @Override
    public <S, T> void map(AhcMapperPath path, String segmentIdentifier, S source, Class<? extends S> sourceClass, Class<?> sourceElementClass, T target, Class<? extends T> targetClass, Class<?> targetElementClass, 
            ResultHandler<T> resultHandler, boolean isPrimary) throws Exception {
        
        path = path.withSegment(segmentIdentifier, source); 
        
        final AhcValueMappingDef<S, T> valueMapping = mappingProvider.getValueMapping(sourceClass, targetClass);
        if(valueMapping != null) {
            resultHandler.handle(mapValue(valueMapping, source));
            return;
        }
        
        final MappingWorkItem<S, T> workItem = new MappingWorkItem<S, T>(path, source, sourceClass, sourceElementClass, target, targetClass, targetElementClass, resultHandler, this);
        scheduleWorkItem(workItem, isPrimary);
    }
    
    private <S, T> T mapValue(AhcValueMappingDef<S, T> valueMapping, S source) throws Exception {
        if(source == null && ! valueMapping.handlesNull()) {
            return null;
        }
        else {
            return valueMapping.map(source, this);
        }
    }
    
    public <S, T> T mapRoot(S source, Class<? extends S> sourceClass, Class<?> sourceElementClass, T target, Class<? extends T> targetClass, Class<?> targetElementClass) throws Exception {
        final ResultHoldingResultHandler<T> resultHandler = new ResultHoldingResultHandler<T>();
        map(AhcMapperPath.ROOT, ROOT_SEGMENT, source, sourceClass, sourceElementClass, target, targetClass, targetElementClass, resultHandler, true);
        
        doScheduledWork();
        
        return resultHandler.result;
    }

    private void doScheduledWork() throws Exception {
        while(true) { // exit via break
            final WorkItem earlyItem = earlyWorkQueue.poll();
            if(earlyItem != null) {
                earlyItem.run();
                continue;
            }
            
            final WorkItem lateItem = lateWorkQueue.poll();
            if(lateItem != null) {
                lateItem.run();
                continue;
            }
            
            break; // both work queues are empty --> we're finished
        }
    }
    
    public <S, T> AhcMapperDiffBuilder diffRoot(S source1, S source2, Class<? extends S> sourceClass, Class<?> sourceElementClass, Class<T> targetClass, Class<?> targetElementClass) throws Exception {
        final AhcMapperDiffBuilder diff = new AhcMapperDiffBuilder();
        diff(AhcMapperPath.ROOT, ROOT_SEGMENT, source1, source2, sourceClass, sourceElementClass, targetClass, targetElementClass, diff, true);
        
        doScheduledWork();
        
        return diff;
    }
    
    @Override
    public <S, T> void diff(AhcMapperPath targetPath, String segmentIdentifier, 
            S source1, S source2, Class<? extends S> sourceClass, Class<?> sourceElementClass, 
            Class<? extends T> targetClass, Class<?> targetElementClass, 
            AhcMapperDiffBuilder diff, boolean isPrimary) throws Exception {
        
        diff(targetPath, segmentIdentifier, source1, source2, sourceClass, sourceElementClass, targetClass, targetElementClass, diff, isPrimary, null, null);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <S, T> void diff(AhcMapperPath targetPath, String segmentIdentifier, S source1, S source2, Class<? extends S> sourceClass, Class<?> sourceElementClass, Class<? extends T> targetClass, Class<?> targetElementClass, AhcMapperDiffBuilder diff, boolean isPrimary,
            T optionalTarget1, T optionalTarget2) throws Exception {

        final AhcValueMappingDef<S, T> valueMapping = mappingProvider.getValueMapping(sourceClass, targetClass);
        if(valueMapping != null) {
            final T targetValue1 = optionalTarget1 != null ? optionalTarget1 : mapValue(valueMapping, source1);
            final T targetValue2 = optionalTarget2 != null ? optionalTarget2 : mapValue(valueMapping, source2);
            
            //TODO is there a reason to make equality configurable here?
            if(!AhcMapperUtil.nullSafeEq(targetValue1, targetValue2)) {
                final DiffPathMarker parentMarker = (DiffPathMarker) targetPath.getMarker();
                diff.addValueChange(parentMarker.getSource1(), parentMarker.getSource2(), targetValue1, targetValue2, targetPath, segmentIdentifier);
            }
            
            return;
        }

        final DiffWorkItem workItem = new DiffWorkItem (targetPath, segmentIdentifier, source1, source2, sourceClass, sourceElementClass, targetClass, targetElementClass, diff, this, optionalTarget1, optionalTarget2);
        scheduleWorkItem(workItem, isPrimary);
        
    }
    
    @Override
    public <S, T> T createOrProvideTargetInstance(S source, T oldTarget, Class<? extends T> targetClass) throws Exception {
        return  instanceProvider.provideInstance(source, oldTarget, targetClass);
    }

    @Override
    public AhcMapperEquivalenceStrategy getEquivalenceStrategy() {
        return equivalenceStrategy;
    }
    
    private static class ResultHoldingResultHandler<T> implements ResultHandler<T> {
        public T result;

        @Override
        public void handle(T targetObject) {
            result = targetObject;
        }
    }
}
