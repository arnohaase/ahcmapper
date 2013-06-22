package com.ajjpj.ahcmapper;

import static com.ajjpj.ahcmapper.builder.AhcMapperBuilder.newObjectMapping;

import org.junit.Assert;
import org.junit.Test;

import com.ajjpj.ahcmapper.AhcMapper;
import com.ajjpj.ahcmapper.builder.AhcMapperBuilder;

import com.ajjpj.ahcmapper.classes.ClassA;
import com.ajjpj.ahcmapper.classes.ClassB;
import com.ajjpj.ahcmapper.classes.InnerClassA;


public class PropertyBasedMappingTest extends Assert {

    @Test
    public void testAddOneWay () throws Exception {
        final AhcMapper mapper = new AhcMapperBuilder()
            .withBidirectionalMapping(newObjectMapping(ClassA.class, ClassB.class)
                .removeMapping("firstName")
                .removeMapping("lastName")
                .addOneWayMapping("firstName", String.class, "lastName", String.class)
            )
            .build ();
        
        final ClassA a = new ClassA();
        a.setFirstName("first");
        
        final ClassB b = mapper.map(a, ClassB.class);
        assertEquals("first", b.getLastName());
        
        final ClassA mappedA = mapper.map(b, ClassA.class);
        assertEquals(null, mappedA.getFirstName());
    }

    @Test
    public void testAddBackwardsOneWay () throws Exception {
        final AhcMapper mapper = new AhcMapperBuilder()
            .withBidirectionalMapping(newObjectMapping(ClassA.class, ClassB.class)
                .removeMapping("firstName")
                .removeMapping("lastName")
                .addBackwardsOneWayMapping("firstName", String.class, "lastName", String.class)
            )
            .build ();
        
        final ClassA a = new ClassA();
        a.setFirstName("first a");
        a.setLastName("last a");
        
        final ClassB b = mapper.map(a, ClassB.class);
        assertEquals(null, b.getFirstName());
        assertEquals(null, b.getLastName());
        
        b.setFirstName("first b");
        b.setLastName("last b");
        final ClassA mappedA = mapper.map(b, ClassA.class);
        assertEquals("last b", mappedA.getFirstName());
        assertEquals(null, mappedA.getLastName());
    }
    
    @Test
    public void testMakeOneWay () throws Exception {
        final AhcMapper mapper = new AhcMapperBuilder ()
            .withBidirectionalMapping(
                newObjectMapping(ClassA.class, ClassB.class)
                .makeOneWay ("firstName")
             )
             .build();
        
        final ClassA a = new ClassA ();
        a.setFirstName("Fritz");
        
        final ClassB b = new ClassB (); 
        mapper.map (a, b);
        assertEquals ("Fritz", b.getFirstName());

        b.setFirstName("Fred");
        mapper.map (b, a);
        assertEquals ("Fritz", a.getFirstName());
    }

    @Test
    public void testMakeBackwardsOneWay () throws Exception {
        final AhcMapper mapper = new AhcMapperBuilder ()
            .withBidirectionalMapping(
                newObjectMapping(ClassA.class, ClassB.class)
                .makeBackwardsOneWay ("firstName")
             )
             .build();
        
        final ClassA a = new ClassA ();
        a.setFirstName("Fritz");
        
        final ClassB b = new ClassB (); 
        mapper.map (a, b);
        assertEquals (null, b.getFirstName());
    
        b.setFirstName("Fred");
        mapper.map (b, a);
        assertEquals ("Fred", a.getFirstName());
    }

    @Test
    public void testOverridesAndOgnl () throws Exception {
        final AhcMapper mapper = new AhcMapperBuilder()
            .withBidirectionalMapping(newObjectMapping(ClassA.class, ClassB.class)
                .removeMapping("phone")
                .addMapping("phone[0].other", String.class, "lastName", String.class)
                .addMapping("lastName", String.class, "firstName", String.class)
                .addOneWayMapping("phone.size()", Integer.class, "numChildren", Long.class)
            )
            .build ();
        
        final ClassA a = new ClassA ();
        a.setLastName("Mustermann");
        a.getPhone().add (new InnerClassA ("123", "other1"));
        a.getPhone().add (new InnerClassA ("456", "other2"));
        a.getPhone().add (new InnerClassA ("789", "other3"));
        
        final ClassB b = mapper.map (a, ClassB.class);
        
        assertTrue (b.getPhone().isEmpty());
        assertEquals ("other1", b.getLastName());
        assertEquals ("Mustermann", b.getFirstName());
        assertEquals (3L, b.getNumChildren());

        final ClassA mappedA = new ClassA ();
        mappedA.getPhone().add (new InnerClassA ("a", "b"));
        mapper.map (b, mappedA);
        assertEquals ("other1", mappedA.getPhone().get(0).getOther());
        assertEquals ("Mustermann", mappedA.getLastName());
    }
    
    @Test
    public void testSameSource() throws Exception {
        // test that a single field can be mapped to several fields
        final AhcMapper mapper = new AhcMapperBuilder ()
        .withBidirectionalMapping(newObjectMapping(ClassA.class, ClassA.class)
            .addMapping ("firstName", String.class, "lastName", String.class)
        )
        .build ();

        final ClassA a = new ClassA ();
        a.setFirstName ("first");
        
        final ClassA mapped = mapper.map (a, ClassA.class);
        assertEquals("first", mapped.getFirstName ());
        assertEquals("first", mapped.getLastName ());
    }
    
}
