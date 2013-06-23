package com.ajjpj.ahcmapper.mappingdef.composite.propertybased;

import java.lang.reflect.Method;


public class MethodPathBasedPropertyAccessorStep {
    private final Method getter;
    private final boolean treatNullSafe;
    
    public MethodPathBasedPropertyAccessorStep(Method getter, boolean treatNullSafe) {
        this.getter = getter;
        this.treatNullSafe = treatNullSafe;
    }

    public Method getGetter() {
        return getter;
    }

    public boolean getTreatNullSafe() {
        return treatNullSafe;
    }
}
