package com.ajjpj.ahcmapper.core.crosscutting;


public class AhcMapperUtil {
    public static boolean nullSafeEq(Object o1, Object o2) {
        if(o1 == o2) {
            return true;
        }
        
        if(o1 == null) {
            return false;
        }
        
        return o1.equals(o2);
    }
}
