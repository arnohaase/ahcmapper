package com.ajjpj.ahcmapper.core.equivalence;

import java.util.Collection;
import java.util.IdentityHashMap;


public interface AhcMapperEquivalenceStrategy {
    <S, T> boolean referToSameTargetElement(S source1, S source2, Class<S> sourceClass, Class<T> targetClass) throws Exception;
    <S, T> IdentityHashMap<S, T> findEquivalentInstances(Collection<S> coll1, Collection<T> coll2, Class<S> coll1ElementClass, Class<T> coll2ElementClass) throws Exception;
    
    /**
     * The results returned by this method for s1 and s2 are equals iff s1 and s2 refer to the same target object, i.e. would be mapped to the same object. This is
     *  just a (potentially cheaper) alternative to actually returning that object.
     */
    <S> Object getTargetEquivalenceMarker(S source, Class<S> sourceClass, Class<?> targetClass) throws Exception;
}
