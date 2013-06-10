package com.ajjpj.ahcmapper.core.crosscutting;


public interface AhcDeProxyStrategy {
    <S> S deProxy(S object);
    
    AhcDeProxyStrategy NULL = new AhcDeProxyStrategy() {
        @Override
        public <S> S deProxy(S object) {
            return object;
        }
    };
}
