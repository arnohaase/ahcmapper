package com.ajjpj.ahcmapper.mappingdef.composite.propertybased;

import ognl.Ognl;
import ognl.OgnlException;


public class OgnlPropertyAccessor implements AhcPropertyAccessor {
    private final String expressionString;
    private final Object ognlExpression;

    private final boolean isPrimary;
    
    private final Class<?> type;
    private final Class<?> elementType;

    private final Class<?> ownerType;
    
    public OgnlPropertyAccessor(String expressionString, Class<?> type, Class<?> elementType, boolean isPrimary, Class<?> ownerType) throws OgnlException {
        this.expressionString = expressionString;
        this.ognlExpression = Ognl.parseExpression(expressionString);
        this.type = type;
        this.elementType = elementType;
        this.isPrimary = isPrimary;
        this.ownerType = ownerType;
    }

    @Override
    public String getName() {
        return expressionString;
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
        return true;
    }
    
    @Override
    public boolean isWritable() {
        return true;
    }
    
    @Override
    public Object getValue(Object parent) throws OgnlException {
        return Ognl.getValue(ognlExpression, parent);
    }

    @Override
    public void setValue(Object parent, Object value) throws OgnlException {
        Ognl.setValue(ognlExpression, parent, value);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + getName() + " (" + isPrimary + "): " + isReadable() + ": " + isWritable() + ")";
    }
}
