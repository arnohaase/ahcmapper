package com.ajjpj.ahcmapper.core.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.AhcObjectMappingDef;
import com.ajjpj.ahcmapper.core.AhcValueMappingDef;


public class AhcMappingDefProvider {
    private final Collection <AhcValueMappingDef <?, ?>> rawValueMappings;
    private final Collection <AhcObjectMappingDef <?, ?>> rawObjectMappings;
    
    private final Map <ValueMappingDefKey,  AhcValueMappingDef  <?, ?>> valueMappings  = new ConcurrentHashMap <ValueMappingDefKey,  AhcValueMappingDef  <?, ?>>();
    private final Map <ObjectMappingDefKey, AhcObjectMappingDef <?, ?>> objectMappings = new ConcurrentHashMap <ObjectMappingDefKey, AhcObjectMappingDef <?, ?>> ();

    private static final AhcValueMappingDef<?, ?> NO_VALUE_MAPPING = new AhcValueMappingDef<Object, Object>() {
        @Override
        public boolean canHandle(Class<?> sourceClass, Class<?> targetClass) {
            return false;
        }

        @Override
        public boolean handlesNull() {
            return false;
        }

        @Override
        public Object map(Object source, AhcMapperWorker worker) throws Exception {
            return null;
        }
    };

    public AhcMappingDefProvider(Collection<AhcValueMappingDef<?, ?>> rawValueMappings, Collection<AhcObjectMappingDef<?, ?>> rawObjectMappings) {
        this.rawValueMappings = rawValueMappings;
        this.rawObjectMappings = rawObjectMappings;
    }

    //TODO path, params, parent type (or is parent type obsolete if we have 'context'?)
    @SuppressWarnings("unchecked")
    public <S, T> AhcValueMappingDef<S, T> getValueMapping(Class<? extends S> sourceClass, Class<? extends T> targetClass) {
        final ValueMappingDefKey key = new ValueMappingDefKey(sourceClass, targetClass);
        
        AhcValueMappingDef <S, T> result = (AhcValueMappingDef<S, T>) valueMappings.get (key);
        if (result != null) {
            return unwrap (result);
        }
        
        result = lookupRawValueMapping (sourceClass, targetClass);
        valueMappings.put (key, result);
        return unwrap (result);
        
    }
    
    private <S, T> AhcValueMappingDef<S, T> unwrap (AhcValueMappingDef<S, T> valueMapping) {
        if(valueMapping == NO_VALUE_MAPPING) {
            return null;
        }
        return valueMapping;
    }
    
    @SuppressWarnings("unchecked")
    private <S, T> AhcValueMappingDef <S, T> lookupRawValueMapping (Class<? extends S> sourceClass, Class<? extends T> targetClass) {
        for (AhcValueMappingDef <?, ?> candidate: rawValueMappings) {
            if (candidate.canHandle(sourceClass, targetClass)) {
                return (AhcValueMappingDef<S, T>) candidate;
            }
        }
        
        return (AhcValueMappingDef<S, T>) NO_VALUE_MAPPING;
    }

    
    //TODO path, params
    public <S, T> AhcObjectMappingDef<S, T> getObjectMapping(Class<S> sourceClass, Class<?> sourceElementClass, Class<T> targetClass, Class<?> targetElementClass) {
        final ObjectMappingDefKey key = new ObjectMappingDefKey(sourceClass, sourceElementClass, targetClass, targetElementClass); 
        
        @SuppressWarnings("unchecked")
        AhcObjectMappingDef <S, T> result = (AhcObjectMappingDef<S, T>) objectMappings.get (key);
        if (result != null) {
            return result;
        }
        
        result = lookupRawObjectMapping (sourceClass, sourceElementClass, targetClass, targetElementClass);
        objectMappings.put (key, result);
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private <S, T> AhcObjectMappingDef <S, T> lookupRawObjectMapping (Class<S> sourceClass, Class<?> sourceElementClass, Class<T> targetClass, Class<?> targetElementClass) {
        for (AhcObjectMappingDef <?, ?> candidate: rawObjectMappings) {
            if (candidate.canHandle(sourceClass, sourceElementClass, targetClass, targetElementClass)) {
                return (AhcObjectMappingDef<S, T>) candidate;
            }
        }
        
        throw new IllegalArgumentException ("No object mapping from " + sourceClass.getName() + " to " + targetClass.getName() + ".");
    }

    
    private static final class ValueMappingDefKey {
        private final Class<?> sourceClass;
        private final Class<?> targetClass;
        
        public ValueMappingDefKey(Class<?> sourceClass, Class<?> targetClass) {
            this.sourceClass = sourceClass;
            this.targetClass = targetClass;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((sourceClass == null) ? 0 : sourceClass.hashCode());
            result = prime * result + ((targetClass == null) ? 0 : targetClass.hashCode());
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
            ValueMappingDefKey other = (ValueMappingDefKey) obj;
            if (sourceClass == null) {
                if (other.sourceClass != null)
                    return false;
            } else if (!sourceClass.equals(other.sourceClass))
                return false;
            if (targetClass == null) {
                if (other.targetClass != null)
                    return false;
            } else if (!targetClass.equals(other.targetClass))
                return false;
            return true;
        }
    }
     
    private static final class ObjectMappingDefKey {
        private final Class<?> sourceClass;
        private final Class<?> sourceElementClass;
        private final Class<?> targetClass;
        private final Class<?> targetElementClass;
        
        public ObjectMappingDefKey(Class<?> sourceClass, Class<?> sourceElementClass, Class<?> targetClass, Class<?> targetElementClass) {
            this.sourceClass = sourceClass;
            this.sourceElementClass = sourceElementClass;
            this.targetClass = targetClass;
            this.targetElementClass = targetElementClass;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((sourceClass == null) ? 0 : sourceClass.hashCode());
            result = prime * result + ((sourceElementClass == null) ? 0 : sourceElementClass.hashCode());
            result = prime * result + ((targetClass == null) ? 0 : targetClass.hashCode());
            result = prime * result + ((targetElementClass == null) ? 0 : targetElementClass.hashCode());
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
            ObjectMappingDefKey other = (ObjectMappingDefKey) obj;
            if (sourceClass == null) {
                if (other.sourceClass != null)
                    return false;
            } else if (!sourceClass.equals(other.sourceClass))
                return false;
            if (sourceElementClass == null) {
                if (other.sourceElementClass != null)
                    return false;
            } else if (!sourceElementClass.equals(other.sourceElementClass))
                return false;
            if (targetClass == null) {
                if (other.targetClass != null)
                    return false;
            } else if (!targetClass.equals(other.targetClass))
                return false;
            if (targetElementClass == null) {
                if (other.targetElementClass != null)
                    return false;
            } else if (!targetElementClass.equals(other.targetElementClass))
                return false;
            return true;
        }
    }

}
