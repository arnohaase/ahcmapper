package com.ajjpj.ahcmappera;

import com.ajjpj.ahcmapper.AhcMapper;
import com.ajjpj.ahcmapper.builder.AhcMapperBuilder;
import com.ajjpj.ahcmapper.classes.ClassA;
import com.ajjpj.ahcmapper.classes.ClassB;
import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;

import static com.ajjpj.ahcmapper.builder.AhcMapperBuilder.newObjectMapping;


@Ignore
public class AhcMapperTest extends TestCase {
//    @Test
//    public void testMapNull () throws Exception {
//        final AhcMapper mapper = new AhcMapperBuilder()
//                .withBidirectionalMapping(newObjectMapping(ClassA.class, ClassB.class)) //TODO mapNull == false
//                .build();
//
//        final ClassA a = new ClassA ();
//        final ClassB b = new ClassB ();
//        b.setLastName ("Mustermann");
//        mapper.map (a, b);
//        assertEquals ("Mustermann", b.getLastName());
//    }
//    @Test
//    public void testSelectiveListAsSet () throws Exception {
//        final AhcMapper mapper = new AhcMapperBuilder()
//            .addObjectMapping (new BidirectionalMapping (ClassA.class, ClassB.class))
//            .build ();
//
//        final ClassA a = new ClassA();
//        a.getListAsSet().add("a");
//        a.getListAsSet().add("d");
//        a.getListAsSet().add("c");
//        a.getListAsSet().add("e");
//
//        final ClassB b = new ClassB();
//        b.getListAsSet().add("c");
//        b.getListAsSet().add("d");
//        b.getListAsSet().add("e");
//
//        // the list has an @OmdListAsSet annotation in B, so B's order should be unchanged and new elements appended
//        mapper.map(a, b);
//        assertEquals(Arrays.asList("c", "d", "e", "a"), b.getListAsSet());
//
//        // there is *no* @OmdListAsSet in A, so in this direction the list should be mapped 'as is'
//        b.getListAsSet().add(1, "b");
//        mapper.map(b, a);
//        assertEquals(Arrays.asList("c", "b", "d", "e", "a"), a.getListAsSet());
//    }
//
//
//
//    public void testEnhancer () {
//        final AhcMapper mapper = new AhcMapperBuilder()
//        .addObjectMapping(new BidirectionalMapping (InnerClassB.class, InnerClassB.class))
//        .addObjectMapping(new BidirectionalMapping (MapperTestMarkedClass.class, MapperTestMarkedClass.class))
//        .addObjectEnhancer(new OmdObjectEnhancer<Object, Object>() {
//            public boolean canHandle(Class<?> fromClass, Class<?> fromElementClass, Class<?> toClass, Class<?> toElementClass) {
//                return MapperTestMarker.class.isAssignableFrom(toClass);
//            }
//            public void enhance(Object from, Object to) {
//                ((MapperTestMarker) to).setDummy("dummy!");
//            }
//        })
//        .build();
//
//        mapper.map (new InnerClassB(), InnerClassB.class);
//        final MapperTestMarkedClass mapped = mapper.map (new MapperTestMarkedClass(), MapperTestMarkedClass.class);
//        assertEquals ("dummy!", mapped.getDummy());
//    }
//
//    public void testFieldMappingDef () throws Exception {
//        final AhcMapper mapper = new AhcMapperBuilder ()
//            .addObjectMapping (new BidirectionalMapping (InnerClassA.class, InnerClassA.class)
//                .addForwardFieldMapping (new FieldMappingDef <InnerClassA, InnerClassA> () {
//                    public String getFromIdentifier () {
//                        return "other";
//                    }
//
//                    public void map (InnerClassA from, InnerClassA to, OmdMappingWorker worker) {
//                        to.setOther (from.getOther ().toUpperCase ());
//                    }
//                })
//            )
//            .build ();
//
//        final InnerClassA a = new InnerClassA ();
//        a.setOther ("aBcDe");
//        assertEquals ("ABCDE", mapper.map (a, InnerClassA.class).getOther ());
//    }
//
//    public void testContext () {
//        final AhcMapper mapper = new AhcMapperBuilder ()
//            .addObjectMapping (new BidirectionalMapping (ClassWithContext.class, ClassWithContext.class))
//            .addObjectMapping (new BidirectionalMapping (ClassRequiringContext.class, ClassRequiringContext.class)
//                .removeMapping ("price")
//                .addOneWayMapping ("amount", Double.TYPE, "price", PriceClass.class)
//                )
//            .withValueMapping (new AhcValueMappingDef<Double, PriceClass>() {
//                public boolean canHandle (Class<?> fromClass, Class<?> toClass) {
//                    return fromClass == Double.TYPE && toClass == PriceClass.class;
//                }
//
//                public boolean handlesNull () {
//                    return true;
//                }
//
//                public PriceClass map (Double from, AhcMapperWorker worker) throws Exception {
//                    return new PriceClass (from, worker.getContextInformation (TestCurrencyProvider.class).getCurrency ());
//                }
//            })
//            .build ();
//
//        final ClassWithContext o = new ClassWithContext ();
//        o.setCurrency ("USD");
//        o.setRequiringContext (new ClassRequiringContext ());
//        o.getRequiringContext ().setAmount (2.0);
//
//        final ClassWithContext mapped = mapper.map (o, ClassWithContext.class);
//        assertEquals ("2.0 USD", mapped.getRequiringContext ().getPrice ().toString ());
//    }
//
//    @Test
//    public void testGuard() {
//        final ObjectHolder<Boolean> isGuardActive = new ObjectHolder<Boolean> (false);
//
//        final AhcMapper mapper = new AhcMapperBuilder (new AhcMapperInstanceProviderBuilder ().build ())
//            .addObjectMapping (new BidirectionalMapping (ClassA.class, ClassB.class)
//                .addGuard ("firstName", new FieldMappingGuard() {
//                    public boolean shouldMap (Object fromParent, Object toParent) {
//                        return ! isGuardActive.value;
//                    }
//                })
//            )
//            .build ();
//
//        // forward without guard
//        final ClassA a = new ClassA();
//        a.setFirstName ("first");
//        a.setLastName ("last");
//
//        assertEquals("first", mapper.map(a, ClassB.class).getFirstName ());
//        assertEquals("last", mapper.map(a, ClassB.class).getLastName ());
//
//        // forward with guard
//        isGuardActive.value = true;
//        assertEquals(null, mapper.map(a, ClassB.class).getFirstName ());
//        assertEquals("last", mapper.map(a, ClassB.class).getLastName ());
//
//        // backward with guard
//        final ClassB b = new ClassB();
//        b.setFirstName ("first");
//        b.setLastName ("last");
//
//        assertEquals(null, mapper.map(b, ClassA.class).getFirstName ());
//        assertEquals("last", mapper.map(b, ClassA.class).getLastName ());
//
//        // backward without guard
//        isGuardActive.value = false;
//        assertEquals("first", mapper.map(b, ClassA.class).getFirstName ());
//        assertEquals("last", mapper.map(b, ClassA.class).getLastName ());
//    }
//
//
//    @Test
//    public void testSameTarget() {
//        // test that several fields can be mapped to the same field. That probably only makes
//        //  sense for "real" objects as a target so that the mappings fill different parts of the
//        //  object
//        final AhcMapper mapper = new AhcMapperBuilder ()
//            .withObjectMapping (new BidirectionalMapping (ClassA.class, ClassAWrapper.class)
//                .addForwardFieldMapping (new OmdFieldMappingAdapter("firstName", "a") {
//                    @Override
//                    protected Object mapProperty (Object fromPropertyValue, Object oldToValue, AhcMapperWorker worker) {
//                        if(oldToValue == null) {
//                            oldToValue = new ClassA();
//                        }
//                        ((ClassA)oldToValue).setFirstName ((String) fromPropertyValue);
//                        return oldToValue;
//                    }
//                })
//                .addForwardFieldMapping (new OmdFieldMappingAdapter("lastName", "a") {
//                    @Override
//                    protected Object mapProperty (Object fromPropertyValue, Object oldToValue, AhcMapperWorker worker) {
//                        if(oldToValue == null) {
//                            oldToValue = new ClassA();
//                        }
//                        ((ClassA)oldToValue).setLastName ((String) fromPropertyValue);
//                        return oldToValue;
//                    }
//                })
//            )
//        .build ();
//
//        final ClassA a = new ClassA();
//        a.setFirstName ("first");
//        a.setLastName ("last");
//
//        final ClassAWrapper mapped = mapper.map(a, ClassAWrapper.class);
//        assertEquals("first", mapped.getA().getFirstName ());
//        assertEquals("last",  mapped.getA().getLastName ());
//    }
}








