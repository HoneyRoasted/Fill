package honeyroasted.fill.test;

import honeyroasted.fill.Inject;
import honeyroasted.fill.Injector;
import honeyroasted.fill.reflect.ReflectionInjector;
import honeyroasted.jype.system.resolver.reflection.JTypeToken;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WildcardInjectionTest {

    private static class Foo {
        @Inject
        List<? extends String> strings;
        @Inject
        List<? extends Number> numbers;
        @Inject
        List<? super Object> objects;

        @Inject
        public Foo() {}
    }

    @Test
    public void testSuccessfulString() {
        List<String> strs = List.of("A", "B", "C");
        Injector<?, ?> injector = ReflectionInjector.builder()
                .bind(new JTypeToken<List<String>>() {}).toInstance(strs)
                .build();

        assertEquals(strs, injector.createAndInject(Foo.class).strings);
    }

    @Test
    public void testSuccessfulNumber() {
        List<Integer> nums = List.of(1, 2, 3);
        Injector<?, ?> injector = ReflectionInjector.builder()
                .bind(new JTypeToken<List<Integer>>() {}).toInstance(nums)
                .build();

        assertEquals(nums, injector.createAndInject(Foo.class).numbers);
    }

    @Test
    public void testSuccessfulObjects() {
        List<Object> objects = List.of("A", "B", "C", "D");
        Injector<?, ?> injector = ReflectionInjector.builder()
                .bind(List.class).toInstance(objects)
                .build();

        assertEquals(objects, injector.createAndInject(Foo.class).objects);
    }

}
