package com.ajjpj.ahcmapper.core.equivalence.equals;

import java.util.Collection;
import java.util.IdentityHashMap;

import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperUtil;
import com.ajjpj.ahcmapper.core.equivalence.AhcMapperEquivalenceStrategy;
import com.ajjpj.ahcmapper.mappingdef.builtin.internal.EqualsPlaceholderMap;



public class EqualsBasedEquivalenceStrategy implements AhcMapperEquivalenceStrategy {
    private final AhcMapperEqualsProvider equalsProvider;

    public EqualsBasedEquivalenceStrategy(AhcMapperEqualsProvider equalsProvider) {
        this.equalsProvider = equalsProvider;
    }

    @Override
    public <S, T> boolean referToSameTargetElement(S source1, S source2, Class<S> sourceClass, Class<T> targetClass) throws Exception {
        final Object target1 = equalsProvider.createEqualsPlaceholder(source1, sourceClass, targetClass);
        final Object target2 = equalsProvider.createEqualsPlaceholder(source2, sourceClass, targetClass);
        
        return AhcMapperUtil.nullSafeEq(target1, target2);
    }

    @Override
    public <S, T> IdentityHashMap<S, T> findEquivalentInstances(Collection<S> coll1, Collection<T> coll2, Class<S> coll1ElementClass, Class<T> coll2ElementClass) throws Exception {
        final IdentityHashMap<S, T> result = new IdentityHashMap<S, T>();
        
        final EqualsPlaceholderMap<T> lookupMap = new EqualsPlaceholderMap<T> (equalsProvider, coll2, coll2ElementClass);

        for(S o: coll1) {
            @SuppressWarnings("unchecked")
            final T cheapEqualObject = (T) equalsProvider.createEqualsPlaceholder(o, coll1ElementClass, coll2ElementClass);
            
            if(lookupMap.containsKey(cheapEqualObject)) {
                result.put(o, lookupMap.get(cheapEqualObject));
            }
        }
        
        return result;
    }

    @Override
    public <S> Object getTargetEquivalenceMarker(S source, Class<S> sourceClass, Class<?> targetClass) throws Exception {
        return equalsProvider.createEqualsPlaceholder(source, sourceClass, targetClass);
    }
}
