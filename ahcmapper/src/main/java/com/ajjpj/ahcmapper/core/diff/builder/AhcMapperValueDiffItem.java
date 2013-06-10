package com.ajjpj.ahcmapper.core.diff.builder;


public class AhcMapperValueDiffItem extends AbstractAhcMapperDiffItem {
    public AhcMapperValueDiffItem(Object oldTargetMarker, Object newTargetMarker, String propertyIdentifier, Object oldValue, Object newValue) {
        super(oldTargetMarker, newTargetMarker, propertyIdentifier, oldValue, newValue);
    }
}