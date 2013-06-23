package com.ajjpj.ahcmapper.builder.propertybased;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.ajjpj.ahcmapper.core.AhcObjectMappingDef;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperLogger;
import com.ajjpj.ahcmapper.mappingdef.composite.AhcCompositeObjectMappingDef;
import com.ajjpj.ahcmapper.mappingdef.composite.AhcMappingPart;
import com.ajjpj.ahcmapper.mappingdef.composite.propertybased.AhcPropertyAccessor;
import com.ajjpj.ahcmapper.mappingdef.composite.propertybased.AhcPropertyBasedMappingPart;



public class PropertyBasedObjectMappingBuilder<S, T> {
    private final AhcMapperLogger logger;
    
    private final Class<S> sourceClass;
    private final Class<T> targetClass;
    
    private final Collection<AhcPropertyAccessorPair> forwardPropertyPairs = new ArrayList<AhcPropertyAccessorPair>();
    private final Collection<AhcPropertyAccessorPair> backwardsPropertyPairs = new ArrayList<AhcPropertyAccessorPair>();
    
    private boolean canHandleSubclasses = false;

    public PropertyBasedObjectMappingBuilder (AhcMapperLogger logger, Class<S> sourceClass, Class<T> targetClass) throws Exception {
        this.logger = logger;
        
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        
        //TODO make this configurable
        forwardPropertyPairs.addAll(new MethodBasedAccessorFactory (sourceClass, targetClass, logger).getDefaultAccessorList());
        for(AhcPropertyAccessorPair pair: forwardPropertyPairs) {
            backwardsPropertyPairs.add(new AhcPropertyAccessorPair(pair.getTargetAccessor(), pair.getSourceAccessor()));
        }
    }
    
    public PropertyBasedObjectMappingBuilder<S, T> withPolymorphicSubclassHandling() {
        this.canHandleSubclasses = true;
        return this;
    }
    
    //TODO addMapping without types --> determine via reflection if possible, otherwise exception
    
    public PropertyBasedObjectMappingBuilder<S, T> addMapping (
            String sourceExpression, Class<?> sourceClass,  
            String targetExpression, Class<?> targetClass) throws Exception {
        return addMapping (sourceExpression, sourceClass, targetExpression, targetClass, true);
    }
        
    public PropertyBasedObjectMappingBuilder<S, T> addMapping (
            String sourceExpression, Class<?> sourceClass,  
            String targetExpression, Class<?> targetClass,
            boolean isPrimary) throws Exception {
    return addMapping(sourceExpression, sourceClass, null, targetExpression, targetClass, null, isPrimary);
    }
    
    public PropertyBasedObjectMappingBuilder<S, T> addMapping (
            String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, 
            String targetExpression, Class<?> targetClass, Class<?> targetElementClass) throws Exception {
        return addMapping(sourceExpression, sourceClass, sourceElementClass, targetExpression, targetClass, targetElementClass, true);
        
    }
    
    public PropertyBasedObjectMappingBuilder<S, T> addMapping (
            String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, 
            String targetExpression, Class<?> targetClass, Class<?> targetElementClass, 
            boolean isPrimary) throws Exception {
        //TODO check if there is a mapping with these expressions
        
        final AhcPropertyAccessor sourceAccessor = new AhcMapperExpressionParser(logger).parse(this.sourceClass, sourceExpression, sourceClass, sourceElementClass, isPrimary);
        final AhcPropertyAccessor targetAccessor = new AhcMapperExpressionParser(logger).parse(this.targetClass, targetExpression, targetClass, targetElementClass, isPrimary);
        
        forwardPropertyPairs  .add(new AhcPropertyAccessorPair(sourceAccessor, targetAccessor));        
        backwardsPropertyPairs.add(new AhcPropertyAccessorPair(targetAccessor, sourceAccessor));        
        return this;
    }

    public PropertyBasedObjectMappingBuilder<S, T> addOneWayMapping (
            String sourceExpression, Class<?> sourceClass,  
            String targetExpression, Class<?> targetClass) throws Exception {
        return addOneWayMapping(sourceExpression, sourceClass, targetExpression, targetClass, true);
    }

    public PropertyBasedObjectMappingBuilder<S, T> addOneWayMapping (
            String sourceExpression, Class<?> sourceClass,  
            String targetExpression, Class<?> targetClass,
            boolean isPrimary) throws Exception {
        return addOneWayMapping(sourceExpression, sourceClass, null, targetExpression, targetClass, null, isPrimary);
    }
        
    public PropertyBasedObjectMappingBuilder<S, T> addOneWayMapping (
                String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, 
                String targetExpression, Class<?> targetClass, Class<?> targetElementClass,
                boolean isPrimary) throws Exception {
        //TODO check if there is a mapping with these expressions

        final AhcPropertyAccessor sourceAccessor = new AhcMapperExpressionParser(logger).parse(this.sourceClass, sourceExpression, sourceClass, sourceElementClass, isPrimary);
        final AhcPropertyAccessor targetAccessor = new AhcMapperExpressionParser(logger).parse(this.targetClass, targetExpression, targetClass, targetElementClass, isPrimary);

        forwardPropertyPairs.add(new AhcPropertyAccessorPair(sourceAccessor, targetAccessor));        
        return this;
    }

