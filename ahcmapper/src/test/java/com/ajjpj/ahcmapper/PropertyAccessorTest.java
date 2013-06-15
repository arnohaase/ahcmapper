package com.ajjpj.ahcmapper;

import org.junit.Assert;
import org.junit.Test;

import com.ajjpj.ahcmapper.mappingdef.composite.propertybased.AhcPropertyAccessor;
import com.ajjpj.ahcmapper.mappingdef.composite.propertybased.FieldBasedPropertyAccessor;
import com.ajjpj.ahcmapper.mappingdef.composite.propertybased.MethodBasedPropertyAccessor;
import com.ajjpj.ahcmapper.mappingdef.composite.propertybased.OgnlPropertyAccessor;

import com.ajjpj.ahcmapper.classes.ClassA;


public class PropertyAccessorTest extends Assert {
    final String readOnlyField = "ReadOnly"; 
    
    @Test
    public void testMethodBased() throws Exception {
        final AhcPropertyAccessor firstName = new MethodBasedPropertyAccessor("firstName", String.class, null, true, ClassA.class.getMethod("getFirstName"), ClassA.class.getMethod("setFirstName", String.class), ClassA.class);
        check(firstName);

        final AhcPropertyAccessor readOnly = new MethodBasedPropertyAccessor("firstName", String.class, null, true, ClassA.class.getMethod("getFirstName"), null, ClassA.class);
        assertEquals(false, readOnly.isWritable());
    }

    @Test
    public void testFieldBased() throws Exception {
        final AhcPropertyAccessor firstName = new FieldBasedPropertyAccessor("firstName", String.class, null, true, ClassA.class.getDeclaredField("firstName"), ClassA.class);
        check(firstName);
        
        final AhcPropertyAccessor readOnly = new FieldBasedPropertyAccessor("readOnly", String.class, null, true, PropertyAccessorTest.class.getDeclaredField("readOnlyField"), PropertyAccessorTest.class);
        assertEquals(false, readOnly.isWritable());
    }

    @Test
    public void testOgnl() throws Exception {
        final AhcPropertyAccessor firstName = new OgnlPropertyAccessor("firstName", String.class, null, true, ClassA.class);
        check(firstName);
    }
    
    private void check(AhcPropertyAccessor firstName) throws Exception {
        assertEquals("firstName", firstName.getName());
        assertEquals(String.class, firstName.getType());
        assertEquals(null, firstName.getElementType());
        assertEquals(true, firstName.isReadable());
        assertEquals(true, firstName.isWritable());
        assertEquals(true, firstName.isPrimary());
        
        final ClassA a = new ClassA();
        assertEquals(null, firstName.getValue(a));
        firstName.setValue(a, "Arno");
        assertEquals("Arno", firstName.getValue(a));
        firstName.setValue(a, null);
        assertEquals(null, firstName.getValue(a));
    }
}
