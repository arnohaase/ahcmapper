package com.ajjpj.ahcmapper.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.ajjpj.ahcmapper.AhcMapper;
import com.ajjpj.ahcmapper.builder.propertybased.PropertyBasedObjectMappingBuilder;
import com.ajjpj.ahcmapper.core.AhcObjectMappingDef;
import com.ajjpj.ahcmapper.core.AhcValueMappingDef;
import com.ajjpj.ahcmapper.core.crosscutting.AhcDeProxyStrategy;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperExceptionHandler;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperLogger;
import com.ajjpj.ahcmapper.core.equivalence.AhcMapperEquivalenceStrategy;
import com.ajjpj.ahcmapper.core.equivalence.equals.AhcMapperEqualsProvider;
import com.ajjpj.ahcmapper.core.equivalence.equals.EqualsBasedEquivalenceStrategy;
import com.ajjpj.ahcmapper.core.impl.AhcMapperImpl;
import com.ajjpj.ahcmapper.core.impl.AhcMappingDefProvider;
import com.ajjpj.ahcmapper.mappingdef.builtin.collection.ArrayObjectMappingDef;
import com.ajjpj.ahcmapper.mappingdef.builtin.collection.ListWithIndexObjectMappingDef;
import com.ajjpj.ahcmapper.mappingdef.builtin.collection.ListWithoutOrderOrDuplicatesObjectMappingDef;
import com.ajjpj.ahcmapper.mappingdef.builtin.collection.SetObjectMappingDef;
import com.ajjpj.ahcmapper.mappingdef.builtin.value.BigDecimalMappingDef;
import com.ajjpj.ahcmapper.mappingdef.builtin.value.CharToStringMappingDef;
import com.ajjpj.ahcmapper.mappingdef.builtin.value.ClassValueMappingDef;
import com.ajjpj.ahcmapper.mappingdef.builtin.value.DateValueMappingDef;
import com.ajjpj.ahcmapper.mappingdef.builtin.value.EnumValueMappingDef;
import com.ajjpj.ahcmapper.mappingdef.builtin.value.IntFromNumberValueMappingDef;
import com.ajjpj.ahcmapper.mappingdef.builtin.value.LongFromNumberValueMappingDef;
import com.ajjpj.ahcmapper.mappingdef.builtin.value.PrimitiveIdentityValueMappingDef;
import com.ajjpj.ahcmapper.mappingdef.builtin.value.ShortFromNumberValueMappingDef;
import com.ajjpj.ahcmapper.mappingdef.builtin.value.StringToCharMappingDef;


/**
 * This class servers to build an OmdMapper instance. It knows the built-in mappings for primitive types and collections, and
 *  it allows customization and registration of system specific mappings.
 * 
 * @author EHAASEC
 */
public class AhcMapperBuilder {
    public static final AtomicReference<AhcMapperLogger> defaultLogger = new AtomicReference<AhcMapperLogger>(new AhcMapperLogger.StdOutLogger ());
    
    private static final AhcValueMappingDef <?, ?>[] BUILTIN_VALUE_MAPPINGS = new AhcValueMappingDef[] {
            new PrimitiveIdentityValueMappingDef (), 
            new LongFromNumberValueMappingDef (), new IntFromNumberValueMappingDef (), new ShortFromNumberValueMappingDef(),
            new DateValueMappingDef(), 
            new EnumValueMappingDef(), new ClassValueMappingDef(),
            new StringToCharMappingDef(), new CharToStringMappingDef(), new BigDecimalMappingDef()};
    
    private static final AhcObjectMappingDef <?, ?>[] BUILTIN_OBJECT_MAPPINGS = new AhcObjectMappingDef <?, ?>[] {
        new ArrayObjectMappingDef(), new SetObjectMappingDef()
    };

    private final List <AhcValueMappingDef  <?, ?>> userDefinedValueMappings  = new ArrayList <AhcValueMappingDef  <?,?>> ();
    private final List <AhcObjectMappingDef <?, ?>> userDefinedObjectMappings = new ArrayList <AhcObjectMappingDef <?,?>> ();
//    TODO private final List <AhcObjectEnhancer<?, ?>> userDefinedEnhancers = new ArrayList<AhcObjectEnhancer<?,?>> ();
    
    private AhcObjectMappingDef<?, ?> listMappingDef = new ListWithIndexObjectMappingDef();
    
    private final AhcMapperInstanceProviderBuilder instanceProvider = new AhcMapperInstanceProviderBuilder();
    private AhcMapperEquivalenceStrategy equivalenceStrategy = new EqualsBasedEquivalenceStrategy(AhcMapperEqualsProvider.NATURAL);
    
