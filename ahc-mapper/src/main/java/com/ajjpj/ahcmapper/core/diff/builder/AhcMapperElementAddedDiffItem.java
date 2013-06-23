package com.ajjpj.ahcmapper.core.diff.builder;


public class AhcMapperElementAddedDiffItem extends AbstractAhcMapperDiffItem {
    public AhcMapperElementAddedDiffItem(Object oldTargetColl, Object newTargetColl, Object addedElement) {
        super(oldTargetColl, newTargetColl, "element", null, addedElement);
    }
}