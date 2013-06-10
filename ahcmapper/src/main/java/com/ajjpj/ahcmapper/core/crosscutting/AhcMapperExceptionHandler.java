package com.ajjpj.ahcmapper.core.crosscutting;


/**
 * This interface is due to the existence of checked exceptions, which can not be tunneled through callbacks gracefully.<p />
 * 
 * Implementations are expected to throw some kind of unchecked exception. They must *not* return in a regular fashion.
 * 
 * @author arno
 */
public interface AhcMapperExceptionHandler {
    void handle(Throwable th);
    
    AhcMapperExceptionHandler SIMPLE = new AhcMapperExceptionHandler() {
        @Override
        public void handle(Throwable th) {
            if (th instanceof Error) {
                throw (Error) th;
            }
            if(th instanceof RuntimeException) {
                throw (RuntimeException) th;
            }
            throw new RuntimeException(th);
        }
    };
}
