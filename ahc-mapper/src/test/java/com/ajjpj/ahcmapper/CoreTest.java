package com.ajjpj.ahcmapper;

import static com.ajjpj.ahcmapper.builder.AhcMapperBuilder.newObjectMapping;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ajjpj.ahcmapper.builder.AhcMapperBuilder;
import com.ajjpj.ahcmapper.classes.ClassCyclicChild;
import com.ajjpj.ahcmapper.classes.ClassCyclicParent;
import com.ajjpj.ahcmapper.core.AhcMapperWorker;
import com.ajjpj.ahcmapper.core.AhcObjectMappingDef;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperPath;
import com.ajjpj.ahcmapper.core.diff.AhcMapperDiffBuilder;


public class CoreTest extends Assert {
    @Test
    public void testCyclicRef () throws Exception {
        final ClassCyclicParent parent = new ClassCyclicParent();
        final ClassCyclicChild child = new ClassCyclicChild();
        
        parent.setChild (child);
        child.setParent (parent);
        
        final AhcMapper mapper = new AhcMapperBuilder()
            .withBidirectionalMapping (newObjectMapping(ClassCyclicParent.class, ClassCyclicParent.class))
            .withBidirectionalMapping (newObjectMapping(ClassCyclicChild.class, ClassCyclicChild.class))
            .build ();
        
        final ClassCyclicParent mappedParent = mapper.map (parent, ClassCyclicParent.class);
        
        assertNotSame (parent, mappedParent);
        assertSame (mappedParent, mappedParent.getChild().getParent());
    }
    
    @Test
    public void testCyclicRefWithList () throws Exception {
        final ClassCyclicParent parent = new ClassCyclicParent();
        final ClassCyclicChild child1 = new ClassCyclicChild();
        final ClassCyclicChild child2 = new ClassCyclicChild();
        
        parent.getChildList().add(child1);
        parent.getChildList().add(child2);
        child1.setParent (parent);
        child2.setParent (parent);
        
        final AhcMapper mapper = new AhcMapperBuilder()
            .withBidirectionalMapping (newObjectMapping(ClassCyclicParent.class, ClassCyclicParent.class))
            .withBidirectionalMapping (newObjectMapping(ClassCyclicChild.class, ClassCyclicChild.class))
            .build ();
        
        final ClassCyclicParent mappedParent = mapper.map (parent, ClassCyclicParent.class);
        
        assertNotSame (parent, mappedParent);
        assertSame (mappedParent, mappedParent.getChildList().get(0).getParent());
    }
    
    @Test
    public void testObjectMappingPerElementClass () {
        final AhcObjectMappingDef <Object, Object> stringListMapping = new AhcObjectMappingDef<Object, Object>() {
            @Override
            public boolean canHandle(Class<?> fromClass, Class<?> fromElementClass, Class<?> toClass, Class<?> toElementClass) {
                return fromClass == List.class && toClass == List.class && fromElementClass == Integer.class && toElementClass == String.class; 
            }

            @Override
            public boolean isCacheable() {
                return true;
            }
            
            @Override
            public void diff(Object source1, Object source2, Class<?> sourceClass, Class<?> sourceElementClass, Class<?> targetClass, Class<?> targetElementClass, AhcMapperPath targetPath, AhcMapperDiffBuilder diff, AhcMapperWorker worker)
                    throws Exception {
                throw new UnsupportedOperationException();
            }
            
            @SuppressWarnings("unchecked")
            @Override
            public Object map(Object source, Class<?> sourceClass, Class<?> sourceElementClass,
                    Object target, Class<?> targetClass, Class<?> targetElementClass,
                    AhcMapperPath path, AhcMapperWorker worker) throws Exception {
                final LinkedList<String> result = new LinkedList<String>();
                for (Integer i: (Collection<Integer>) source) {
                    result.add ("number " + i);
                }
                return result;
            }
        };
        
        final AhcMapper mapper = new AhcMapperBuilder()
            .withObjectMapping (stringListMapping)
            .build ();
        
        // initialize the MappingDefProvider with the default List mapper
        mapper.map (Arrays.asList ("a", "b"), List.class, String.class, null, List.class, String.class);
        
        final List<?> mapped = mapper.map (Arrays.asList (1, 2, 3), List.class, Integer.class, null, List.class, String.class);
        assertEquals ("number 1", mapped.get (0));
    }
}