    private AhcDeProxyStrategy deProxyStrategy = AhcDeProxyStrategy.NULL;
    private AhcMapperExceptionHandler exceptionHandler = AhcMapperExceptionHandler.SIMPLE;
    
    private AhcMapperLogger logger = defaultLogger.get();
    
    public AhcMapperBuilder withLogger (AhcMapperLogger logger) {
        this.logger = logger;
        return this;
    }
    
    public AhcMapperBuilder withInstanceProviderExtension (AhcMapperInstanceProviderExtension extension) {
        instanceProvider.withExtension(extension);
        return this;
    }

    public AhcMapperBuilder withEquivalenceStrategy (AhcMapperEquivalenceStrategy equivalenceStrategy) {
        this.equivalenceStrategy = equivalenceStrategy;
        return this;
    }
    
    public AhcMapperBuilder withDeProxyStrategy (AhcDeProxyStrategy deProxyStrategy) {
        this.deProxyStrategy = deProxyStrategy;
        return this;
    }
    
    public AhcMapperBuilder withValueMapping (AhcValueMappingDef <?, ?> mapping) {
        userDefinedValueMappings.add (mapping);
        return this;
    }
    
    public AhcMapperBuilder withObjectMapping (AhcObjectMappingDef <?, ?> mapping) {
        userDefinedObjectMappings.add (mapping);
        return this;
    }
    
    public AhcMapperBuilder withForwardMapping (PropertyBasedObjectMappingBuilder<?, ?> mapping) {
        userDefinedObjectMappings.add (mapping.build());
        return this;
    }
    
    public AhcMapperBuilder withBackwardsMapping (PropertyBasedObjectMappingBuilder<?, ?> mapping) {
        userDefinedObjectMappings.add (mapping.buildBackwards());
        return this;
    }
    
    public AhcMapperBuilder withBidirectionalMapping (PropertyBasedObjectMappingBuilder<?, ?> mapping) {
        userDefinedObjectMappings.add (mapping.build());
        userDefinedObjectMappings.add (mapping.buildBackwards());
        return this;
    }
    
//    public AhcMapperBuilder addObjectMapping (BidirectionalMapping mapping) {
//        userDefinedObjectMappings.addAll (mapping.getMappings());
//        return this;
//    }
    
    public AhcMapperBuilder withListMapping (AhcMapperListStrategy listStrategy) {
        switch(listStrategy) {
        case LIST: this.listMappingDef = new ListWithIndexObjectMappingDef(); break;
        case SET:  this.listMappingDef = new ListWithoutOrderOrDuplicatesObjectMappingDef(); break;
        case NONE: this.listMappingDef = null; break;
        default: throw new IllegalArgumentException("unknown strategy " + listStrategy);
        }
        return this;
    }
    
//TODO    public AhcMapperBuilder addObjectEnhancer (AhcObjectEnhancer<?, ?> enhancer) {
//        userDefinedEnhancers.add (enhancer);
//        return this;
//    }
    
    public AhcMapper build () {
        final List <AhcValueMappingDef  <?, ?>> valueMappings  = new ArrayList <AhcValueMappingDef  <?, ?>> (userDefinedValueMappings); 
        final List <AhcObjectMappingDef <?, ?>> objectMappings = new ArrayList <AhcObjectMappingDef <?, ?>> (userDefinedObjectMappings);
        
        valueMappings .addAll (Arrays.asList (BUILTIN_VALUE_MAPPINGS));
        objectMappings.addAll (Arrays.asList (BUILTIN_OBJECT_MAPPINGS));
        
        if (listMappingDef != null) {
            objectMappings.add(listMappingDef);
        }
        
        final AhcMappingDefProvider mappingProvider = new AhcMappingDefProvider (valueMappings, objectMappings);
//  TODO      final ObjectEnhancerProvider enhancerProvider = new ObjectEnhancerProvider(userDefinedEnhancers);
        
        return new AhcMapperImpl (mappingProvider, deProxyStrategy, instanceProvider.build(), exceptionHandler, equivalenceStrategy, logger);
    }
    
    public static <S, T> PropertyBasedObjectMappingBuilder<S, T> newObjectMapping(Class<S> sourceClass, Class<T> targetClass) throws Exception {
        return newObjectMapping(sourceClass, targetClass, defaultLogger.get());
    }

    public static <S, T> PropertyBasedObjectMappingBuilder<S, T> newObjectMapping(Class<S> sourceClass, Class<T> targetClass, AhcMapperLogger logger) throws Exception {
        return new PropertyBasedObjectMappingBuilder<S, T>(logger, sourceClass, targetClass);
    }
}
