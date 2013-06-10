package com.ajjpj.ahcmapper.core.crosscutting;


public interface AhcMapperInstanceProvider {
    <T> T provideInstance(Object source, T oldTarget, Class<? extends T> targetClass) throws Exception;
}
