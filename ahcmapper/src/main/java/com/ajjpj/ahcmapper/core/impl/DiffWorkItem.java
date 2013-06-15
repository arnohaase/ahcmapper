package com.ajjpj.ahcmapper.core.impl;

import com.ajjpj.ahcmapper.core.AhcObjectMappingDef;
import com.ajjpj.ahcmapper.core.WorkItem;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperPath;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperUtil;
import com.ajjpj.ahcmapper.core.diff.AhcMapperDiffBuilder;


class DiffWorkItem<S, T> implements WorkItem {
    private final AhcMapperPath parentPath;
    private final String propertyIdentifier;
    private final S source1;
    private final S source2;
    private final Class<S> sourceClass;
    private final Class<?> sourceElementClass;
    private final Class<T> targetClass;
    private final Class<?> targetElementClass;

    private final AhcMapperDiffBuilder diff;
    private final AhcMapperWorkerImpl worker;

    public DiffWorkItem(AhcMapperPath parentPath, String propertyIdentifier, S source1, S source2, Class<S> sourceClass, Class<?> sourceElementClass, Class<T> targetClass, Class<?> targetElementClass, 
            AhcMapperDiffBuilder diff, AhcMapperWorkerImpl worker) {
        this.parentPath = parentPath;
        this.propertyIdentifier = propertyIdentifier;
        this.source1 = source1;
        this.source2 = source2;
        this.sourceClass = sourceClass;
        this.sourceElementClass = sourceElementClass;
        this.targetClass = targetClass;
        this.targetElementClass = targetElementClass;
        this.diff = diff;
        this.worker = worker;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void run() throws Exception {
        worker.logger.debug("running diff worker: " + source1 + " [" + sourceClass.getSimpleName() + "] and " + source2 + " -> " + targetClass.getSimpleName());

        final S unproxiedSource1 = worker.deProxyStrategy.deProxy(source1);
        final S unproxiedSource2 = worker.deProxyStrategy.deProxy(source2);

        if(unproxiedSource1 == unproxiedSource2) {
            // convenience (primarily for 'null'=='null') and a minor optimization
            return;
        }
        
        final Object targetMarker1 = worker.equivalenceStrategy.getTargetEquivalenceMarker(source1, sourceClass, targetClass);
        final Object targetMarker2 = worker.equivalenceStrategy.getTargetEquivalenceMarker(source2, sourceClass, targetClass);

        if(AhcMapperUtil.nullSafeEq(targetMarker1, targetMarker2)) {
            // equivalent (i.e. 'same id but potentially changed') target objects: no change here but potentially further down --> descend

            final AhcMapperPath curPath = parentPath.withSegment(propertyIdentifier, new DiffPathMarker(unproxiedSource1, unproxiedSource2, targetMarker1, targetMarker2, propertyIdentifier));
            
            //TODO log 'object mapping not found'
            final AhcObjectMappingDef<Object, Object> objectMapping = 
                    (AhcObjectMappingDef<Object, Object>) worker.mappingProvider.getObjectMapping(sourceClass, sourceElementClass, targetClass, targetElementClass);
            
            if(objectMapping.isCacheable()) {
                final boolean visitedBefore = diff.markVisited(curPath);
                if(visitedBefore) {
                    return;
                }
            }
            objectMapping.diff(source1, source2, sourceClass, sourceElementClass, targetClass, targetElementClass, curPath, diff, worker);
        }
        else {
            //TODO descend nonetheless!
            
            // non-equivalent (i.e. 'different id') target objects: add RefChangedItem to the diff
            final DiffPathMarker parentMarker = (DiffPathMarker) parentPath.getMarker();
            if(parentMarker == null) {
                // root element
                diff.addRefChange(null, null, targetMarker1, targetMarker2, parentPath, propertyIdentifier);
            }
            else {
                diff.addRefChange(parentMarker.getSource1(), parentMarker.getSource2(), targetMarker1, targetMarker2, parentPath, propertyIdentifier);
            }
        }
    }
    
    static class CachedTargetObject {
        private final AhcMapperPath path;
        private final Object targetEquivalent;
        
        public CachedTargetObject(AhcMapperPath path, Object targetEquivalent) {
            this.path = path;
            this.targetEquivalent = targetEquivalent;
        }

        public AhcMapperPath getPath() {
            return path;
        }
        public Object getTargetEquivalent() {
            return targetEquivalent;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((path == null) ? 0 : path.hashCode());
            result = prime * result + ((targetEquivalent == null) ? 0 : targetEquivalent.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CachedTargetObject other = (CachedTargetObject) obj;
            if (path == null) {
                if (other.path != null)
                    return false;
            } else if (!path.equals(other.path))
                return false;
            if (targetEquivalent == null) {
                if (other.targetEquivalent != null)
                    return false;
            } else if (!targetEquivalent.equals(other.targetEquivalent))
                return false;
            return true;
        }
    }
}


