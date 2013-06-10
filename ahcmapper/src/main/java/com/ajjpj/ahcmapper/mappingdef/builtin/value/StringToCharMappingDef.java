package com.ajjpj.ahcmapper.mappingdef.builtin.value;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.AhcValueMappingDef;


public class StringToCharMappingDef implements AhcValueMappingDef <String, Character> {
    @Override
    public boolean canHandle(Class<?> sourceClass, Class<?> targetClass) {
        return sourceClass == String.class && (targetClass == Character.class || targetClass == Character.TYPE);
    }

    @Override
    public boolean handlesNull() {
        return false;
    }
    
    @Override
    public Character map (String source, AhcMapperWorker worker) {
        if (source == null || "".equals(source.trim())) {
            return null;
        }
        
        return source.charAt (0);
    }
}
