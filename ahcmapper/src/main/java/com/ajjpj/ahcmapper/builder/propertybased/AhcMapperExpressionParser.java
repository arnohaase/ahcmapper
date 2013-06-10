package com.ajjpj.ahcmapper.builder.propertybased;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.ajjpj.ahcmapper.builder.propertybased.MethodBasedAccessorFactory.PropertyDef;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperLogger;
import com.ajjpj.ahcmapper.mappingdef.composite.propertybased.AhcPropertyAccessor;
import com.ajjpj.ahcmapper.mappingdef.composite.propertybased.MethodPathBasedPropertyAccessor;
import com.ajjpj.ahcmapper.mappingdef.composite.propertybased.MethodPathBasedPropertyAccessorStep;
import com.ajjpj.ahcmapper.mappingdef.composite.propertybased.OgnlPropertyAccessor;



class AhcMapperExpressionParser {
    private final AhcMapperLogger logger;
    
    public AhcMapperExpressionParser(AhcMapperLogger logger) {
        this.logger = logger;
    }

    public AhcPropertyAccessor parse(Class<?> parentClass, String expression, Class<?> type, Class<?> elementType, boolean isPrimary) throws Exception {
        final String[] segments = expression.split("\\.");

        try {
            if(segments.length == 1) {
                return asSingleProp(parentClass, segments[0], type, elementType, isPrimary);
            }
            else {
                return asPropCascade(parentClass, expression, segments, type, elementType, isPrimary);
            }
        }
        catch (Exception exc) {
            return new OgnlPropertyAccessor(expression, type, elementType, isPrimary);
        }
    }

    private AhcPropertyAccessor asSingleProp(Class<?> parentClass, String propName, Class<?> type, Class<?> elementType, boolean isPrimary) throws NoSuchMethodException {
        return MethodBasedAccessorFactory.createAccessor(new PropertyDef(parentClass, toFirstUpper(propName), type, elementType), isPrimary, logger);
    }
    
    private static String toFirstUpper (String s) {
        if(s == null || s.length() < 2) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
    
    private AhcPropertyAccessor asPropCascade(Class<?> parentClass, String expression, String[] segments, Class<?> type, Class<?> elementType, boolean isPrimary) throws NoSuchMethodException {
        final List<MethodPathBasedPropertyAccessorStep> steps = new ArrayList<MethodPathBasedPropertyAccessorStep>();
        
        Class<?> curType = parentClass;
        for (int i=0; i<segments.length-1; i++) {
            String propName;
            boolean treatNullSafe;
            
            if(segments[i].startsWith("?")) {
                propName = segments[i].substring(1);
                treatNullSafe = true;
            }
            else {
                propName = segments[i];
                treatNullSafe = false;
            }
            
            final Method stepGetter = MethodBasedAccessorFactory.getGetter(new PropertyDef(curType, toFirstUpper(propName), null, null));
            if(stepGetter == null) {
                throw new IllegalArgumentException ("no property " + propName + " in class " + curType + " (as part of expression " + expression + ")");
            }
            steps.add(new MethodPathBasedPropertyAccessorStep(stepGetter, treatNullSafe));
            curType = stepGetter.getReturnType();
        }

        final String lastSegment = segments[segments.length-1];

        String propName;
        boolean treatNullSafe;
        
        if(lastSegment.startsWith("?")) {
            propName = lastSegment.substring(1);
            treatNullSafe = true;
        }
        else {
            propName = lastSegment;
            treatNullSafe = false;
        }
        
        //TODO use asSingleProp for the last step
        final PropertyDef lastPropertyDef = new PropertyDef(curType, toFirstUpper(propName), type, elementType);
        
        return new MethodPathBasedPropertyAccessor(expression, type, elementType, isPrimary, steps, 
                MethodBasedAccessorFactory.getGetter(lastPropertyDef), 
                MethodBasedAccessorFactory.getSetter(lastPropertyDef, logger), 
                treatNullSafe);
    }
}
