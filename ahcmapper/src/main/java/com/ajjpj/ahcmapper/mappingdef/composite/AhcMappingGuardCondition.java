package com.ajjpj.ahcmapper.mappingdef.composite;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperPath;


public interface AhcMappingGuardCondition<S, T> {
    boolean shouldMap(S source, T target, AhcMapperPath path, AhcMapperWorker worker) throws Exception;
}
