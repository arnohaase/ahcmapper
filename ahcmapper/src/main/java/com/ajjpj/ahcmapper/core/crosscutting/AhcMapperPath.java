package com.ajjpj.ahcmapper.core.crosscutting;

import java.util.LinkedList;
import java.util.List;


public abstract class AhcMapperPath {
    public static final AhcMapperPath ROOT = new AhcMapperRootPath();

    public AhcMapperPath withSegment(String segment, Object graphElementMarker) {
        return new AhcMapperChildPath(segment, graphElementMarker, this);
    }

    public abstract boolean isRoot();
    public abstract String head();
    public abstract Object getMarker();
    public abstract AhcMapperPath tail();
    
    public abstract List<Object> getSegments();

    public String getDotSeparatedRepresentation() {
        final StringBuilder sb = new StringBuilder();
        collectDotSeparated(sb);
        return sb.toString();
    }
    
    abstract void collectDotSeparated(StringBuilder sb);
    
    @Override
    public String toString() {
        return getSegments().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if(! (obj instanceof AhcMapperPath)) {
            return false;
        }
        
        return getSegments().equals(((AhcMapperPath)obj).getSegments());
    }

    @Override
    public int hashCode() {
        return getSegments().hashCode();
    }
    
    //TODO optimized equals, hashCode
    
    private static final class AhcMapperRootPath extends AhcMapperPath {
        @Override
        public boolean isRoot() {
            return true;
        }
        
        @Override
        public List<Object> getSegments() {
            return new LinkedList<Object>();
        }
        
        @Override
        public String head() {
            throw new IllegalStateException("no head on root path");
        }
        
        @Override
        public Object getMarker() {
            return null;
        }
        
        @Override
        public AhcMapperPath tail() {
            throw new IllegalStateException("no tail on root path");
        }
        
        @Override
        void collectDotSeparated(StringBuilder sb) {
            throw new IllegalStateException("not dot-separated representation of root path");
        }
    }
    
    private static final class AhcMapperChildPath extends AhcMapperPath {
        private final String head;
        private final Object graphElementMarker;
        private final AhcMapperPath tail;
        
        public AhcMapperChildPath(String head, Object graphElementMarker, AhcMapperPath tail) {
            this.head = head;
            this.graphElementMarker = graphElementMarker;
            this.tail = tail;
        }
        
        @Override
        public boolean isRoot() {
            return false;
        }
        
        @Override
        public String head() {
            return head;
        }

        public Object getMarker() {
            return graphElementMarker;
        }
        
        @Override
        public AhcMapperPath tail() {
            return tail;
        }
        
        @Override
        void collectDotSeparated(StringBuilder sb) {
            if(tail == ROOT) {
                sb.append(head);
            }
            else {
                tail.collectDotSeparated(sb);
                sb.append("." + head);
            }
        }
        
        @Override
        public List<Object> getSegments() {
            //TODO this can be optimized by providing a view
            final LinkedList<Object> result = (LinkedList<Object>) tail.getSegments();
            result.addFirst(head);
            return result;
        }
    }
}
