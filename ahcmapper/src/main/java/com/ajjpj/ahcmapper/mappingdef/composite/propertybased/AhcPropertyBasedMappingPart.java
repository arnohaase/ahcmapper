package com.ajjpj.ahcmapper.mappingdef.composite.propertybased;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperPath;
import com.ajjpj.ahcmapper.core.diff.AhcMapperDiffBuilder;
import com.ajjpj.ahcmapper.mappingdef.composite.AhcMappingPart;


public class AhcPropertyBasedMappingPart<S, T> implements AhcMappingPart<S, T> {
    private final boolean isPrimary;
    //TODO shouldIgnoreSourceNull ?!
    //TODO shortcut: hasSpecialNullHandling ?! --> if false, bypass the entire 'worker.map' stuff for sourceValue == null

    private final AhcPropertyAccessor sourceProperty;
    private final AhcPropertyAccessor targetProperty;
    
    public AhcPropertyBasedMappingPart(AhcPropertyAccessor sourceProperty, AhcPropertyAccessor targetProperty, boolean isPrimary) {
        this.isPrimary = isPrimary;
        this.sourceProperty = sourceProperty;
        this.targetProperty = targetProperty;
    }

    @Override
    public void map(S source, final T target, AhcMapperPath path, AhcMapperWorker worker) throws Exception {
        final AhcMapperWorker.ResultHandler<Object> resultHandler = new AhcMapperWorker.ResultHandler<Object>() {
            @Override
            public void handle(Object targetPropValue) throws Exception {
                targetProperty.setValue(target, targetPropValue);
            }
        };
        
        worker.map(path, sourceProperty.getName(), 
                sourceProperty.getValue(source), sourceProperty.getType(), sourceProperty.getElementType(), 
                targetProperty.getValue(target), targetProperty.getType(), targetProperty.getElementType(), 
                resultHandler, isPrimary);
    }
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void diff(S source1, S source2, AhcMapperPath targetPath, AhcMapperDiffBuilder diff, AhcMapperWorker worker) throws Exception {
        worker.diff(targetPath, targetProperty.getName(), sourceProperty.getValue(source1), sourceProperty.getValue(source2), (Class) sourceProperty.getType(), sourceProperty.getElementType(), (Class) targetProperty.getType(), targetProperty.getElementType(), diff, isPrimary);
    }
    
    @Override
    public String toString() {
        return "PropBasedMappingPart: " + sourceProperty.getName() + " -> " + targetProperty.getName();
    }
}
