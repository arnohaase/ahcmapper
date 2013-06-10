package com.ajjpj.ahcmapper.builder.propertybased;

import org.junit.Assert;
import org.junit.Test;

import com.ajjpj.ahcmapper.builder.propertybased.AhcMapperExpressionParser;
import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperLogger;
import com.ajjpj.ahcmapper.mappingdef.composite.propertybased.AhcPropertyAccessor;
import com.ajjpj.ahcmapper.mappingdef.composite.propertybased.MethodBasedPropertyAccessor;
import com.ajjpj.ahcmapper.mappingdef.composite.propertybased.MethodPathBasedPropertyAccessor;
import com.ajjpj.ahcmapper.mappingdef.composite.propertybased.OgnlPropertyAccessor;

import com.ajjpj.ahcmapper.classes.*;


public class AhcMapperExpressionParserTest extends Assert {
    @Test
    public void testParseForPrimitive() throws Exception {
        // the 'numChildren' property has type Long.TYPE and *not* Long.class
        final AhcPropertyAccessor accessor = new AhcMapperExpressionParser(AhcMapperLogger.DEBUG_STDOUT).parse(ClassB.class, "numChildren", Long.class, null, true);
        assertTrue(accessor.isReadable());
        assertTrue(accessor.isWritable());
    }
    
    @Test
    public void testParseSingle() throws Exception {
        final AhcPropertyAccessor accessor = new AhcMapperExpressionParser(AhcMapperLogger.DEBUG_STDOUT).parse(ClassA.class, "firstName", String.class, null, true);
        
        assertEquals (MethodBasedPropertyAccessor.class, accessor.getClass());
        assertEquals (true, accessor.isPrimary());
        assertEquals (true, accessor.isReadable());
        assertEquals (true, accessor.isWritable());
        assertEquals ("firstName", accessor.getName());
        
        final ClassA a = new ClassA();
        accessor.setValue(a, "first");
        assertEquals("first", a.getFirstName());
        assertEquals("first", accessor.getValue(a));
    }

    @Test
    public void testParseSingleReadOnly() throws Exception {
        final AhcPropertyAccessor accessor = new AhcMapperExpressionParser(AhcMapperLogger.DEBUG_STDOUT).parse(ClassCyclicChild.class, "readOnly", String.class, null, true);

        assertEquals (MethodBasedPropertyAccessor.class, accessor.getClass());
        assertEquals (true, accessor.isPrimary());
        assertEquals (true, accessor.isReadable());
        assertEquals (false, accessor.isWritable());
        assertEquals ("readOnly", accessor.getName());
        
        final ClassCyclicChild o = new ClassCyclicChild();
        o.setWriteOnly("abc");
        assertEquals("abc", accessor.getValue(o));
    }

    @Test
    public void testParseSingleWriteOnly() throws Exception {
        final AhcPropertyAccessor accessor = new AhcMapperExpressionParser(AhcMapperLogger.DEBUG_STDOUT).parse(ClassCyclicChild.class, "writeOnly", String.class, null, true);
        
        assertEquals (MethodBasedPropertyAccessor.class, accessor.getClass());
        assertEquals (true, accessor.isPrimary());
        assertEquals (false, accessor.isReadable());
        assertEquals (true, accessor.isWritable());
        assertEquals ("writeOnly", accessor.getName());
        
        final ClassCyclicChild o = new ClassCyclicChild();
        accessor.setValue(o, "abc");
        assertEquals("abc", o.getReadOnly());
    }
    
