package com.ajjpj.ahcmapper;

import static com.ajjpj.ahcmapper.builder.AhcMapperBuilder.newObjectMapping;

import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.ajjpj.ahcmapper.builder.AhcMapperBuilder;
import com.ajjpj.ahcmapper.classes.ClassA;
import com.ajjpj.ahcmapper.classes.ClassB;
import com.ajjpj.ahcmapper.classes.SourceChildWithId;
import com.ajjpj.ahcmapper.classes.SourceParentWithId;
import com.ajjpj.ahcmapper.classes.TargetChildWithId;
import com.ajjpj.ahcmapper.classes.TargetParentWithId;
import com.ajjpj.ahcmapper.core.diff.AhcMapperDiff;
import com.ajjpj.ahcmapper.core.diff.AhcMapperDiffEntry;
import com.ajjpj.ahcmapper.core.diff.StepDetails;
import com.ajjpj.ahcmapper.core.diff.builder.AhcMapperElementAddedDiffItem;
import com.ajjpj.ahcmapper.core.diff.builder.AhcMapperRefDiffItem;
import com.ajjpj.ahcmapper.core.diff.builder.AhcMapperValueDiffItem;
import com.ajjpj.ahcmapper.core.equivalence.AhcMapperEquivalenceStrategy;
import com.ajjpj.ahcmapper.core.equivalence.equals.AhcMapperEqualsProvider;
import com.ajjpj.ahcmapper.mappingdef.builtin.internal.EqualsPlaceholderMap;


public class DiffTest extends Assert {
    
