package com.ajjpj.ahcmapper.core;


public interface AhcObjectFactory {
    <T> T create(Class<T> cls, Object forSource) throws Exception;
}
