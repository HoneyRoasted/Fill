package honeyroasted.fill;


import honeyroasted.javatype.Types;
import honeyroasted.javatype.informal.TypeInformal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a target for a single value injection
 */
public class InjectionTarget {
    private TypeInformal type;
    private List<? extends Annotation> annotations;

    /**
     * Creates a new {@link InjectionTarget}
     *
     * @param type The type of this injection target
     * @param annotations The annotations on this injection target
     */
    public InjectionTarget(TypeInformal type, List<? extends Annotation> annotations) {
        this.type = type;
        this.annotations = annotations;
    }

    /**
     * Creates a new {@link InjectionTarget} from a {@link Field}
     *
     * @param field The field to target
     */
    public InjectionTarget(Field field) {
        this(Types.type(field.getGenericType()), Arrays.asList(field.getAnnotations()));
    }

    /**
     * Creates a new {@link InjectionTarget} from a method {@link Parameter}
     *
     * @param parameter The parameter to target
     */
    public InjectionTarget(Parameter parameter) {
        this(Types.type(parameter.getParameterizedType()), Arrays.asList(parameter.getAnnotations()));
    }

    /**
     * @return The {@link TypeInformal} of this {@link InjectionTarget}
     */
    public TypeInformal type() {
        return this.type;
    }

    /**
     * @return The {@link Annotation}s on this {@link InjectionTarget}
     */
    public List<? extends Annotation> annotations() {
        return this.annotations;
    }

    /**
     * Checks if this {@link InjectionTarget} has an {@link Annotation} of the given type
     *
     * @param cls The annotation type
     * @return True if this target has an annotation of the given type
     */
    public boolean has(Class<? extends Annotation> cls) {
        return this.annotations.stream().anyMatch(cls::isInstance);
    }

    /**
     * Gets an {@link Annotation} on this {@link InjectionTarget} of the given type
     *
     * @param cls The annotation type
     * @param <T> The annotation type
     * @return The {@link Annotation}, or null if no annotation of type {@code T} was found
     */
    public <T extends Annotation> T get(Class<T> cls) {
        return (T) this.annotations.stream().filter(cls::isInstance).findFirst().orElse(null);
    }

}
