package com.ajjpj.ahcmapper.mappingdef.builtin.value;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.AhcValueMappingDef;


public class PrimitiveIdentityValueMappingDef implements AhcValueMappingDef <Object, Object> {
    @Override
    public boolean canHandle(Class<?> sourceClass, Class<?> targetClass) {
        if (sourceClass == Long.class || sourceClass == Long.TYPE) {
            return targetClass == Long.class || targetClass == Long.TYPE;
        }
        if (sourceClass == Integer.class || sourceClass == Integer.TYPE) {
            return targetClass == Integer.class || targetClass == Integer.TYPE;
        }
        if (sourceClass == Short.class || sourceClass == Short.TYPE) {
            return targetClass == Short.class || targetClass == Short.TYPE;
        }
        if (sourceClass == Character.class || sourceClass == Character.TYPE) {
            return targetClass == Character.class || targetClass == Character.TYPE;
        }
        if (sourceClass == Byte.class || sourceClass == Byte.TYPE) {
            return targetClass == Byte.class || targetClass == Byte.TYPE;
        }
        
        if (sourceClass == Double.class || sourceClass == Double.TYPE) {
            return targetClass == Double.class || targetClass == Double.TYPE;
        }
        if (sourceClass == Float.class || sourceClass == Float.TYPE) {
            return targetClass == Float.class || targetClass == Float.TYPE;
        }

        if (sourceClass == Boolean.class || sourceClass == Boolean.TYPE) {
            return targetClass == Boolean.class || targetClass == Boolean.TYPE;
        }

        if (sourceClass == String.class) {
            return targetClass == String.class;
        }
        
        return false;
    }

    @Override
    public boolean handlesNull() {
        return false;
    }

    @Override
    public Object map(Object source, AhcMapperWorker worker) {
        return source;
    }
}
