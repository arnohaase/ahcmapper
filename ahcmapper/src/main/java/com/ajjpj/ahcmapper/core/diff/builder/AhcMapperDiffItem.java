package com.ajjpj.ahcmapper.core.diff.builder;


public interface AhcMapperDiffItem {
    Object getOldTargetMarker();
    Object getNewTargetMarker();
    String getPropertyIdentifier();
    Object getOldValue();
    Object getNewValue();
}