    @Test
    public void testParseSimpleCascade() throws Exception {
        final AhcPropertyAccessor accessor = new AhcMapperExpressionParser(AhcMapperLogger.DEBUG_STDOUT).parse(ClassCyclicParent.class, "child.name", String.class, null, true);

        assertEquals (MethodPathBasedPropertyAccessor.class, accessor.getClass());
        assertEquals (true, accessor.isPrimary());
        assertEquals (true, accessor.isReadable());
        assertEquals (true, accessor.isWritable());
        assertEquals ("child.name", accessor.getName());
        
        final ClassCyclicParent o = new ClassCyclicParent();
        o.setChild(new ClassCyclicChild());
        accessor.setValue(o, "abc");
        assertEquals("abc", o.getChild().getName());
        assertEquals("abc", accessor.getValue(o));
        
        try {
            accessor.getValue(new ClassCyclicParent());
            fail("exception expected");
        }
        catch(NullPointerException exc) {
        }
    }

    @Test
    public void testParseCascadeReadOnly() throws Exception {
        final AhcPropertyAccessor accessor = new AhcMapperExpressionParser(AhcMapperLogger.DEBUG_STDOUT).parse(ClassCyclicParent.class, "child.readOnly", String.class, null, true);

        assertEquals (MethodPathBasedPropertyAccessor.class, accessor.getClass());
        assertEquals (true, accessor.isPrimary());
        assertEquals (true, accessor.isReadable());
        assertEquals (false, accessor.isWritable());
        assertEquals ("child.readOnly", accessor.getName());
        
        final ClassCyclicParent o = new ClassCyclicParent();
        o.setChild(new ClassCyclicChild());
        o.getChild().setWriteOnly("abc");
        assertEquals("abc", accessor.getValue(o));
    }

    @Test
    public void testParseCascadeWriteOnly() throws Exception {
        final AhcPropertyAccessor accessor = new AhcMapperExpressionParser(AhcMapperLogger.DEBUG_STDOUT).parse(ClassCyclicParent.class, "child.writeOnly", String.class, null, true);

        assertEquals (MethodPathBasedPropertyAccessor.class, accessor.getClass());
        assertEquals (true, accessor.isPrimary());
        assertEquals (false, accessor.isReadable());
        assertEquals (true, accessor.isWritable());
        assertEquals ("child.writeOnly", accessor.getName());
        
        final ClassCyclicParent o = new ClassCyclicParent();
        o.setChild(new ClassCyclicChild());
        accessor.setValue(o, "12345");
        assertEquals("12345", o.getChild().getReadOnly());
    }

    @Test
    public void testParseNullSafeCascade() throws Exception {
        final AhcPropertyAccessor accessor = new AhcMapperExpressionParser(AhcMapperLogger.DEBUG_STDOUT).parse(ClassCyclicParent.class, "child.?name", String.class, null, true);

        assertEquals (MethodPathBasedPropertyAccessor.class, accessor.getClass());
        assertEquals (true, accessor.isPrimary());
        assertEquals (true, accessor.isReadable());
        assertEquals (true, accessor.isWritable());
        assertEquals ("child.?name", accessor.getName());
        
        final ClassCyclicParent o = new ClassCyclicParent();
        o.setChild(new ClassCyclicChild());
        accessor.setValue(o, "abc");
        assertEquals("abc", o.getChild().getName());
        assertEquals("abc", accessor.getValue(o));
        
        assertEquals(null, accessor.getValue(new ClassCyclicParent()));
    }
    
    @Test
    public void testParseOgnl() throws Exception {
        final AhcPropertyAccessor accessor = new AhcMapperExpressionParser(AhcMapperLogger.DEBUG_STDOUT).parse(ClassA.class, "phone[0].phone", String.class, null, true);

        assertEquals (OgnlPropertyAccessor.class, accessor.getClass());
        assertEquals (true, accessor.isPrimary());
        assertEquals (true, accessor.isReadable());
        assertEquals (true, accessor.isWritable());
        assertEquals ("phone[0].phone", accessor.getName());
        
        final ClassA a = new ClassA();
        a.getPhone().add(new InnerClassA());
        accessor.setValue(a, "xyz");
        assertEquals("xyz", a.getPhone().get(0).getPhone());
        assertEquals("xyz", accessor.getValue(a));
    }
}
