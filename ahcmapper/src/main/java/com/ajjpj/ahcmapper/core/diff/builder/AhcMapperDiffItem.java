package com.ajjpj.ahcmapper.core.diff.builder;


public interface AhcMapperDiffItem {
    Object getOldTargetMarker();
    Object getNewTargetMarker();
    String getPropertyIdentifier();
    Object getOldValue();
    Object getNewValue();
    
    /**
     * This (potentially derived) property allows to differentiate between changes found
     *  during regular traversal of the object graph and those below a 'Ref Change'. If this
     *  method returns <code>false</code>, this item may be redundant / irrelevant: If
     *  the reference was changed from 'customer 1' to 'customer 2' (the ref change), the fact that they have
     *  different names may or may not be of interest.
     */
    boolean hasSameParent();
}