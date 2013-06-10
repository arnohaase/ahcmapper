package com.ajjpj.ahcmapper.builder.propertybased;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperLogger;
import com.ajjpj.ahcmapper.mappingdef.composite.propertybased.AhcPropertyAccessor;
import com.ajjpj.ahcmapper.mappingdef.composite.propertybased.MethodBasedPropertyAccessor;



/**
 * This class uses reflection to find matching getter / setter pairs in two classes, based bean property name conventions.
 * 
 * @author arno
 */
class MethodBasedAccessorFactory {
    private static final Map<Class<?>, Class<?>> primitiveEquivalents = new HashMap<Class<?>, Class<?>>();
    static {
        primitiveEquivalents.put(Byte.class, Byte.TYPE);
        primitiveEquivalents.put(Short.class, Short.TYPE);
        primitiveEquivalents.put(Integer.class, Integer.TYPE);
        primitiveEquivalents.put(Long.class, Long.TYPE);
        primitiveEquivalents.put(Float.class, Float.TYPE);
        primitiveEquivalents.put(Double.class, Double.TYPE);
        primitiveEquivalents.put(Boolean.class, Boolean.TYPE);
        primitiveEquivalents.put(Character.class, Character.TYPE);
    }
    
    private final Collection <AhcPropertyAccessorPair> result = new ArrayList <AhcPropertyAccessorPair> ();

    public MethodBasedAccessorFactory (Class<?> sourceClass, Class<?> targetClass, AhcMapperLogger logger) throws NoSuchMethodException {
        final Map <String, PropertyDef> fromProperties = getPropertyDefs (sourceClass);
        final Map <String, PropertyDef> toProperties   = getPropertyDefs (targetClass);
        
        final Set <String> commonPropNames = new HashSet <String> (fromProperties.keySet());
        commonPropNames.retainAll (toProperties.keySet());
        
        for (String prop: commonPropNames) {
            result.add (new AhcPropertyAccessorPair (createAccessor (fromProperties.get (prop), logger), createAccessor (toProperties.get (prop), logger)));
        }
    }

    public Collection <AhcPropertyAccessorPair> getDefaultAccessorList () {
        return result;
    }

    public static AhcPropertyAccessor createAccessor (PropertyDef def, AhcMapperLogger logger) throws NoSuchMethodException {
        return createAccessor(def, null, logger);
    }

    public static AhcPropertyAccessor createAccessor (PropertyDef def, Boolean isPrimary, AhcMapperLogger logger) throws NoSuchMethodException {
        final Method getter = getGetter (def);
        final Method setter = getSetter (def, logger);

        //TODO log discrepancy between getter and setter type
        
        if(isPrimary == null) {
            isPrimary = (getter != null && getter.getAnnotation(AhcMapperDelayedProperty.class) == null) ||
                    (setter != null && setter.getAnnotation(AhcMapperDelayedProperty.class) != null);
        }
        
        return new MethodBasedPropertyAccessor (toFirstLower(def.getPropName()), def.getPropType (), def.getElementType (), isPrimary, getter, setter);
    }

    public static Method getGetter (PropertyDef def) {
        try {
            return def.getDeclaringClass ().getMethod ("get" + def.getPropName ());
        } 
        catch (NoSuchMethodException e) {
            try {
                if (def.getPropType () == Boolean.class || def.getPropType () == Boolean.TYPE) {
                    return def.getDeclaringClass ().getMethod ("is" + def.getPropName ());
                }
            }
            catch(Exception exc) {
            }

            try {
                if (def.getPropType () == Boolean.class || def.getPropType () == Boolean.TYPE) {
                    return def.getDeclaringClass ().getMethod ("has" + def.getPropName ());
                }
            }
            catch(Exception exc) {
            }

            return null;
        }
    }
    
    public static Method getSetter (PropertyDef propertyDef, AhcMapperLogger logger) {
        try {
            return propertyDef.getDeclaringClass ().getMethod ("set" + propertyDef.getPropName (), propertyDef.getPropType ());
        }
        catch (NoSuchMethodException exc) {
            final Class<?> primitiveEquivalent = primitiveEquivalents.get(propertyDef.getPropType());
            if(primitiveEquivalent != null) {
                try {
                    final Method mtd2 = propertyDef.getDeclaringClass().getMethod("set" + propertyDef.getPropName(), primitiveEquivalent);
                    logger.warn("The setter " + propertyDef.getDeclaringClass().getName() + ".set" + propertyDef.getPropName() + " does not accept type " + propertyDef.getPropType() + " but the primitive equivalent " + primitiveEquivalent + ".");
                    //TODO log 'using primitive equivalent'
                    return mtd2;
                }
                catch (NoSuchMethodException exc2) {
                }
            }
            
            return null;
        }
    }
    
