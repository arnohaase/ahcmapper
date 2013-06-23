package com.ajjpj.ahcmapper.core.equivalence;


public interface AhcMapperPartialEquivalenceStrategy extends AhcMapperEquivalenceStrategy {
    boolean canHandle(Class<?> sourceClass, Class<?> targetClass) throws Exception;
}
