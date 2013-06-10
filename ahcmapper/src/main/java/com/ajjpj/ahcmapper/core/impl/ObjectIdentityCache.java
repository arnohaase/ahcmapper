package com.ajjpj.ahcmapper.core.impl;

import java.util.HashMap;
import java.util.Map;

import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperLogger;


class ObjectIdentityCache {
    private final Map<Object, Object> impl = new HashMap<Object, Object>();
    private final AhcMapperLogger logger;
    
    public ObjectIdentityCache(AhcMapperLogger logger) {
        this.logger = logger;
    }

    static class ObjectWrapper {
        public final Object element;
        public ObjectWrapper(Object element) {
            this.element = element;
        }
    }
    
    public ObjectWrapper getPreviousMapping (Object o, Class<?> targetClass) {
        if (o == null) {
            return null;
        }

        final Key key = new Key(o, targetClass);
        
        if(impl.containsKey(key)) {
            logger.debug("looking up identity cache for " + asDebugString(o) + " to " + targetClass.getName() + ": " + impl.get(key));
            return new ObjectWrapper(impl.get(key));
        }
        logger.debug("no entry in identity cache for " + asDebugString(o) + " to " + targetClass.getName());
        return null;
    }
    
    private String asDebugString(Object o) {
        return o.getClass().getName() + "[" + o + "@" + Integer.toHexString(System.identityHashCode(o)) + "]";
    }
    
    public void registerMapping (Object source, Object target, Class<?> targetClass) {
        if (source != null) {
            logger.debug("registering mapping in identity cache: " + asDebugString(source) + " to " + targetClass.getName() + " -> " + target);
            impl.put (new Key(source, targetClass), target);
        }
        else {
            logger.debug("not registering mapping: source is null");
        }
    }
    
    private static class Key {
        public final Object source;
        public final Class<?> targetClass;
        
        public Key(Object source, Class<?> targetClass) {
            this.source = source;
            this.targetClass = targetClass;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((source == null) ? 0 : source.hashCode());
            result = prime * result + ((targetClass == null) ? 0 : targetClass.hashCode());
            return result;
        }

        //NB: this is *not* the regular, generated equals method - it compares the 'source' attribute for same-ness
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Key other = (Key) obj;
            if (source != other.source) {
                return false;
            }
            if (targetClass == null) {
                if (other.targetClass != null)
                    return false;
            } else if (!targetClass.equals(other.targetClass))
                return false;
            return true;
        }
    }
}
