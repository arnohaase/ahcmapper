package com.ajjpj.ahcmapper.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperInstanceProvider;


class AhcMapperInstanceProviderBuilder {
    private final List <AhcMapperInstanceProviderExtension> extensions = new ArrayList <AhcMapperInstanceProviderExtension>();
    
    public AhcMapperInstanceProviderBuilder withExtension (AhcMapperInstanceProviderExtension extension) {
        extensions.add (extension);
        return this;
    }

    public AhcMapperInstanceProvider build () {
        return new AhcMapperInstanceProvider () {
            @SuppressWarnings("unchecked")
            @Override
            public <T> T provideInstance(Object source, T oldTarget, Class<? extends T> targetClass) throws Exception {
                for (AhcMapperInstanceProviderExtension extension: extensions) {
                    if (extension.canHandle (source, targetClass)) {
                        return extension.provideInstance(source, oldTarget, targetClass);
                    }
                }
                
                if (oldTarget != null) {
                    return oldTarget;
                }
                
                if (targetClass == List.class) { // TODO extract this default behaviour to someplace configurable?
                    return (T) new ArrayList <Object>();
                }
                if (targetClass == Set.class){
                    return (T) new HashSet <Object>();
                }
                if (targetClass == Map.class) {
                    return (T) new HashMap <Object, Object>();
                }
                
                return targetClass.newInstance();
            }
        };
    }
}
