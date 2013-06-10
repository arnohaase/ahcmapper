package com.ajjpj.ahcmapper.mappingdef.builtin.value;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.AhcValueMappingDef;


public class LongFromNumberValueMappingDef implements AhcValueMappingDef <Number, Long> {
    @Override
    public boolean canHandle(Class<?> sourceClass, Class<?> targetClass) {
        return (targetClass == Long.class || targetClass == Long.TYPE) && 
          (Number.class.isAssignableFrom (sourceClass) || sourceClass == Integer.TYPE || sourceClass == Short.TYPE || sourceClass == Byte.TYPE);
    }

    @Override
    public boolean handlesNull() {
        return false;
    }

    @Override
    public Long map (Number source, AhcMapperWorker worker) {
        if (source == null) {
            return null;
        }
        
        return source.longValue();
    }
}
