package com.ajjpj.ahcmapper.core.diff.builder;


public class AhcMapperElementRemovedDiffItem extends AbstractAhcMapperDiffItem {
    public AhcMapperElementRemovedDiffItem(Object oldTargetColl, Object newTargetColl, Object removedElement) {
        super(oldTargetColl, newTargetColl, "element", removedElement, null);
    }
}