    final AhcMapperEquivalenceStrategy equivalenceStrategy = new AhcMapperEquivalenceStrategy() {
        @Override
        public <S, T> boolean referToSameTargetElement(S source1, S source2, Class<S> sourceClass, Class<T> targetClass) throws Exception {
            return getTargetEquivalenceMarker(source1, sourceClass, targetClass).equals(getTargetEquivalenceMarker(source2, sourceClass, targetClass));
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <S, T> IdentityHashMap<S, T> findEquivalentInstances(Collection<S> coll1, Collection<T> coll2, Class<S> coll1ElementClass, Class<T> coll2ElementClass) throws Exception {
            final EqualsPlaceholderMap<T> equalsMap = new EqualsPlaceholderMap<T>(AhcMapperEqualsProvider.NATURAL, coll2, coll2ElementClass);
            
            final IdentityHashMap<S, T> result = new IdentityHashMap<S, T>();
            for(S o: coll1) {
                if(equalsMap.containsKey((T) o)) {
                    result.put(o, equalsMap.get((T)o));
                }
            }
            return result;
        }
        
        @Override
        public <S> Object getTargetEquivalenceMarker(S source, Class<S> sourceClass, Class<?> targetClass) throws Exception {
            if(source == null) {
                return null;
            }
            
            if(sourceClass == SourceParentWithId.class) {
                final TargetParentWithId result = new TargetParentWithId();
                result.setTargetId(((SourceParentWithId) source).getSourceId());
                return result;
            }
            else if (sourceClass == SourceChildWithId.class) {
                final TargetChildWithId result = new TargetChildWithId();
                result.setTargetId(((SourceChildWithId) source).getSourceId());
                return result;
            }
            else if (Set.class.isAssignableFrom(sourceClass)) {
                return new HashSet<Object>();
            }
            throw new UnsupportedOperationException(sourceClass.getName());
        }
    };

    
    private AhcMapper createMapper() {
        try {
            return new AhcMapperBuilder()
//            .withLogger(AhcMapperLogger.DEBUG_STDOUT)
            .withBidirectionalMapping(
                    newObjectMapping(SourceParentWithId.class, TargetParentWithId.class)
                    .addMapping("sourceId", Integer.TYPE, "targetId", Integer.TYPE)
                    .addMapping("sourceAttrib", String.class, "targetAttrib", String.class)
                    .addMapping("sourceChild", SourceChildWithId.class, "targetChild", TargetChildWithId.class)
                    .addMapping("sourceChildren", Set.class, SourceChildWithId.class, "targetChildren", Set.class, TargetChildWithId.class)
                    )
                    .withBidirectionalMapping(
                            newObjectMapping(SourceChildWithId.class, TargetChildWithId.class)
                            .addMapping("sourceId", Integer.TYPE, "targetId", Integer.TYPE)
                            .addMapping("sourceAttrib1", String.class, "targetAttrib1", String.class)
                            .addMapping("sourceAttrib2", Integer.TYPE, "targetAttrib2", Integer.TYPE)
                            )
                            .withEquivalenceStrategy(equivalenceStrategy)
                            .build();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }
    
    @Test
    public void testSimpleDiff() throws Exception {
        final AhcMapper mapper = createMapper();

        final SourceParentWithId source1 = new SourceParentWithId(1, "attrib1");
        final SourceParentWithId source2 = new SourceParentWithId(1, "attrib2");
        
        final AhcMapperDiff diff = mapper.diff(source1, source2, SourceParentWithId.class, TargetParentWithId.class);
        assertEquals(1, diff.getEntries().size());
        
        final AhcMapperDiffEntry diffEntry = diff.getEntries().iterator().next();
        assertEquals("targetAttrib", diffEntry.getPath().getDotSeparatedRepresentation());
        
        assertEquals("targetAttrib", diffEntry.getItem().getPropertyIdentifier());
        assertEquals("attrib1", diffEntry.getItem().getOldValue());
        assertEquals("attrib2", diffEntry.getItem().getNewValue());
        
        assertSame(diffEntry, diff.getSingleEntry("targetAttrib"));
    }

    @Test
    public void testDiffDifferentRoot() throws Exception {
        final ClassA source1 = new ClassA();
        final ClassA source2 = new ClassA();
        
        final AhcMapper mapper = new AhcMapperBuilder()
            .withBidirectionalMapping(newObjectMapping(ClassA.class, ClassB.class))
            .build();
        
        final AhcMapperDiff diff = mapper.diff(source1, source2, ClassA.class, ClassB.class);
        assertEquals(1, diff.getEntries().size());
        
        final AhcMapperDiffEntry entry = diff.getEntries().iterator().next();
        assertEquals("root", entry.getPath().getDotSeparatedRepresentation());
        
        assertEquals(null, entry.getItem().getOldTargetMarker());
        assertEquals(null, entry.getItem().getNewTargetMarker());
        assertEquals("root", entry.getItem().getPropertyIdentifier());
        
        assertTrue(entry.getItem().getOldValue() instanceof ClassB);
        assertTrue(entry.getItem().getNewValue() instanceof ClassB);
    }

    @Test
    public void testRefChange() throws Exception {
        final AhcMapper mapper = createMapper();
        
        final SourceParentWithId source1 = new SourceParentWithId(1, "a");
        final SourceParentWithId source2 = new SourceParentWithId(2, "a");

        source1.setSourceChild(new SourceChildWithId(1, "a", 1));
        source2.setSourceChild(new SourceChildWithId(2, "b", 1));
        
        final AhcMapperDiff diff = mapper.diff(source1, source2, SourceParentWithId.class, TargetParentWithId.class);
        for (AhcMapperDiffEntry entry: diff.getEntries()) {
            System.out.println(entry.getPath().getDotSeparatedRepresentation() + ": " + entry.getItem().getClass().getSimpleName());
            System.out.println("    " + entry.getItem().getPropertyIdentifier() + ": " + entry.getItem().getOldValue() + " -> " + entry.getItem().getNewValue());
        }

        assertEquals(5, diff.getEntries().size());
        
        assertEquals(AhcMapperRefDiffItem.class, diff.getSingleEntry("root").getItem().getClass());
        
        assertEquals(AhcMapperValueDiffItem.class, diff.getSingleEntry("targetId").getItem().getClass());
        assertEquals(false, diff.getSingleEntry("targetId").getItem().hasSameParent());
        
        assertEquals(AhcMapperRefDiffItem.class, diff.getSingleEntry("targetChild").getItem().getClass());
        assertEquals(false, diff.getSingleEntry("targetChild").getItem().hasSameParent());

        assertEquals(AhcMapperValueDiffItem.class, diff.getSingleEntry("targetChild.targetId").getItem().getClass());
        assertEquals(false, diff.getSingleEntry("targetChild.targetId").getItem().hasSameParent());

        assertEquals(AhcMapperValueDiffItem.class, diff.getSingleEntry("targetChild.targetAttrib1").getItem().getClass());
        assertEquals(false, diff.getSingleEntry("targetChild.targetAttrib1").getItem().hasSameParent());
    }

    @Test
    public void testRefChangeFromNull() throws Exception {
        fail("todo");
    }

    @Test
    public void testRefChangeToNull() throws Exception {
        fail("todo");
    }

    @Test
    public void testRefChangeFromNullWithNonNullAttributeDefault() throws Exception {
        // target side attribute is initialized with a value other than null --> base diff on that
        fail("todo"); 
    }

    @Test
    public void testRefChangeToNullWithNonNullAttributeDefault() throws Exception {
        fail("todo");
    }
    
    @Test
    public void testCollectionElementedAddedRemoved() throws Exception {
        fail("todo");
    }
    
    @Test
    public void testParentChildDiff() throws Exception {
        final AhcMapper mapper = createMapper();
        
        final SourceParentWithId source1 = new SourceParentWithId(1, "attrib1");
        final SourceParentWithId source2 = new SourceParentWithId(1, "attrib2");
        
        source1.setSourceChild(new SourceChildWithId(5, "a", 1));
        source2.setSourceChild(new SourceChildWithId(5, "x", 1));
        
        source1.getSourceChildren().add(source1.getSourceChild());
        source2.getSourceChildren().add(source2.getSourceChild());
        
        source1.getSourceChildren().add(new SourceChildWithId(3, "a", 3));
        source2.getSourceChildren().add(new SourceChildWithId(4, "b", 4));
        
        final AhcMapperDiff diff = mapper.diff(source1, source2, SourceParentWithId.class, TargetParentWithId.class);

        assertEquals(5, diff.getEntries().size());
        
        //TODO check paths incl. target markers
        
        assertEquals(new TargetParentWithId(1, null), diff.getSingleEntry("targetAttrib").getItem().getOldTargetMarker());
        assertEquals(new TargetParentWithId(1, null), diff.getSingleEntry("targetAttrib").getItem().getNewTargetMarker());
        assertEquals("attrib1", diff.getSingleEntry("targetAttrib").getItem().getOldValue());
        assertEquals("attrib2", diff.getSingleEntry("targetAttrib").getItem().getNewValue());
        assertEquals(true, diff.getSingleEntry("targetAttrib").getItem().hasSameParent());
        
        assertEquals("targetAttrib", diff.getSingleEntry("targetAttrib").getPath().getDotSeparatedRepresentation());
        assertEquals(new TargetParentWithId(1, null), ((StepDetails) diff.getSingleEntry("targetAttrib").getPath().getMarker()).getParentTarget1());
        assertEquals(new TargetParentWithId(1, null), ((StepDetails) diff.getSingleEntry("targetAttrib").getPath().getMarker()).getParentTarget2());
        assertSame(source1,                           ((StepDetails) diff.getSingleEntry("targetAttrib").getPath().getMarker()).getParentSource1());
        assertSame(source2,                           ((StepDetails) diff.getSingleEntry("targetAttrib").getPath().getMarker()).getParentSource2());
        assertEquals(true, diff.getSingleEntry("targetAttrib").getItem().hasSameParent());
        
        assertEquals(new TargetChildWithId(5, null, 0), diff.getSingleEntry("targetChild.targetAttrib1").getItem().getOldTargetMarker());
        assertEquals(new TargetChildWithId(5, null, 0), diff.getSingleEntry("targetChild.targetAttrib1").getItem().getNewTargetMarker());
        assertEquals("a", diff.getSingleEntry("targetChild.targetAttrib1").getItem().getOldValue());
        assertEquals("x", diff.getSingleEntry("targetChild.targetAttrib1").getItem().getNewValue());
        assertEquals(true, diff.getSingleEntry("targetChild.targetAttrib1").getItem().hasSameParent());

        assertEquals("targetChild.targetAttrib1", diff.getSingleEntry("targetChild.targetAttrib1").getPath().getDotSeparatedRepresentation());
        assertEquals(new TargetParentWithId(1, null), ((StepDetails) diff.getSingleEntry("targetChild.targetAttrib1").getPath().tail().getMarker()).getParentTarget1());
        assertEquals(new TargetParentWithId(1, null), ((StepDetails) diff.getSingleEntry("targetChild.targetAttrib1").getPath().tail().getMarker()).getParentTarget2());
        assertSame(source1, ((StepDetails) diff.getSingleEntry("targetChild.targetAttrib1").getPath().tail().getMarker()).getParentSource1());
        assertSame(source2, ((StepDetails) diff.getSingleEntry("targetChild.targetAttrib1").getPath().tail().getMarker()).getParentSource2());
        
        //TODO test getMarker().getChild...
        
        assertEquals(new TargetChildWithId(5, null, 0), diff.getSingleEntry("targetChildren.element.targetAttrib1").getItem().getOldTargetMarker());
        assertEquals(new TargetChildWithId(5, null, 0), diff.getSingleEntry("targetChildren.element.targetAttrib1").getItem().getNewTargetMarker());
        assertEquals("a", diff.getSingleEntry("targetChildren.element.targetAttrib1").getItem().getOldValue());
        assertEquals("x", diff.getSingleEntry("targetChildren.element.targetAttrib1").getItem().getNewValue());
        assertEquals(true, diff.getSingleEntry("targetChildren.element.targetAttrib1").getItem().hasSameParent());

        assertEquals("targetChildren.element.targetAttrib1", diff.getSingleEntry("targetChildren.element.targetAttrib1").getPath().getDotSeparatedRepresentation());
        assertEquals(new TargetChildWithId(5, null, 0), ((StepDetails) diff.getSingleEntry("targetChildren.element.targetAttrib1").getPath().getMarker()).getParentTarget1());
        assertEquals(new TargetChildWithId(5, null, 0), ((StepDetails) diff.getSingleEntry("targetChildren.element.targetAttrib1").getPath().getMarker()).getParentTarget2());
        assertSame(source1.getSourceChild(),            ((StepDetails) diff.getSingleEntry("targetChildren.element.targetAttrib1").getPath().getMarker()).getParentSource1());
        assertSame(source2.getSourceChild(),            ((StepDetails) diff.getSingleEntry("targetChildren.element.targetAttrib1").getPath().getMarker()).getParentSource2());
        assertEquals(new HashSet<Object>(),     ((StepDetails) diff.getSingleEntry("targetChildren.element.targetAttrib1").getPath().tail().getMarker()).getParentTarget1());
        assertEquals(new HashSet<Object>(),     ((StepDetails) diff.getSingleEntry("targetChildren.element.targetAttrib1").getPath().tail().getMarker()).getParentTarget2());
        assertSame(source1.getSourceChildren(), ((StepDetails) diff.getSingleEntry("targetChildren.element.targetAttrib1").getPath().tail().getMarker()).getParentSource1());
        assertSame(source2.getSourceChildren(), ((StepDetails) diff.getSingleEntry("targetChildren.element.targetAttrib1").getPath().tail().getMarker()).getParentSource2());
        assertEquals(new TargetParentWithId(1, null), ((StepDetails) diff.getSingleEntry("targetChildren.element.targetAttrib1").getPath().tail().tail().getMarker()).getParentTarget1());
        assertEquals(new TargetParentWithId(1, null), ((StepDetails) diff.getSingleEntry("targetChildren.element.targetAttrib1").getPath().tail().tail().getMarker()).getParentTarget2());
        assertSame(source1,                           ((StepDetails) diff.getSingleEntry("targetChildren.element.targetAttrib1").getPath().tail().tail().getMarker()).getParentSource1());
        assertSame(source2,                           ((StepDetails) diff.getSingleEntry("targetChildren.element.targetAttrib1").getPath().tail().tail().getMarker()).getParentSource2());
        
        final Collection<AhcMapperDiffEntry> addedRemoved = diff.getEntries("targetChildren.element");
        assertEquals(2, addedRemoved.size());
        
        boolean added = false;
        boolean removed = false;
        
        for(AhcMapperDiffEntry entry: addedRemoved) {
            assertEquals("targetChildren.element", entry.getPath().getDotSeparatedRepresentation());
            
            @SuppressWarnings("unchecked")
            final Set<TargetChildWithId> oldTarget = (Set<TargetChildWithId>) entry.getItem().getOldTargetMarker();
            @SuppressWarnings("unchecked")
            final Set<TargetChildWithId> newTarget = (Set<TargetChildWithId>) entry.getItem().getNewTargetMarker();
            
            assertEquals(2, oldTarget.size());
            assertEquals(2, newTarget.size());
            
            assertTrue(oldTarget.contains(new TargetChildWithId(3, null, 0)));
            assertTrue(oldTarget.contains(new TargetChildWithId(5, null, 0)));

            assertTrue(newTarget.contains(new TargetChildWithId(4, null, 0)));
            assertTrue(newTarget.contains(new TargetChildWithId(5, null, 0)));
            
            if(entry.getItem() instanceof AhcMapperElementAddedDiffItem) {
                added = true;
                assertEquals(null, entry.getItem().getOldValue());
                assertEquals(new TargetChildWithId(4, null, 0), entry.getItem().getNewValue());
            }
            else {
                removed = true;
                assertEquals(new TargetChildWithId(3, null, 0), entry.getItem().getOldValue());
                assertEquals(null, entry.getItem().getNewValue());
                
            }
        }
        
        assertTrue(added);
        assertTrue(removed);
    }
}
