package com.ajjpj.ahcmapper.core.impl;


public class DiffPathMarker {
    private final Object source1;
    private final Object source2;
    private final Object targetMarker1;
    private final Object targetMarker2;
    private final String propertyName;
    
    public <S> DiffPathMarker(S source1, S source2, Object targetMarker1, Object targetMarker2, String propertyName) {
        this.source1 = source1;
        this.source2 = source2;
        this.targetMarker1 = targetMarker1;
        this.targetMarker2 = targetMarker2;
        this.propertyName = propertyName;
    }

    public Object getSource1() {
        return source1;
    }
    public Object getSource2() {
        return source2;
    }

    public Object getTargetMarker1() {
        return targetMarker1;
    }
    public Object getTargetMarker2() {
        return targetMarker2;
    }
    
    public String getPropertyName() {
        return propertyName;
    }
    
    @Override
    public String toString() {
        return "DiffPathMarker [" + source1 + " / " + source2 + " -> " + targetMarker1 + " / " + targetMarker2 + ": " + propertyName + "]";
    }
}