    public PropertyBasedObjectMappingBuilder<S, T> addBackwardsOneWayMapping (
            String sourceExpression, Class<?> sourceClass, 
            String targetExpression, Class<?> targetClass) throws Exception {
        return addBackwardsOneWayMapping(sourceExpression, sourceClass, targetExpression, targetClass, true);
    }

    public PropertyBasedObjectMappingBuilder<S, T> addBackwardsOneWayMapping (
            String sourceExpression, Class<?> sourceClass, 
            String targetExpression, Class<?> targetClass,
            boolean isPrimary) throws Exception {
        return addBackwardsOneWayMapping(sourceExpression, sourceClass, null, targetExpression, targetClass, null, isPrimary);
    }
        
    public PropertyBasedObjectMappingBuilder<S, T> addBackwardsOneWayMapping (
                String sourceExpression, Class<?> sourceClass, Class<?> sourceElementClass, 
                String targetExpression, Class<?> targetClass, Class<?> targetElementClass,
                boolean isPrimary) throws Exception {
        //TODO check if there is a mapping with these expressions
        
        final AhcPropertyAccessor sourceAccessor = new AhcMapperExpressionParser(logger).parse(this.sourceClass, sourceExpression, sourceClass, sourceElementClass, isPrimary);
        final AhcPropertyAccessor targetAccessor = new AhcMapperExpressionParser(logger).parse(this.targetClass, targetExpression, targetClass, targetElementClass, isPrimary);

        backwardsPropertyPairs.add(new AhcPropertyAccessorPair(targetAccessor, sourceAccessor));        
        return this;
    }
    
    
    public PropertyBasedObjectMappingBuilder<S, T> makeOneWay (String sourceAndTargetPropName) {
        return makeOneWay(sourceAndTargetPropName, sourceAndTargetPropName);
    }

    public PropertyBasedObjectMappingBuilder<S, T> makeOneWay (String sourcePropName, String targetPropName) {
        removeMapping(targetPropName, sourcePropName, backwardsPropertyPairs);
        return this;
    }

    public PropertyBasedObjectMappingBuilder<S, T> makeBackwardsOneWay (String sourceAndTargetPropName) {
        return makeBackwardsOneWay(sourceAndTargetPropName, sourceAndTargetPropName);
    }
    
    public PropertyBasedObjectMappingBuilder<S, T> makeBackwardsOneWay (String sourcePropName, String targetPropName) {
        removeMapping(sourcePropName, targetPropName, forwardPropertyPairs);
        return this;
    }
    
    public PropertyBasedObjectMappingBuilder<S, T> removeMapping (String sourceAndTargetPropName) {
        return removeMapping(sourceAndTargetPropName, sourceAndTargetPropName);
    }
    
    public PropertyBasedObjectMappingBuilder<S, T> removeMapping (String sourcePropName, String targetPropName) {
        removeMapping(sourcePropName, targetPropName, forwardPropertyPairs);
        removeMapping(targetPropName, sourcePropName, backwardsPropertyPairs);
        return this;
    }

    private void removeMapping(String sourcePropName, String targetPropName, Collection<AhcPropertyAccessorPair> props) {
        for(Iterator<AhcPropertyAccessorPair> iter = props.iterator(); iter.hasNext(); ) {
            final AhcPropertyAccessorPair candidate = iter.next();
            
            if(sourcePropName.equals(candidate.getSourceAccessor().getName()) && targetPropName.equals(candidate.getTargetAccessor().getName())) {
                iter.remove();
                return;
            }
        }
        
        //TODO log warning if there is no such mapping
    }
    
    
    //TODO guards
    
    public AhcObjectMappingDef<S, T> build() {
        final List<AhcMappingPart<S, T>> parts = new ArrayList<AhcMappingPart<S, T>>();
        logger.debug("building mapping " + sourceClass.getName() + " -> " + targetClass.getSimpleName());
        for (AhcPropertyAccessorPair propPair: forwardPropertyPairs) {
            logger.debug("  adding property mapping " + propPair.getSourceAccessor() + " -> " + propPair.getTargetAccessor());
            if(propPair.getSourceAccessor().isReadable() && propPair.getTargetAccessor().isWritable()) {
                final boolean isPrimary = propPair.getSourceAccessor().isPrimary();
                parts.add(new AhcPropertyBasedMappingPart<S, T>(propPair.getSourceAccessor(), propPair.getTargetAccessor(), isPrimary));
            }
        }
        
        return new AhcCompositeObjectMappingDef<S, T>(canHandleSubclasses, sourceClass, targetClass, parts);
    }
    
    public AhcObjectMappingDef<T, S> buildBackwards() {
        final List<AhcMappingPart<T, S>> parts = new ArrayList<AhcMappingPart<T, S>>();

        for (AhcPropertyAccessorPair propPair: backwardsPropertyPairs) {
            if(propPair.getSourceAccessor().isReadable() && propPair.getTargetAccessor().isWritable()) {
                final boolean isPrimary = propPair.getSourceAccessor().isPrimary();
                parts.add(new AhcPropertyBasedMappingPart<T, S>(propPair.getSourceAccessor(), propPair.getTargetAccessor(), isPrimary));
            }
        }
        
        return new AhcCompositeObjectMappingDef<T, S>(canHandleSubclasses, targetClass, sourceClass, parts);
    }
}
