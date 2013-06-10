package com.ajjpj.ahcmapper.mappingdef.builtin.value;

import java.math.BigDecimal;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.AhcValueMappingDef;


public class BigDecimalMappingDef implements AhcValueMappingDef <BigDecimal, BigDecimal>{
    @Override
    public boolean canHandle(Class<?> sourceClass, Class<?> targetClass) {
        return BigDecimal.class == sourceClass && sourceClass == targetClass;
    }

    @Override
    public boolean handlesNull() {
        return true;
    }

    @Override
    public BigDecimal map(BigDecimal source, AhcMapperWorker worker) throws Exception {
        return source;
    }   
}
