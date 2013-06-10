package com.ajjpj.ahcmapper.builder;

import com.ajjpj.ahcmapper.core.equivalence.equals.AhcMapperEqualsProvider;


public interface AhcMapperEqualsProviderExtension extends AhcMapperEqualsProvider {
    boolean canHandle (Class<?> sourceClass, Class<?> targetClass);
}
