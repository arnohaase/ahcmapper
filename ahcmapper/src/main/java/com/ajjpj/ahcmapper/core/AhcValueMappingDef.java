package com.ajjpj.ahcmapper.core;


public interface AhcValueMappingDef<S, T> {
    boolean canHandle(Class<?> sourceClass, Class<?> targetClass);
    boolean handlesNull();
    
    T map(S source, AhcMapperWorker worker) throws Exception; //TODO params
}
