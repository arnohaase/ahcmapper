package com.ajjpj.ahcmapper.mappingdef.builtin.value;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.AhcValueMappingDef;


public class CharToStringMappingDef implements AhcValueMappingDef <Character, String> {
    @Override
    public boolean canHandle (Class<?> sourceClass, Class<?> targetClass) {
        return (sourceClass == Character.class || sourceClass == Character.TYPE) && targetClass == String.class;
    }

    @Override
    public boolean handlesNull() {
        return false;
    }
    
    @Override
    public String map (Character source, AhcMapperWorker worker) {
        if (source == null) {
            return null;
        }
        return String.valueOf (source);
    }
}
