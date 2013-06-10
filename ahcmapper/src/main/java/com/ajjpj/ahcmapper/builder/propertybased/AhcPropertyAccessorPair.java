package com.ajjpj.ahcmapper.builder.propertybased;

import com.ajjpj.ahcmapper.mappingdef.composite.propertybased.AhcPropertyAccessor;


public class AhcPropertyAccessorPair {
    private final AhcPropertyAccessor sourceAccessor;
    private final AhcPropertyAccessor targetAccessor;
    
    public AhcPropertyAccessorPair(AhcPropertyAccessor sourceAccessor, AhcPropertyAccessor targetAccessor) {
        this.sourceAccessor = sourceAccessor;
        this.targetAccessor = targetAccessor;
    }

    public AhcPropertyAccessor getSourceAccessor() {
        return sourceAccessor;
    }
    public AhcPropertyAccessor getTargetAccessor() {
        return targetAccessor;
    }
    
    @Override
    public String toString() {
        return "AccessorPair (" + sourceAccessor.getName() + " -> " + targetAccessor + ")";
    }
}
