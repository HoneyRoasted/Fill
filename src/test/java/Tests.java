import honeyroasted.fill.Injector;
import honeyroasted.javatype.reflection.Token;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Tests {

    @Test
    public void test() {
        Injector injector =
                Injector.builder()
                        .bind(int.class).toInstance(52)
                        .bind(String.class, TestAnnotation.class).toInstance("test annotation")
                        .bind(new Token<List<String>>(){}.resolve()).toInstance(Arrays.asList("hello", "world"))
                        .bind(new Token<List<Integer>>(){}.resolve()).toInstance(Arrays.asList(1, 2, 3))
                        .bind(String.class).toInstance("string")
                        .build();

        TestObject obj = injector.createAndInject(TestObject.class);

        assertEquals(52, obj.x);

        assertEquals(Arrays.asList("hello", "world"), obj.listStr);
        assertEquals(Arrays.asList("hello", "world"), obj.listWildObj);
        assertEquals(Arrays.asList(1, 2, 3), obj.listInt);

        assertEquals("test annotation", obj.k);

        assertEquals("string", obj.z);
        assertEquals("string", obj.name);
        assertEquals("string", obj.desc);
    }

}
