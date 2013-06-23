package com.ajjpj.ahcmapper;

import static com.ajjpj.ahcmapper.builder.AhcMapperBuilder.newObjectMapping;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.ajjpj.ahcmapper.builder.AhcMapperBuilder;
import com.ajjpj.ahcmapper.classes.ClassA;
import com.ajjpj.ahcmapper.classes.ClassB;
import com.ajjpj.ahcmapper.classes.InnerClassA;
import com.ajjpj.ahcmapper.classes.InnerClassB;
import com.ajjpj.ahcmapper.classes.MapperTestEnum;


public class SimpleTest extends Assert {
    @Test
    public void testSimple () throws Exception {
        final AhcMapper mapper = new AhcMapperBuilder ()
            .withBidirectionalMapping (newObjectMapping (ClassA.class, ClassB.class))
            .withBidirectionalMapping (newObjectMapping (InnerClassA.class, InnerClassB.class))
            .build ();
        
        final ClassA a = new ClassA ();
        a.setFirstName ("Heino");
        a.setLastName ("Mustermann");
        a.setNumChildren(99);
        a.setBirthday (new Date(1234567));
        a.setE (MapperTestEnum.b);
        a.getPhone().add (new InnerClassA ("123", "a"));
        a.getPhone().add (new InnerClassA ("456", "b"));
        
        final ClassB b = mapper.map (a, ClassB.class);
        
        assertEquals ("Heino", b.getFirstName());
        assertEquals ("Mustermann", b.getLastName());
        assertEquals (99, b.getNumChildren());
        assertEquals (new Date (1234567), b.getBirthday());
        assertNotSame (a.getBirthday(), b.getBirthday());
        assertSame (MapperTestEnum.b, b.getE());
        
        assertEquals (2, b.getPhone().size());
        assertEquals ("123", b.getPhone().get(0).getPhone());
        assertEquals ("a",   b.getPhone().get(0).getOther());
        assertEquals ("456", b.getPhone().get(1).getPhone());
        assertEquals ("b",   b.getPhone().get(1).getOther());
    }
}
