package com.ajjpj.ahcmapper.mappingdef.composite;

import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperPath;
import com.ajjpj.ahcmapper.core.diff.AhcMapperDiffBuilder;


public interface AhcMappingPart<S, T> {
    void map(S source, T target, AhcMapperPath path, AhcMapperWorker worker) throws Exception;

    void diff(S source1, S source2, AhcMapperPath targetPath, AhcMapperDiffBuilder diff, AhcMapperWorker worker) throws Exception;
}
