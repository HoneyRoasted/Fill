package honeyroasted.fill.test;

import honeyroasted.fill.Inject;
import honeyroasted.fill.InjectionException;
import honeyroasted.fill.InjectionResult;
import honeyroasted.fill.Injector;
import honeyroasted.fill.bindings.Matchers;
import honeyroasted.fill.reflect.Aggregators;
import honeyroasted.fill.reflect.ReflectionInjector;
import honeyroasted.fill.reflect.ReflectionInjectorBuilder;
import honeyroasted.jype.system.JTypeSystem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReflectionInjectorTest {

    private static abstract class Base<T> {
        public abstract void set(T value);
    }

    private static class Bridged extends Base<String> {
        public int calls;

        @Inject
        @Override
        public void set(String value) {
            this.calls++;
        }
    }

    private static class Parent {
        public int calls;

        @Inject
        protected void set(String value) {
            this.calls++;
        }
    }

    private static class Child extends Parent {
        @Inject
        @Override
        public void set(String value) {
            this.calls++;
        }
    }

    private static class Methods {
        public int init;
        public String hidden;

        @Inject
        public void init() {
            this.init++;
        }

        @Inject
        private void hidden(String value) {
            this.hidden = value;
        }
    }

    private static class StaticParent {
        @Inject
        public static String value;
        public static String method;

        @Inject
        public static void method(String value) {
            StaticParent.method = value;
        }
    }

    private static class StaticChild extends StaticParent {

    }

    private static class Fields {
        @Inject
        public String value;
    }

    private static class PrivateConstructor {
        public String value;

        @Inject
        private PrivateConstructor(String value) {
            this.value = value;
        }
    }

    private static class Ambiguous {
        public Ambiguous(@Inject String a) {}

        public Ambiguous(@Inject Object b) {}
    }

    private static class AllMethods {
        public int calls;

        public void set(Object value) {
            this.calls++;
        }
    }

    @Test
    public void testBridgeMethodInjectedOnce() {
        Injector<?, ?> injector = ReflectionInjector.builder()
                .bind(String.class).toInstance("A")
                .build();

        Bridged bridged = new Bridged();
        injector.inject(bridged);
        assertEquals(1, bridged.calls);
    }

    @Test
    public void testOverriddenMethodInjectedOnce() {
        Injector<?, ?> injector = ReflectionInjector.builder()
                .bind(String.class).toInstance("A")
                .build();

        Child child = new Child();
        injector.inject(child);
        assertEquals(1, child.calls);
    }

    @Test
    public void testZeroArgAndPrivateMethods() {
        Injector<?, ?> injector = ReflectionInjector.builder()
                .bind(String.class).toInstance("A")
                .build();

        Methods methods = new Methods();
        injector.inject(methods);
        assertEquals(1, methods.init);
        assertEquals("A", methods.hidden);
    }

    @Test
    public void testSuperclassStaticInjection() {
        Injector<?, ?> injector = ReflectionInjector.builder()
                .bind(String.class).toInstance("A")
                .build();

        injector.injectStatic(StaticChild.class);
        assertEquals("A", StaticParent.value);
        assertEquals("A", StaticParent.method);
    }

    @Test
    public void testErrorMessage() {
        ReflectionInjector injector = new ReflectionInjector(Matchers.type(String.class).to((target, system) -> InjectionResult.error("boom")), JTypeSystem.RUNTIME_REFLECTION, (target, obj) -> true, Aggregators.ANNOTATED_FIELDS, Aggregators.ANNOTATED_METHODS, Aggregators.ANNOTATED_CONSTRUCTORS);

        InjectionException ex = assertThrows(InjectionException.class, () -> injector.inject(new Fields()));
        assertEquals("boom", ex.getMessage());
    }

    @Test
    public void testToBuilderKeepsSettings() {
        ReflectionInjector injector = ReflectionInjector.builder()
                .fieldAggregator(cls -> List.of())
                .bind(String.class).toInstance("A")
                .build();

        Fields fields = new Fields();
        injector.toBuilder().build().inject(fields);
        assertNull(fields.value);
    }

    @Test
    public void testBuildIsolatedFromBuilder() {
        ReflectionInjectorBuilder builder = ReflectionInjector.builder();
        ReflectionInjector injector = builder.build();
        builder.bind(String.class).toInstance("A");

        Fields fields = new Fields();
        injector.inject(fields);
        assertNull(fields.value);
    }

    @Test
    public void testPrivateConstructor() {
        Injector<?, ?> injector = ReflectionInjector.builder()
                .bind(String.class).toInstance("A")
                .build();

        assertEquals("A", injector.create(PrivateConstructor.class).value);
    }

    @Test
    public void testAmbiguousConstructor() {
        Injector<?, ?> injector = ReflectionInjector.builder()
                .bind(String.class).toInstance("A")
                .build();

        assertThrows(InjectionException.class, () -> injector.create(Ambiguous.class));
    }

    @Test
    public void testAllMethodsSkipsObject() {
        Injector<?, ?> injector = ReflectionInjector.builder()
                .methodAggregator(Aggregators.ALL_METHODS)
                .bind(Object.class).toInstance("A")
                .build();

        AllMethods methods = new AllMethods();
        injector.inject(methods);
        assertEquals(1, methods.calls);
    }

}
