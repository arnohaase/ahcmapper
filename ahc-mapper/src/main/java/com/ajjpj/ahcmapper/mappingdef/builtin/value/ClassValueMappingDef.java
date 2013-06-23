package com.ajjpj.ahcmapper.mappingdef.builtin.value;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.AhcValueMappingDef;


public class ClassValueMappingDef implements AhcValueMappingDef <Class<?>, Class<?>> {
    @Override
    public boolean canHandle (Class<?> sourceClass, Class<?> targetClass) {
        return sourceClass == Class.class && targetClass == Class.class;
    }

    @Override
    public boolean handlesNull() {
        return false;
    }
    
    @Override
    public Class<?> map(Class<?> source, AhcMapperWorker worker) {
        return source;
    }
}
