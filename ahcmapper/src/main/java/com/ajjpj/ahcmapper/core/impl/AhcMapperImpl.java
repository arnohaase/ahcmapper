package com.ajjpj.ahcmapper.core.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.ajjpj.ahcmapper.AhcMapper;
import com.ajjpj.ahcmapper.core.crosscutting.AhcDeProxyStrategy;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperExceptionHandler;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperInstanceProvider;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperLogger;
import com.ajjpj.ahcmapper.core.diff.AhcMapperDiff;
import com.ajjpj.ahcmapper.core.diff.AhcMapperDiffBuilder;
import com.ajjpj.ahcmapper.core.equivalence.AhcMapperEquivalenceStrategy;



public class AhcMapperImpl implements AhcMapper {
    private final AhcMappingDefProvider mappingProvider;
    private final AhcDeProxyStrategy deProxyStrategy;
    private final AhcMapperInstanceProvider instanceProvider;
    private final AhcMapperExceptionHandler exceptionHandler;
    private final AhcMapperEquivalenceStrategy equivalenceStrategy;
    private final AhcMapperLogger logger;

    public AhcMapperImpl(AhcMappingDefProvider mappingProvider, AhcDeProxyStrategy deProxyStrategy, AhcMapperInstanceProvider instanceProvider, AhcMapperExceptionHandler exceptionHandler,
            AhcMapperEquivalenceStrategy equivalenceStrategy, AhcMapperLogger logger) {
        this.mappingProvider = mappingProvider;
        this.deProxyStrategy = deProxyStrategy;
        this.instanceProvider = instanceProvider;
        this.exceptionHandler = exceptionHandler;
        this.equivalenceStrategy = equivalenceStrategy;
        this.logger = logger;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S, T> T map(S source, T target) {
        return (T) map(source, source.getClass(), target, target.getClass());
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <S, T> T map(S source, Class<T> targetClass) {
        if(source == null) {
            return null;
        }
        return (T) map(source, (Class) source.getClass(), targetClass);
    }

    @Override
    public <S, T> T map(S source, Class<? extends S> sourceClass, Class<T> targetClass) {
        return map(source, sourceClass, null, targetClass);
    }

    @Override
    public <S, T> T map(S source, Class<? extends S> sourceClass, T target, Class<? extends T> targetClass) {
        return map(source, sourceClass, null, target, targetClass, null);
    }

    @Override
    public <S, T> T map(S source, Class<? extends S> sourceClass, Class<?> sourceElementClass, T target, Class<? extends T> targetClass, Class<?> targetElementClass) {
        try {
            return new AhcMapperWorkerImpl(mappingProvider, deProxyStrategy, instanceProvider, equivalenceStrategy, logger).mapRoot(source, sourceClass, sourceElementClass, target, targetClass, targetElementClass);
        } catch (Exception exc) {
            exceptionHandler.handle(exc);
            return null; // for the compiler
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S, T> List<T> mapList(Collection<S> source, Class<S> sourceElementClass, Class<T> targetElementClass) {
        return map(source, Collection.class, sourceElementClass, null, List.class, targetElementClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S, T> Set<T> mapSet(Collection<S> source, Class<S> sourceElementClass, Class<T> targetElementClass) {
        return map(source, Collection.class, sourceElementClass, null, Set.class, targetElementClass);
    }
    
    @Override
    public <S, T> AhcMapperDiff diff(S source1, S source2, Class<S> sourceClass, Class<T> targetClass) {
        return diff(source1, source2, sourceClass, null, targetClass, null);
    }
    
    @Override
    public <S, T> AhcMapperDiff diff(S source1, S source2, Class<S> sourceClass, Class<?> sourceElementClass, Class<T> targetClass, Class<?> targetElementClass) {
        try {
            final AhcMapperDiffBuilder diff = new AhcMapperWorkerImpl(mappingProvider, deProxyStrategy, instanceProvider, equivalenceStrategy, logger)
                .diffRoot(source1, source2, sourceClass, sourceElementClass, targetClass, targetElementClass);
            return diff.build();
        }
        catch (Exception exc) {
            exceptionHandler.handle(exc);
            return null; // for the compiler
        }
    }
}
