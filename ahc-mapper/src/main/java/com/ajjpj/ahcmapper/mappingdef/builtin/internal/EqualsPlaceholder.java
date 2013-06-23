package com.ajjpj.ahcmapper.mappingdef.builtin.internal;

import com.ajjpj.ahcmapper.core.equivalence.equals.AhcMapperEqualsProvider;


//TODO move to 'equivalence' package
class EqualsPlaceholder {
    private final AhcMapperEqualsProvider.CompareStrategy compareStrategy;
    private final Object inner;
    
    public EqualsPlaceholder(AhcMapperEqualsProvider.CompareStrategy compareStrategy, Object inner) {
        this.compareStrategy = compareStrategy;
        this.inner = inner;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof EqualsPlaceholder)) {
            return false;
        }
        
        return compareStrategy.isEqual(inner, ((EqualsPlaceholder) obj).inner);
    }

    @Override
    public int hashCode() {
        return compareStrategy.hashCode(inner);
    }
}
