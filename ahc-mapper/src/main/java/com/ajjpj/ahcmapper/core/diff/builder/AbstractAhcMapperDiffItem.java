package com.ajjpj.ahcmapper.core.diff.builder;

import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperUtil;


public abstract class AbstractAhcMapperDiffItem implements AhcMapperDiffItem {
    private final Object oldTargetMarker;
    private final Object newTargetMarker;
    private final String propertyIdentifier;
    private final Object oldValue;
    private final Object newValue;

    public AbstractAhcMapperDiffItem(Object oldTargetMarker, Object newTargetMarker, String propertyIdentifier, Object oldValue, Object newValue) {
        this.oldTargetMarker = oldTargetMarker;
        this.newTargetMarker = newTargetMarker;
        this.propertyIdentifier = propertyIdentifier;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public Object getOldTargetMarker() {
        return oldTargetMarker;
    }
    @Override
    public Object getNewTargetMarker() {
        return newTargetMarker;
    }
    
    @Override
    public String getPropertyIdentifier() {
        return propertyIdentifier;
    }

    @Override
    public Object getOldValue() {
        return oldValue;
    }

    @Override
    public Object getNewValue() {
        return newValue;
    }
    
    @Override
    public boolean isCausedByStructuralChange() {
        return ! AhcMapperUtil.nullSafeEq(oldTargetMarker, newTargetMarker);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + oldTargetMarker + "/" + newTargetMarker + "." + propertyIdentifier + ": " + oldValue + " -> " + newValue + "]";
    }
}