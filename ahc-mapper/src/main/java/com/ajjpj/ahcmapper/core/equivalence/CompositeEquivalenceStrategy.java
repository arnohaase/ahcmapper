package com.ajjpj.ahcmapper.core.equivalence;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;


public class CompositeEquivalenceStrategy implements AhcMapperEquivalenceStrategy {
    private final List<AhcMapperPartialEquivalenceStrategy> partialStrategies;

    public CompositeEquivalenceStrategy(List<AhcMapperPartialEquivalenceStrategy> partialStrategies) {
        this.partialStrategies = partialStrategies;
    }

    @Override
    public <S, T> boolean referToSameTargetElement(S source1, S source2, Class<S> sourceClass, Class<T> targetClass) throws Exception {
        //TODO cache by element class
        for (AhcMapperPartialEquivalenceStrategy strategy: partialStrategies) {
            if(strategy.canHandle(sourceClass, targetClass)) {
                return strategy.referToSameTargetElement(source1, source2, sourceClass, targetClass);
            }
        }
        throw new IllegalArgumentException("no equivalence strategy for classes " + sourceClass.getName() + " and " + targetClass.getName() + ".");
    }
    
    @Override
    public <S, T> IdentityHashMap<S, T> findEquivalentInstances(Collection<S> coll1, Collection<T> coll2, Class<S> coll1ElementClass, Class<T> coll2ElementClass) throws Exception {
        //TODO cache by element class
        for (AhcMapperPartialEquivalenceStrategy strategy: partialStrategies) {
            if(strategy.canHandle(coll1ElementClass, coll2ElementClass)) {
                return strategy.findEquivalentInstances(coll1, coll2, coll1ElementClass, coll2ElementClass);
            }
        }
        throw new IllegalArgumentException("no equivalence strategy for classes " + coll1ElementClass.getName() + " and " + coll2ElementClass.getName() + ".");
    }

    @Override
    public <S> Object getTargetEquivalenceMarker(S source, Class<S> sourceClass, Class<?> targetClass) throws Exception {
        //TODO cache by element class
        for (AhcMapperPartialEquivalenceStrategy strategy: partialStrategies) {
            if(strategy.canHandle(sourceClass, targetClass)) {
                return strategy.getTargetEquivalenceMarker(source, sourceClass, targetClass);
            }
        }
        throw new IllegalArgumentException("no equivalence strategy for classes " + sourceClass.getName() + " and " + targetClass.getName() + ".");
    }
}
