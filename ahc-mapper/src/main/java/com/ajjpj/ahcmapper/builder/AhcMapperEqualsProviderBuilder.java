package com.ajjpj.ahcmapper.builder;

import java.util.ArrayList;
import java.util.List;

import com.ajjpj.ahcmapper.core.equivalence.equals.AhcMapperEqualsProvider;



//TODO refactor this into the 'equivalence' builder that is to be created
public class AhcMapperEqualsProviderBuilder {
    private final List<AhcMapperEqualsProviderExtension> extensions = new ArrayList<AhcMapperEqualsProviderExtension>();
    
    public AhcMapperEqualsProviderBuilder withExtension(AhcMapperEqualsProviderExtension extension) {
        extensions.add(extension);
        return this;
    }
    
    public AhcMapperEqualsProvider build() {
        return new AhcMapperEqualsProvider() { 
            //TODO caching?!
            private AhcMapperEqualsProvider getProvider(Class<?> sourceClass, Class<?> targetClass) {
                for(AhcMapperEqualsProviderExtension candidate: extensions) {
                    if(candidate.canHandle(sourceClass, targetClass)) {
                        return candidate;
                    }
                }
                return AhcMapperEqualsProvider.NATURAL;
            }
            
            @Override
            public Object createEqualsPlaceholder(Object source, Class<?> sourceClass, Class<?> targetClass) throws Exception {
                return getProvider(sourceClass, targetClass).createEqualsPlaceholder(source, sourceClass, targetClass);
            }

            @Override
            public CompareStrategy getCompareStrategy(Class<?> cls) throws Exception {
                return getProvider(cls, cls).getCompareStrategy(cls);
            }
        };
    }
}
