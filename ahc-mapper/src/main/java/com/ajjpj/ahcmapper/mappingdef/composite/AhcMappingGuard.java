package com.ajjpj.ahcmapper.mappingdef.composite;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperPath;


public abstract class AhcMappingGuard<S, T> implements AhcMappingPart<S, T> {
    private final AhcMappingPart<S, T> inner;
    private final AhcMappingGuardCondition<S, T> condition;
    
    public AhcMappingGuard(AhcMappingPart<S, T> inner, AhcMappingGuardCondition<S, T> condition) {
        this.inner = inner;
        this.condition = condition;
    }

    @Override
    public final void map(S source, T target, AhcMapperPath path, AhcMapperWorker worker) throws Exception {
        if(condition.shouldMap(source, target, path, worker)) {
            inner.map(source, target, path, worker);
        }
    }
    
    public static <S, T> AhcMappingGuardCondition<S, T> reverse (AhcMappingGuardCondition<T, S> condition) {
        if(condition instanceof ReverseCondition) {
            return ((ReverseCondition<T, S>) condition).inner;
        }
        else {
            return new ReverseCondition<S, T> (condition);
        }
    }
    
    private static class ReverseCondition<S, T> implements AhcMappingGuardCondition<S, T> {
        private final AhcMappingGuardCondition<T, S> inner;

        public ReverseCondition(AhcMappingGuardCondition<T, S> inner) {
            this.inner = inner;
        }

        @Override
        public boolean shouldMap(S source, T target, AhcMapperPath path, AhcMapperWorker worker) throws Exception {
            return inner.shouldMap(target, source, path, worker);
        }
    }
}
