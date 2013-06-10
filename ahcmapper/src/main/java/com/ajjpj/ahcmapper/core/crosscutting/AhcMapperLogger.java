package com.ajjpj.ahcmapper.core.crosscutting;


public interface AhcMapperLogger {
    void debug (String msg);
    void info (String msg);
    void warn (String msg);
    void error (String msg);

    public enum Level {
        debug, info, warn, error;
        
        private Level() {
            padded = String.format("%5s", name()).toUpperCase();
        }
        
        public final String padded;
    }

    AhcMapperLogger DEBUG_STDOUT = new StdOutLogger(Level.debug);
    
    class StdOutLogger extends AbstractAhcMapperLogger {
        public StdOutLogger() {
            this(Level.warn);
        }
        
        public StdOutLogger (Level level) {
            setLevel(level);
        }
        
        @Override
        protected void msg(Level level, String msg) {
            System.out.println(level.padded + ": " + msg);
        }
    }
    
    abstract class AbstractAhcMapperLogger implements AhcMapperLogger {
        private Level level = Level.warn;
        
        public void setLevel(Level level) {
            this.level = level;
        }
        
        @Override
        public void debug(String msg) {
            if(level == Level.debug) {
                msg (Level.debug, msg);
            }
        }

        @Override
        public void info(String msg) {
            if(level == Level.debug || level == Level.info) {
                msg (Level.info, msg);
            }
        }

        @Override
        public void warn(String msg) {
            if(level == Level.debug || level == Level.info || level == Level.warn) {
                msg (Level.warn, msg);
            }
        }

        @Override
        public void error(String msg) {
            msg (Level.error, msg);
        }

        protected abstract void msg(Level level, String msg);
    }
}
