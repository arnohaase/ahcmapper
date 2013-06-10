package com.ajjpj.ahcmapper.mappingdef.builtin.value;

import java.util.Date;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.AhcValueMappingDef;



public class DateValueMappingDef implements AhcValueMappingDef <Date, Date> {
    @Override
    public boolean canHandle(Class<?> sourceClass, Class<?> targetClass) {
        return Date.class.isAssignableFrom (sourceClass) && targetClass == Date.class;
    }

    @Override
    public boolean handlesNull() {
        return false;
    }

    @Override
    public Date map (Date source, AhcMapperWorker worker) {
        return new Date (source.getTime());
    }
}
