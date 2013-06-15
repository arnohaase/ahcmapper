package com.ajjpj.ahcmapper.mappingdef.composite.propertybased;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class MethodBasedPropertyAccessor implements AhcPropertyAccessor {
    private final String name;
    private final Class<?> type;
    private final Class<?> elementType;

    private final Class<?> ownerType;
    
    private final boolean isPrimary;
    
    private final Method getter;
    private final Method setter;
    
    public MethodBasedPropertyAccessor(String name, Class<?> type, Class<?> elementType, boolean isPrimary, Method getter, Method setter, Class<?> ownerType) {
        if(getter == null && setter == null) {
            throw new IllegalArgumentException ("either setter or getter must be not-null");
        }
        this.name = name;
        this.type = type;
        this.elementType = elementType;
        this.isPrimary = isPrimary;
        this.getter = getter;
        this.setter = setter;
        
        this.ownerType = ownerType;
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
    public Class<?> getOwnerType() {
        return ownerType;
    }
    
    @Override
    public boolean isReadable() {
        return getter != null;
    }
    
    @Override
    public boolean isWritable() {
        return setter != null;
    }
    
    @Override
    public Object getValue(Object parent) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return getter.invoke(parent);
    }

    @Override
    public void setValue(Object parent, Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        setter.invoke(parent, value);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + getName() + " (" + isPrimary + "): " + isReadable() + " / " + isWritable() + ")";
    }
}
