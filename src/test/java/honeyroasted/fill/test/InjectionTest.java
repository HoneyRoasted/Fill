package honeyroasted.fill.test;

import honeyroasted.fill.Inject;
import honeyroasted.fill.Injector;
import honeyroasted.fill.reflect.ReflectionInjector;
import honeyroasted.jype.system.resolver.reflection.JTypeToken;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InjectionTest {

    @Test
    public void test() {
        Injector injector =
                ReflectionInjector.builder()
                        .bind(int.class).toInstance(52)
                        .bind(String.class, TestAnnotation.class).toInstance("test annotation")
                        .bind(String.class, TestValueAnnotation.class).toFactory(target -> target.get(TestValueAnnotation.class).value())
                        .bind(new JTypeToken<List<String>>(){}).toInstance(Arrays.asList("hello", "world"))
                        .bind(new JTypeToken<List<Integer>>(){}).toInstance(Arrays.asList(1, 2, 3))
                        .bind(String.class, "qualified").toInstance("qualified")
                        .bind(String.class, Inject.class).toInstance("string")
                        .bind(boolean.class, "namedBoolean").toInstance(true)
                        .build();

        TestObject obj = injector.createAndInject(TestObject.class);

        assertEquals(52, obj.x);

        assertEquals(Arrays.asList("hello", "world"), obj.listStr);

        assertEquals(Arrays.asList("hello", "world"), obj.listWildObj);
        assertEquals(Arrays.asList(1, 2, 3), obj.listInt);

        assertEquals("test annotation", obj.k);
        assertEquals("hello factory", obj.testFactory);

        assertEquals("string", obj.z);
        assertEquals("string", obj.name);
        assertEquals("string", obj.desc);

        assertEquals("qualified", obj.qualified);

        assertEquals(true, obj.namedBoolean);
        assertEquals(false, obj.aBoolean);
        System.out.println(obj);
    }

}
