package com.ajjpj.ahcmapper.core.impl;

import com.ajjpj.ahcmapper.core.AhcObjectMappingDef;
import com.ajjpj.ahcmapper.core.WorkItem;
import com.ajjpj.ahcmapper.core.AhcMapperWorker.ResultHandler;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperPath;
import com.ajjpj.ahcmapper.core.impl.ObjectIdentityCache.ObjectWrapper;


class MappingWorkItem<S, T> implements WorkItem {
    private final AhcMapperPath path;
    private final S sourceRaw;
    private final Class<? extends S> sourceClass;
    private final Class<?> sourceElementClass;
    private final T target;
    private final Class<? extends T> targetClass;
    private final Class<?> targetElementClass;
    private final ResultHandler<T> resultHandler;
    
    private final AhcMapperWorkerImpl worker;
    
    public MappingWorkItem(AhcMapperPath path, 
            S source, Class<? extends S> sourceClass, Class<?> sourceElementClass, T target, Class<? extends T> targetClass, Class<?> targetElementClass, 
            ResultHandler<T> resultHandler,
            AhcMapperWorkerImpl worker) {
        this.path = path;
        this.sourceRaw = source;
        this.sourceClass = sourceClass;
        this.sourceElementClass = sourceElementClass;
        this.target = target;
        this.targetClass = targetClass;
        this.targetElementClass = targetElementClass;
        this.resultHandler = resultHandler;
        this.worker = worker;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() throws Exception {
        worker.logger.debug("running mapping worker: " + sourceRaw + " [" + sourceClass.getSimpleName() + "] -> " + target + " [" + targetClass.getSimpleName() + "]");
        
        final Object source = worker.deProxyStrategy.deProxy(sourceRaw);

        final AhcObjectMappingDef<Object, Object> objectMapping = 
                (AhcObjectMappingDef<Object, Object>) worker.mappingProvider.getObjectMapping(sourceClass, sourceElementClass, targetClass, targetElementClass);
        
        //TODO log 'object mapping not found'
        
        if(objectMapping.isCacheable()) {
            final ObjectWrapper cached = worker.objectIdentityCache.getPreviousMapping(source, targetClass);
            if(cached != null) {
                resultHandler.handle((T) cached.element);
                return;
            }
        }
        
//        final Map <Class<?>, Object> oldContext = new HashMap<Class<?>, Object> ();
//        for (Class<?> ctxKey: om.getContextInfoKeys (from)) {
//            oldContext.put (ctxKey, contextInfo.put (ctxKey, from));
//        }
        
        //TODO targetValue*Holder*?
//        final To toParam = toParamSource != null ? toParamSource.getObject() : null;

        final Object newTarget = objectMapping.map(source, sourceClass, sourceElementClass, target, targetClass, targetElementClass, path, worker);
        if(objectMapping.isCacheable()) {
            worker.objectIdentityCache.registerMapping (source, newTarget, targetClass);
        }
        
        resultHandler.handle((T) newTarget);
        
        //TODO AhcObjectEnhancer
//        for (OmdObjectEnhancer enhancer: enhancers.getEnhancers(fromClass, fromElementClass, toClass, toElementClass)) {
//            enhancer.enhance(from, to);
//        }
        
//        contextInfo.putAll (oldContext);
        
        //TODO 'trace' mechanism --> if there is an exception, log it with the path
    }
}
