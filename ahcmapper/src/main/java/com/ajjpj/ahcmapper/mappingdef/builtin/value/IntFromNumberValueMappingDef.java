package com.ajjpj.ahcmapper.mappingdef.builtin.value;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.AhcValueMappingDef;


public class IntFromNumberValueMappingDef implements AhcValueMappingDef <Number, Integer> {
    @Override
    public boolean canHandle(Class<?> sourceClass, Class<?> targetClass) {
        return (targetClass == Integer.class || targetClass == Integer.TYPE) && 
        (Number.class.isAssignableFrom (sourceClass) || sourceClass == Long.TYPE || sourceClass == Short.TYPE || sourceClass == Byte.TYPE);
    }

    @Override
    public boolean handlesNull() {
        return false;
    }

    @Override
    public Integer map (Number source, AhcMapperWorker worker) {
        if (source == null) {
            return null;
        }
        
        return source.intValue();
    }
}
