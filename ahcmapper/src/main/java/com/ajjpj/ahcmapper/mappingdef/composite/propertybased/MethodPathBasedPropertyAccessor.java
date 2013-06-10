package com.ajjpj.ahcmapper.mappingdef.composite.propertybased;

import java.lang.reflect.Method;
import java.util.List;


public class MethodPathBasedPropertyAccessor implements AhcPropertyAccessor {
    private final String name;
    private final Class<?> type;
    private final Class<?> elementType;
    private final boolean isPrimary;

    private final List<MethodPathBasedPropertyAccessorStep> steps;
    
    private final Method finalGetter;
    private final Method finalSetter;
    private final boolean nullSafeOnFinalStep;
    
    public MethodPathBasedPropertyAccessor(String name, Class<?> type, Class<?> elementType, boolean isPrimary, List<MethodPathBasedPropertyAccessorStep> steps, Method finalGetter, Method finalSetter, boolean nullSafeOnFinalStep) {
        if(finalGetter == null && finalSetter == null) {
            throw new IllegalArgumentException ("either setter or getter must be not-null");
        }
        this.name = name;
        this.type = type;
        this.elementType = elementType;
        this.isPrimary = isPrimary;
        this.steps = steps;
        this.finalGetter = finalGetter;
        this.finalSetter = finalSetter;
        this.nullSafeOnFinalStep = nullSafeOnFinalStep;
    }

    @Override
    public String getName() {
        return name;
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
    public boolean isPrimary() {
        return isPrimary;
    }

    @Override
    public boolean isReadable() {
        return finalGetter != null;
    }

    @Override
    public boolean isWritable() {
        return finalSetter != null;
    }

    @Override
    public Object getValue(Object parent) throws Exception {
        Object cur = parent;
        for (MethodPathBasedPropertyAccessorStep step: steps) {
            if(cur == null && step.getTreatNullSafe()) {
                return null;
            }
            cur = step.getGetter().invoke(cur);
        }
        if(cur == null && nullSafeOnFinalStep) {
            return null;
        }
        return finalGetter.invoke(cur);
    }

    @Override
    public void setValue(Object parent, Object value) throws Exception {
        Object cur = parent;
        for (MethodPathBasedPropertyAccessorStep step: steps) {
            if(cur == null && step.getTreatNullSafe()) {
                return;
            }
            cur = step.getGetter().invoke(cur);
        }
        if(cur == null && nullSafeOnFinalStep) {
            return;
        }
        finalSetter.invoke(cur, value);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + getName() + " [" + isPrimary + "]: " + isReadable() + ", " + isWritable() + ")"; 
    }
}
