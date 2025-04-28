package honeyroasted.fill.test;

import honeyroasted.fill.Inject;
import honeyroasted.fill.InjectionAnnotation;
import honeyroasted.fill.Injector;
import honeyroasted.fill.reflect.ReflectionInjector;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.junit.jupiter.api.Assertions.*;

public class BasicInjectionTest {

    @InjectionAnnotation
    @Retention(RetentionPolicy.RUNTIME)
    private @interface Bar {

    }

    private static class Foo {
        @Inject
        public static String staticValue;

        @Bar
        public String a;
        public String b;

        public String constructed;

        @Inject
        public Integer named;
        @Inject
        public Integer otherName;

        public Foo() {}

        public Foo(@Bar String constructed) {
            this.constructed = constructed;
        }


        public Foo setB(@Bar String b) {
            this.b = b;
            return this;
        }
    }

    @Test
    public void testStaticInjection() {
        Injector<?, ?> injector = ReflectionInjector.builder()
                .bind(String.class).toInstance("Hello")
                .build();

        injector.injectStatic(Foo.class);
        assertEquals("Hello", Foo.staticValue);
    }

    @Test
    public void testInjection() {
        Injector<?, ?> injector = ReflectionInjector.builder()
                .bind(String.class).toInstance("ABC")
                .build();

        Foo foo = injector.createAndInject(Foo.class);
        assertEquals("ABC", foo.constructed);
        assertEquals("ABC", foo.a);
        assertEquals("ABC", foo.b);
    }

    @Test
    public void testNameInjection() {
        Injector<?, ?> injector = ReflectionInjector.builder()
                .bind(Integer.class, "named").toInstance(1)
                .bind(Integer.class, "otherName").toInstance(2)
                .bind(String.class, "a").toInstance("ABC")
                .build();

        Foo foo = injector.createAndInject(Foo.class);
        assertEquals(1, foo.named);
        assertEquals(2, foo.otherName);
        assertEquals("ABC", foo.a);
        assertNull(foo.b);
    }

    @Test
    public void testInjectionOrder() {
        Injector<?, ?> injector1 = ReflectionInjector.builder()
                .bind(String.class, "a").toInstance("A")
                .bind(String.class).toInstance("B")
                .build();

        Injector<?, ?> injector2 = ReflectionInjector.builder()
                .bind(String.class).toInstance("B")
                .bind(String.class, "a").toInstance("A")
                .build();

        Foo foo1 = injector1.createAndInject(Foo.class);
        Foo foo2 = injector2.createAndInject(Foo.class);

        assertEquals("A", foo1.a);
        assertEquals("B", foo1.b);

        assertEquals("B", foo2.a);
        assertEquals("B", foo2.b);
    }


}
