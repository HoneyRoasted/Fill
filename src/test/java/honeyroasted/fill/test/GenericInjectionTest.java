package honeyroasted.fill.test;

import honeyroasted.fill.Inject;
import honeyroasted.fill.Injector;
import honeyroasted.fill.reflect.ReflectionInjector;
import honeyroasted.jype.system.resolver.reflection.JTypeToken;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GenericInjectionTest {

    private static class Foo {
        @Inject
        private Map<String, Integer> map;
        @Inject
        public List<String> strList;

        @Inject
        public Foo() {}
    }

    @Test
    public void testMapInjection() {
        Map<?, ?> map = Map.of("1", 1);
        Injector<?, ?> injector = ReflectionInjector.builder()
                .bind(new JTypeToken<Map<String, Integer>>() {}).toInstance(map)
                .build();

        assertEquals(map, injector.createAndInject(Foo.class).map);
    }

    @Test
    public void testStrListInjection() {
        List<String> strs = List.of("A", "B", "C");
        Injector<?, ?> injector = ReflectionInjector.builder()
                .bind(new JTypeToken<List<String>>() {}).toInstance(strs)
                .build();

        assertEquals(strs, injector.createAndInject(Foo.class).strList);
    }

}
