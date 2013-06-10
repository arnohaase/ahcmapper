package com.ajjpj.ahcmapper.mappingdef.builtin.value;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.AhcValueMappingDef;


public class EnumValueMappingDef implements AhcValueMappingDef <Enum<?>, Enum<?>>{
    @Override
    public boolean canHandle(Class<?> sourceClass, Class<?> targetClass) {
        return Enum.class.isAssignableFrom (sourceClass) && sourceClass == targetClass;
    }

    @Override
    public boolean handlesNull() {
        return false;
    }

    @Override
    public Enum<?> map (Enum<?> source, AhcMapperWorker worker) {
        return source;
    }
}
