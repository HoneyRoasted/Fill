package honeyroasted.fill;


import honeyroasted.javatype.Types;
import honeyroasted.javatype.informal.TypeInformal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

public class InjectionTarget {
    private TypeInformal type;
    private List<? extends Annotation> annotations;

    public InjectionTarget(TypeInformal type, List<? extends Annotation> annotations) {
        this.type = type;
        this.annotations = annotations;
    }

    public InjectionTarget(Field field) {
        this(Types.type(field.getGenericType()), Arrays.asList(field.getAnnotations()));
    }

    public InjectionTarget(Parameter parameter) {
        this(Types.type(parameter.getParameterizedType()), Arrays.asList(parameter.getAnnotations()));
    }

    public TypeInformal type() {
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
