package honeyroasted.fill;

import honeyroasted.jype.system.JTypeSystem;
import honeyroasted.jype.type.JType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a target for a single value injection
 */
public class InjectionTarget {
    private String name;
    private JType type;
    private Class<?> rawType;
    private List<? extends Annotation> annotations;

    /**
     * Creates a new {@link InjectionTarget}
     *
     * @param name        The name of this injection target
     * @param type        The type of this injection target
     * @param rawType     The raw {@link Class} type of this injection target
     * @param annotations The annotations on this injection target
     */
    public InjectionTarget(String name, JType type, Class<?> rawType, List<? extends Annotation> annotations) {
        this.name = name;
        this.type = type;
        this.rawType = rawType;
        this.annotations = annotations;
    }

    /**
     * Creates a new {@link InjectionTarget} from a {@link Field}
     *
     * @param system The {@link JTypeSystem} to use for resolving types
     * @param field  The field to target
     */
    public InjectionTarget(JTypeSystem system, Field field) {
        this(field.getName(), system.tryResolve(field.getGenericType()), field.getType(), Arrays.asList(field.getAnnotations()));
    }

    /**
     * Creates a new {@link InjectionTarget} from a method {@link Parameter}
     *
     * @param system    The {@link JTypeSystem} to use for resolving types
     * @param parameter The parameter to target
     */
    public InjectionTarget(JTypeSystem system, Parameter parameter) {
        this(parameter.getName(), system.tryResolve(parameter.getParameterizedType()), parameter.getType(), Arrays.asList(parameter.getAnnotations()));
    }

    /**
     * @return The name of this {@link InjectionTarget}
     */
    public String name() {
        return this.name;
    }

    /**
     * @return The {@link JType} of this {@link InjectionTarget}
     */
    public JType type() {
        return this.type;
    }

    /**
     * @return The raw {@link Class} type of this {@link InjectionTarget}
     */
    public Class<?> rawType() {
        return this.rawType;
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
