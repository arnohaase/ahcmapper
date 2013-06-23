package com.ajjpj.ahcmapper.core.diff;

import com.ajjpj.ahcmapper.core.diff.builder.AhcMapperDiffItem;
import com.ajjpj.ahcmapper.core.impl.DiffPathMarker;

public class StepDetails {
    private final Object fromSource1;
    private final Object fromSource2;
    private final Object fromTarget1;
    private final Object fromTarget2;
    private final Object toSource1;
    private final Object toSource2;
    private final Object toTarget1;
    private final Object toTarget2;
    private final String propertyName;
    
    StepDetails(DiffPathMarker parentMarker, DiffPathMarker curMarker) {
        this.fromSource1 = parentMarker.getSource1();
        this.fromSource2 = parentMarker.getSource2();
        this.fromTarget1 = parentMarker.getTargetMarker1();
        this.fromTarget2 = parentMarker.getTargetMarker2();
        this.toSource1 = curMarker.getSource1();
        this.toSource2 = curMarker.getSource2();
        this.toTarget1 = curMarker.getTargetMarker1();
        this.toTarget2 = curMarker.getTargetMarker2();
        this.propertyName = curMarker.getPropertyName();
    }

    StepDetails(Object parentSource1, Object parentSource2, AhcMapperDiffItem item) {
        this.fromSource1 = parentSource1;
        this.fromSource2 = parentSource2;
        this.fromTarget1 = item.getOldTargetMarker();
        this.fromTarget2 = item.getNewTargetMarker();
        this.toSource1 = null;
        this.toSource2 = null;
        this.toTarget1 = null;
        this.toTarget2 = null;
        this.propertyName = item.getPropertyIdentifier();
    }
    
    public Object getParentSource1() {
        return fromSource1;
    }
    public Object getParentSource2() {
        return fromSource2;
    }

    public Object getParentTarget1() {
        return fromTarget1;
    }
    public Object getParentTarget2() {
        return fromTarget2;
    }

    public Object getChildSource1() {
        return toSource1;
    }
    public Object getChildSource2() {
        return toSource2;
    }
    
    public Object getChildTarget1() {
        return toTarget1;
    }
    public Object getChildTarget2() {
        return toTarget2;
    }
    
    public String getPropertyName() {
        return propertyName;
    }
}