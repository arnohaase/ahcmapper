package com.ajjpj.ahcmapper.core.crosscutting;

import org.junit.Assert;
import org.junit.Test;


public class AhcMapperPathTest extends Assert {
    @Test
    public void testRoot () {
        assertEquals(null, AhcMapperPath.ROOT.getMarker());
    }

    @Test
    public void testPath() {
        final AhcMapperPath p1 = AhcMapperPath.ROOT.withSegment("p1", 1);
        final AhcMapperPath p2 = p1.withSegment("p2", 2);
        final AhcMapperPath p3 = p1.withSegment("p3", 3);
        
        assertSame(AhcMapperPath.ROOT, p1.tail());
        assertEquals("p1", p1.head());
        assertEquals(1, p1.getMarker());
        assertEquals("p1", p1.getDotSeparatedRepresentation());
        
        assertSame(p1, p2.tail());
        assertEquals("p2", p2.head());
        assertEquals(2, p2.getMarker());
        assertEquals("p1.p2", p2.getDotSeparatedRepresentation());

        assertSame(p1, p3.tail());
        assertEquals("p3", p3.head());
        assertEquals(3, p3.getMarker());
        assertEquals("p1.p3", p3.getDotSeparatedRepresentation());
    }
}