    private static Map <String, PropertyDef> getPropertyDefs (Class<?> cls) {
        final Map <String, PropertyDef> result = new HashMap <String, PropertyDef> ();
        
        for (Method mtd: cls.getMethods()) {
            final PropertyDef def = asPropertyDef (mtd);
            if (def != null) {
                result.put (def.getPropName (), def);
            }
        }
        
        return result;
    }
    
    private static Class<?> getElementType (Method mtd) {
        if (mtd.getParameterTypes ().length == 1) {
            return getElementType (mtd.getGenericParameterTypes ()[0]);
        }
        else {
            return getElementType (mtd.getGenericReturnType ());
        }
    }
        
    @SuppressWarnings("rawtypes")
    private static Class<?> getElementType (Type type) {
        if (type instanceof Class<?> && ((Class<?>) type).isArray ()) {
            return ((Class<?>) type).getComponentType();
        }
        if (type instanceof GenericArrayType) {
            if ( ((GenericArrayType) type).getGenericComponentType() instanceof TypeVariable) {
                return ((TypeVariable) ((GenericArrayType) type).getGenericComponentType()).getClass();
            }
            final ParameterizedType componentType = (ParameterizedType) ((GenericArrayType) type).getGenericComponentType ();
            return (Class<?>) componentType.getRawType ();                            
        }
        if (type instanceof Class<?> && ! Collection.class.isAssignableFrom ((Class<?>) type)) {
            return null;
        }
        
        if (! (type instanceof ParameterizedType)) {
            throw new IllegalArgumentException ("attempting to map a collection without generic parameter type.");
        }
        
        final Type parameterType = ((ParameterizedType) type).getActualTypeArguments()[0];
        if (parameterType instanceof Class) {
            return (Class<?>) parameterType;
        }
        else if (parameterType instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) parameterType).getRawType();
        }
        return null;
    }

    private static PropertyDef asPropertyDef (Method mtd) {
        if (mtd.getName().startsWith ("set") && mtd.getParameterTypes().length == 1) {
            return new PropertyDef (mtd.getDeclaringClass (), mtd.getName().substring (3), mtd.getParameterTypes ()[0], getElementType (mtd));
        }
        if (mtd.getName().startsWith ("get") && mtd.getParameterTypes().length == 0 && mtd.getDeclaringClass () != Object.class) {
            return new PropertyDef (mtd.getDeclaringClass (), mtd.getName().substring (3), mtd.getReturnType (), getElementType (mtd));
        }
        if (mtd.getName().startsWith ("is") && mtd.getParameterTypes().length == 0 && (mtd.getReturnType () == Boolean.class || mtd.getReturnType () == Boolean.TYPE)) {
            return new PropertyDef (mtd.getDeclaringClass (), mtd.getName().substring (2), mtd.getReturnType (), getElementType (mtd));
        }
        if (mtd.getName().startsWith ("has") && mtd.getParameterTypes().length == 0 && (mtd.getReturnType () == Boolean.class || mtd.getReturnType () == Boolean.TYPE)) {
            return new PropertyDef (mtd.getDeclaringClass (), mtd.getName().substring (3), mtd.getReturnType (), getElementType (mtd));
        }
        
        return null;
    }
    
    private static String toFirstLower (String s) {
        if (s.length() == 1) {
            return s.toLowerCase();
        }
        
        // special case: the Java Beans standard prescribes that a property name starting with several uppercase letters
        //  does *not* have its first letter lowercased
        if (Character.isUpperCase (s.charAt(1))) {
            return s;
        }
        
        return Character.toLowerCase (s.charAt (0)) + s.substring (1);
    }
    
    static class PropertyDef {
        private final Class<?> declaringClass;
        private final String propName;
        private final Class<?> propType;
        private final Class<?> elementType;
        
        public PropertyDef (Class<?> declaringClass, String propName, Class<?> propType, Class<?> elementType) {
            this.declaringClass = declaringClass;
            this.propName = propName;
            this.propType = propType;
            this.elementType = elementType;
        }

        public Class<?> getDeclaringClass () {
            return declaringClass;
        }

        public String getPropName () {
            return propName;
        }

        public Class<?> getPropType () {
            return propType;
        }

        public Class<?> getElementType () {
            return elementType;
        }
    }
}
