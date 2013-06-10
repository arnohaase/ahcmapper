package com.ajjpj.ahcmapper.mappingdef.composite.propertybased;


public interface AhcPropertyAccessor {
    String getName();
    Class<?> getType();
    Class<?> getElementType();

    boolean isPrimary();
    
    boolean isReadable();
    boolean isWritable();
    
    Object getValue(Object parent) throws Exception;
    void setValue(Object parent, Object value) throws Exception;
}
