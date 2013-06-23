package com.ajjpj.ahcmapper.mappingdef.builtin.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.ajjpj.ahcmapper.core.equivalence.equals.AhcMapperEqualsProvider;



public class EqualsPlaceholderMap<T> {
    private final AhcMapperEqualsProvider.CompareStrategy compareStrategy;
    private final Map<EqualsPlaceholder, T> map = new HashMap<EqualsPlaceholder, T>();

    public EqualsPlaceholderMap(AhcMapperEqualsProvider equalsProvider, Collection<T> elements, Class<T> elementClass) throws Exception {
        this.compareStrategy = equalsProvider.getCompareStrategy(elementClass);
        
        for (T o: elements) {
            final EqualsPlaceholder key = createPlaceholder(o);
            map.put(key, o);
        }
    }
    
    private EqualsPlaceholder createPlaceholder(T o) {
        return new EqualsPlaceholder(compareStrategy, o);
    }
    
    public boolean containsKey(T o) {
        return map.containsKey(createPlaceholder(o));
    }
    
    public T get(T key) {
        return map.get(createPlaceholder(key));
    }
}
