package honeyroasted.fill.test;

import honeyroasted.fill.InjectionAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@InjectionAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface TestValueAnnotation {

    String value();

}
