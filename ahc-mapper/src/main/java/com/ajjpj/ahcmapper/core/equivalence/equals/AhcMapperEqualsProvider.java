package com.ajjpj.ahcmapper.core.equivalence.equals;


public interface AhcMapperEqualsProvider {
    Object createEqualsPlaceholder(Object source, Class<?> sourceClass, Class<?> targetClass) throws Exception;
    CompareStrategy getCompareStrategy(Class<?> cls) throws Exception;
    
    interface CompareStrategy {
        /**
         * This method is used to check if two references refer to the logically *same* object. The two
         *  may be different *versions* of that object (e.g. in the context of a diff operation), so comparing
         *  all attribute values is usually not a good strategy.<p />
         *  
         * Typical implementations of this method check for some sort of 'primary key' (e.g. for persistent 
         *  objects) or 'logical key' such as a customer number.
         */
        boolean isEqual (Object o1, Object o2);
        int hashCode (Object o);
    }

    AhcMapperEqualsProvider NATURAL = new AhcMapperEqualsProvider () {
        @Override
        public Object createEqualsPlaceholder(Object source, Class<?> sourceClass, Class<?> targetClass) throws Exception {
            if (targetClass.isInstance(source)) {
                return source;
            }
            
            if(source == null) {
                return null;
            }
            
            try {
                return targetClass.newInstance();
            }
            catch(Exception exc) {
            }
            
            return null;
        }

        @Override
        public CompareStrategy getCompareStrategy(Class<?> cls) throws Exception {
            return NATURAL_COMPARE_STRATEGY;
        }
    };
    
    CompareStrategy NATURAL_COMPARE_STRATEGY = new CompareStrategy () {
        @Override
        public boolean isEqual(Object o1, Object o2) {
            if(o1 == null) {
                return o2 == null;
            }
            return o1.equals(o2);
        }

        @Override
        public int hashCode(Object o) {
            if(o == null) {
                return 0;
            }
            return o.hashCode();
        }
    };
}
