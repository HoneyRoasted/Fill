package honeyroasted.fill;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An annotation which makes a field or method eligible for injection
 */
@Retention(RetentionPolicy.RUNTIME)
@InjectionAnnotation
public @interface Inject {

    String value() default "#EMPTY";

}
