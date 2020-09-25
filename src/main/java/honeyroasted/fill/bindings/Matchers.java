package honeyroasted.fill.bindings;

import honeyroasted.javatype.JavaType;

import java.lang.annotation.Annotation;

public interface Matchers {

    static Matcher annotation(Class<? extends Annotation> type) {
        return target -> target.has(type);
    }

    static Matcher type(Class<?> type) {
        return target -> target.type().getType().isAssignableFrom(type);
    }

    static Matcher type(JavaType type) {
        return target -> type.isAssignableTo(target.type());
    }


}
