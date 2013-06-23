package com.ajjpj.ahcmapper;

import com.ajjpj.ahcmapper.builder.AhcMapperBuilder;
import com.ajjpj.ahcmapper.classes.*;
import com.ajjpj.ahcmapper.core.diff.AhcMapperDiff;
import com.ajjpj.ahcmapper.core.diff.AhcMapperDiffEntry;
import com.ajjpj.ahcmapper.core.diff.builder.*;
import com.ajjpj.ahcmapper.core.equivalence.AhcMapperEquivalenceStrategy;
import com.ajjpj.ahcmapper.core.equivalence.equals.AhcMapperEqualsProvider;
import com.ajjpj.ahcmapper.mappingdef.builtin.internal.EqualsPlaceholderMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

import static com.ajjpj.ahcmapper.builder.AhcMapperBuilder.newObjectMapping;


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

        assertEquals(5, diff.getEntries().size());
        
        assertEquals(AhcMapperRefDiffItem.class, diff.getSingleEntry("root").getItem().getClass());
        
        assertEquals(AhcMapperValueDiffItem.class, diff.getSingleEntry("targetId").getItem().getClass());
        assertEquals(true, diff.getSingleEntry("targetId").getItem().isCausedByStructuralChange());
        
        assertEquals(AhcMapperRefDiffItem.class, diff.getSingleEntry("targetChild").getItem().getClass());
        assertEquals(true, diff.getSingleEntry("targetChild").getItem().isCausedByStructuralChange());

        assertEquals(AhcMapperValueDiffItem.class, diff.getSingleEntry("targetChild.targetId").getItem().getClass());
        assertEquals(true, diff.getSingleEntry("targetChild.targetId").getItem().isCausedByStructuralChange());

        assertEquals(AhcMapperValueDiffItem.class, diff.getSingleEntry("targetChild.targetAttrib1").getItem().getClass());
        assertEquals(true, diff.getSingleEntry("targetChild.targetAttrib1").getItem().isCausedByStructuralChange());
    }

    @Test
    public void testRefChangeFromNull() throws Exception {
        final AhcMapper mapper = createMapper();
        
        final SourceParentWithId source1 = new SourceParentWithId(1, "a");
        final SourceParentWithId source2 = new SourceParentWithId(1, "a");

        source2.setSourceChild(new SourceChildWithId(1, "a", 1));
        
        final AhcMapperDiff diff = mapper.diff(source1, source2, SourceParentWithId.class, TargetParentWithId.class);

        assertEquals(4, diff.getEntries().size());
        
        assertEquals(AhcMapperRefDiffItem.class, diff.getSingleEntry("targetChild").getItem().getClass());
        assertEquals(null,                              diff.getSingleEntry("targetChild").getItem().getOldValue());
        assertEquals(new TargetChildWithId(1, null, 0), diff.getSingleEntry("targetChild").getItem().getNewValue());
        assertEquals(false, diff.getSingleEntry("targetChild").getItem().isCausedByStructuralChange());

        assertEquals(AhcMapperValueDiffItem.class, diff.getSingleEntry("targetChild.targetId").getItem().getClass());
        assertEquals(0, diff.getSingleEntry("targetChild.targetId").getItem().getOldValue());
        assertEquals(1, diff.getSingleEntry("targetChild.targetId").getItem().getNewValue());
        assertEquals(false, diff.getSingleEntry("targetChild.targetId").getItem().isCausedByStructuralChange());

        assertEquals(AhcMapperValueDiffItem.class, diff.getSingleEntry("targetChild.targetAttrib1").getItem().getClass());
        assertEquals(null, diff.getSingleEntry("targetChild.targetAttrib1").getItem().getOldValue());
        assertEquals("a",  diff.getSingleEntry("targetChild.targetAttrib1").getItem().getNewValue());
        assertEquals(true, diff.getSingleEntry("targetChild.targetAttrib1").getItem().isCausedByStructuralChange());

        assertEquals(AhcMapperValueDiffItem.class, diff.getSingleEntry("targetChild.targetAttrib2").getItem().getClass());
        assertEquals(0, diff.getSingleEntry("targetChild.targetAttrib2").getItem().getOldValue());
        assertEquals(1, diff.getSingleEntry("targetChild.targetAttrib2").getItem().getNewValue());
        assertEquals(true, diff.getSingleEntry("targetChild.targetAttrib2").getItem().isCausedByStructuralChange());
    }

    @Test
    public void testRefChangeToNull() throws Exception {
        final AhcMapper mapper = createMapper();
        
        final SourceParentWithId source1 = new SourceParentWithId(1, "a");
        final SourceParentWithId source2 = new SourceParentWithId(1, "a");

        source1.setSourceChild(new SourceChildWithId(1, "a", 1));
        
        final AhcMapperDiff diff = mapper.diff(source1, source2, SourceParentWithId.class, TargetParentWithId.class);

        assertEquals(4, diff.getEntries().size());
        
        assertEquals(AhcMapperRefDiffItem.class, diff.getSingleEntry("targetChild").getItem().getClass());
        assertEquals(new TargetChildWithId(1, null, 0), diff.getSingleEntry("targetChild").getItem().getOldValue());
        assertEquals(null,                              diff.getSingleEntry("targetChild").getItem().getNewValue());
        assertEquals(false, diff.getSingleEntry("targetChild").getItem().isCausedByStructuralChange());

        assertEquals(AhcMapperValueDiffItem.class, diff.getSingleEntry("targetChild.targetId").getItem().getClass());
        assertEquals(1, diff.getSingleEntry("targetChild.targetId").getItem().getOldValue());
        assertEquals(0, diff.getSingleEntry("targetChild.targetId").getItem().getNewValue());
        assertEquals(true, diff.getSingleEntry("targetChild.targetId").getItem().isCausedByStructuralChange());

        assertEquals(AhcMapperValueDiffItem.class, diff.getSingleEntry("targetChild.targetAttrib1").getItem().getClass());
        assertEquals("a",  diff.getSingleEntry("targetChild.targetAttrib1").getItem().getOldValue());
        assertEquals(null, diff.getSingleEntry("targetChild.targetAttrib1").getItem().getNewValue());
        assertEquals(true, diff.getSingleEntry("targetChild.targetAttrib1").getItem().isCausedByStructuralChange());

        assertEquals(AhcMapperValueDiffItem.class, diff.getSingleEntry("targetChild.targetAttrib2").getItem().getClass());
        assertEquals(1, diff.getSingleEntry("targetChild.targetAttrib2").getItem().getOldValue());
        assertEquals(0, diff.getSingleEntry("targetChild.targetAttrib2").getItem().getNewValue());
        assertEquals(true, diff.getSingleEntry("targetChild.targetAttrib2").getItem().isCausedByStructuralChange());
    }

    @Test
    public void testSetElementAdded() throws Exception {
        final AhcMapper mapper = createMapper();

        final SourceParentWithId source1 = new SourceParentWithId(1, "a");
        final SourceParentWithId source2 = new SourceParentWithId(1, "a");

        source2.getSourceChildren().add(new SourceChildWithId(1, "b", 1));

        final AhcMapperDiff diff = mapper.diff(source1, source2, SourceParentWithId.class, TargetParentWithId.class);

        assertEquals(4, diff.getEntries().size());

        final AhcMapperDiffItem addedItem = diff.getSingleEntry("targetChildren.element").getItem();
        assertTrue(addedItem instanceof AhcMapperElementAddedDiffItem);
        assertEquals("element", addedItem.getPropertyIdentifier());
        assertEquals(null, addedItem.getOldValue());
        assertEquals(new TargetChildWithId(1, null, 0), addedItem.getNewValue());

        final AhcMapperDiffItem idItem = diff.getSingleEntry("targetChildren.element.targetId").getItem();
        assertEquals(0, idItem.getOldValue());
        assertEquals(1, idItem.getNewValue());

        final AhcMapperDiffItem attrib1Item = diff.getSingleEntry("targetChildren.element.targetAttrib1").getItem();
        assertEquals(null, attrib1Item.getOldValue());
        assertEquals("b", attrib1Item.getNewValue());

        final AhcMapperDiffItem attrib2Item = diff.getSingleEntry("targetChildren.element.targetAttrib2").getItem();
        assertEquals(0, attrib2Item.getOldValue());
        assertEquals(1, attrib2Item.getNewValue());
    }

    //TODO diff API: getEntries(target item) --> 'durchhangeln' von added / removed

    @Test
    public void testSetElementRemoved() throws Exception {
        final AhcMapper mapper = createMapper();

        final SourceParentWithId source1 = new SourceParentWithId(1, "a");
        final SourceParentWithId source2 = new SourceParentWithId(1, "a");

        source1.getSourceChildren().add(new SourceChildWithId(1, "b", 1));

        final AhcMapperDiff diff = mapper.diff(source1, source2, SourceParentWithId.class, TargetParentWithId.class);

        assertEquals(4, diff.getEntries().size());

        final AhcMapperDiffItem addedItem = diff.getSingleEntry("targetChildren.element").getItem();
        assertTrue(addedItem instanceof AhcMapperElementRemovedDiffItem);
        assertEquals("element", addedItem.getPropertyIdentifier());
        assertEquals(new TargetChildWithId(1, null, 0), addedItem.getOldValue());
        assertEquals(null, addedItem.getNewValue());

        final AhcMapperDiffItem idItem = diff.getSingleEntry("targetChildren.element.targetId").getItem();
        assertEquals(1, idItem.getOldValue());
        assertEquals(0, idItem.getNewValue());

        final AhcMapperDiffItem attrib1Item = diff.getSingleEntry("targetChildren.element.targetAttrib1").getItem();
        assertEquals("b", attrib1Item.getOldValue());
        assertEquals(null, attrib1Item.getNewValue());

        final AhcMapperDiffItem attrib2Item = diff.getSingleEntry("targetChildren.element.targetAttrib2").getItem();
        assertEquals(1, attrib2Item.getOldValue());
        assertEquals(0, attrib2Item.getNewValue());
    }

    @Test
    public void testSetElementAddedRemoved() throws Exception {
        final AhcMapper mapper = createMapper();

        final SourceParentWithId source1 = new SourceParentWithId(1, "a");
        final SourceParentWithId source2 = new SourceParentWithId(1, "a");

        source1.getSourceChildren().add(new SourceChildWithId(1, "a", 1));
        source2.getSourceChildren().add(new SourceChildWithId(2, "b", 2));

        final AhcMapperDiff diff = mapper.diff(source1, source2, SourceParentWithId.class, TargetParentWithId.class);

        assertEquals(8, diff.getEntries().size());
        assertEquals(2, diff.getEntries("targetChildren.element").size());

        boolean added = false;
        boolean removed = false;

        for(AhcMapperDiffEntry entry: diff.getEntries("targetChildren.element")) {
            if(entry.getItem() instanceof AhcMapperElementAddedDiffItem) {
                added = true;

                assertEquals(null, entry.getItem().getOldValue());
                assertEquals(new TargetChildWithId(1, null, 0), entry.getItem().getNewValue());

                final AhcMapperDiffItem idItem = diff.getSingleEntry("targetChildren.element.targetId", entry.getItem().getNewValue()).getItem();
                assertEquals(0, idItem.getOldValue());
                assertEquals(1, idItem.getNewValue());
                assertEquals(true, idItem.isCausedByStructuralChange());

                final AhcMapperDiffItem attrib1Item = diff.getSingleEntry("targetChildren.element.targetAttrib1", entry.getItem().getNewValue()).getItem();
                assertEquals(null, attrib1Item.getOldValue());
                assertEquals("a", attrib1Item.getNewValue());
                assertEquals(true, attrib1Item.isCausedByStructuralChange());

                final AhcMapperDiffItem attrib2Item = diff.getSingleEntry("targetChildren.element.targetAttrib2", entry.getItem().getNewValue()).getItem();
                assertEquals(1, attrib2Item.getOldValue());
                assertEquals(0, attrib2Item.getNewValue());
                assertEquals(true, attrib2Item.isCausedByStructuralChange());
            }
            else if(entry.getItem() instanceof AhcMapperElementRemovedDiffItem) {
                removed = true;

                assertEquals(new TargetChildWithId(2, null, 0), entry.getItem().getOldValue());
                assertEquals(null, entry.getItem().getNewValue());

                final AhcMapperDiffItem idItem = diff.getSingleEntry("targetChildren.element.targetId", entry.getItem().getOldValue()).getItem();
                assertEquals(0, idItem.getOldValue());
                assertEquals(1, idItem.getNewValue());
                assertEquals(true, idItem.isCausedByStructuralChange());

                final AhcMapperDiffItem attrib1Item = diff.getSingleEntry("targetChildren.element.targetAttrib1", entry.getItem().getOldValue()).getItem();
                assertEquals(null, attrib1Item.getOldValue());
                assertEquals("a", attrib1Item.getNewValue());
                assertEquals(true, attrib1Item.isCausedByStructuralChange());

                final AhcMapperDiffItem attrib2Item = diff.getSingleEntry("targetChildren.element.targetAttrib2", entry.getItem().getOldValue()).getItem();
                assertEquals(1, attrib2Item.getOldValue());
                assertEquals(0, attrib2Item.getNewValue());
                assertEquals(true, attrib2Item.isCausedByStructuralChange());
            }
        }

        final AhcMapperDiffItem addedItem = diff.getSingleEntry("targetChildren.element").getItem();
        assertTrue(addedItem instanceof AhcMapperElementRemovedDiffItem);
    }

    //TODO list
}
