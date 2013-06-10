package com.ajjpj.ahcmapper.mappingdef.builtin.value;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.AhcValueMappingDef;


public class ShortFromNumberValueMappingDef implements AhcValueMappingDef <Number, Short> {
    @Override
    public boolean canHandle(Class<?> sourceClass, Class<?> targetClass) {
        return (targetClass == Short.class || targetClass == Short.TYPE) && 
        (Number.class.isAssignableFrom (sourceClass) || sourceClass == Long.TYPE || sourceClass == Integer.TYPE || sourceClass == Byte.TYPE);
    }

    @Override
    public boolean handlesNull() {
        return false;
    }

    @Override
    public Short map (Number source, AhcMapperWorker worker) {
        if (source == null) {
            return null;
        }
        
        return source.shortValue();
    }
}
