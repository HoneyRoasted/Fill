import honeyroasted.fill.DummyObject;
import honeyroasted.fill.Inject;
import honeyroasted.fill.InjectionAnnotation;
import honeyroasted.fill.Injector;
import honeyroasted.fill.bindings.Matchers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Test {
    @Inject private final String meaningOfLife = null;
    @Inject private Integer stuff;
    private final Integer otherStuff;

    @Inject
    private Test(Integer test) {
        this.otherStuff = test;
    }

    public static void main(String[] args) {
        final int[] i = {0};
        Injector injector = Injector.builder()
                .bind(String.class).toInstance("42")
                .bind(Integer.class).toProvider(() -> i[0]++)
                .build();

        System.out.println(injector.createAndInject(Test.class));
    }

    @Override
    public String toString() {
        return "Test{" +
                "meaningOfLife='" + meaningOfLife + '\'' +
                ", stuff=" + stuff +
                ", otherStuff=" + otherStuff +
                '}';
    }
}
