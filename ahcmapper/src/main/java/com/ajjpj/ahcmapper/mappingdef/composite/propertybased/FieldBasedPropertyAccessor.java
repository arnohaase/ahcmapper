package com.ajjpj.ahcmapper.mappingdef.composite.propertybased;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class FieldBasedPropertyAccessor implements AhcPropertyAccessor {
    private final String name;
    private final Class<?> type;
    private final Class<?> elementType;
    
    private final boolean isPrimary;
    
    private final Field field;
    
    public FieldBasedPropertyAccessor(String name, Class<?> type, Class<?> elementType, boolean isPrimary, Field field) {
        this.name = name;
        this.type = type;
        this.elementType = elementType;
        this.isPrimary = isPrimary;
        this.field = field;
        
        if(! Modifier.isPublic(field.getModifiers()) && ! field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isPrimary() {
        return isPrimary;
    }
    
    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Class<?> getElementType() {
        return elementType;
    }

    @Override
    public boolean isReadable() {
        return true;
    }
    
    @Override
    public boolean isWritable() {
        return ! Modifier.isFinal(field.getModifiers());
    }
    
    @Override
    public Object getValue(Object parent) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return field.get(parent);
    }

    @Override
    public void setValue(Object parent, Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        field.set(parent, value);
    }
}
