package honeyroasted.fill;

import honeyroasted.javatype.JavaType;
import honeyroasted.javatype.JavaTypes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

public class InjectionTarget {
    private JavaType type;
    private List<? extends Annotation> annotations;

    public InjectionTarget(JavaType type, List<? extends Annotation> annotations) {
        this.type = type;
        this.annotations = annotations;
    }

    public InjectionTarget(Field field) {
        this(JavaTypes.of(field.getGenericType()), Arrays.asList(field.getAnnotations()));
    }

    public InjectionTarget(Parameter parameter) {
        this(JavaTypes.of(parameter.getParameterizedType()), Arrays.asList(parameter.getAnnotations()));
    }

    public JavaType type() {
        return this.type;
    }

    public List<? extends Annotation> annotations() {
        return this.annotations;
    }

    public boolean has(Class<? extends Annotation> cls) {
        return this.annotations.stream().anyMatch(cls::isInstance);
    }

    public <T extends Annotation> T get(Class<T> cls) {
        return (T) this.annotations.stream().filter(cls::isInstance).findFirst().orElse(null);
    }

}
