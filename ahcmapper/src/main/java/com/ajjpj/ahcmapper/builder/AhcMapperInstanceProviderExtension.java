package com.ajjpj.ahcmapper.builder;

import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperInstanceProvider;


/**
 * This interface serves to customize the factory behavior. Its <code>canHandle</code>
 *  filters whether it can take care of a specific object creation request.
 * 
 * @author EHAASEC
 */
public interface AhcMapperInstanceProviderExtension extends AhcMapperInstanceProvider {
    boolean canHandle (Object source, Class<?> targetClass);
}
