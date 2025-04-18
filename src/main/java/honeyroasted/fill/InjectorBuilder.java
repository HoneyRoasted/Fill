package honeyroasted.fill;

import honeyroasted.fill.bindings.Binding;
import honeyroasted.fill.bindings.BindingBuilder;
import honeyroasted.fill.bindings.Matcher;
import honeyroasted.fill.bindings.Matchers;
import honeyroasted.fill.reflect.ReflectionInjectorBuilder;
import honeyroasted.jype.system.resolver.reflection.JTypeToken;
import honeyroasted.jype.type.JType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Generic interface for builders that build {@link Injector}s
 *
 * @param <T> This type
 */
public interface InjectorBuilder<T extends InjectorBuilder<T>> {

    /**
     * Adds one or more bindings to this builder
     *
     * @param bindings The bindings to add
     * @return This, for method chaining
     */
    T bind(Binding... bindings);

    /**
     * Creates a new {@link BindingBuilder} with the given matcher and returns it, the binding builder may then be
     * used to bind to a value and continue back into this builder
     *
     * @param matcher The matcher to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> bind(Matcher matcher) {
        return new BindingBuilder<T>(matcher, (T) this);
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on assignment rules), the
     * matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param type The type to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> bind(Type type) {
        return this.bind(Matchers.type(type));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on exact type equality), the
     * matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param type The type to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> exactBind(Type type) {
        return this.bind(Matchers.exactType(type));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on assignment rules), the 
     * matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param type The type to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> bind(JType type) {
        return this.bind(Matchers.type(type));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on exact type equality), the 
     * matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param type The type to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> exactBind(JType type) {
        return this.bind(Matchers.exactType(type));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on assignment rules), the 
     * matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param token The type to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> bind(JTypeToken<?> token) {
        return this.bind(Matchers.type(token));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on exact type equality), the 
     * matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param token The type to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> exactBind(JTypeToken<?> token) {
        return this.bind(Matchers.exactType(token));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on assignment rules) and the 
     * given annotation, the matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param type       The type to use
     * @param annotation The annotation to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> bind(Type type, Class<? extends Annotation> annotation) {
        return this.bind(Matchers.type(type).and(Matchers.annotation(annotation)));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on exact type equality) and the 
     * given annotation, the matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param type       The type to use
     * @param annotation The annotation to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> exactBind(Type type, Class<? extends Annotation> annotation) {
        return this.bind(Matchers.exactType(type).and(Matchers.annotation(annotation)));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on assignment rules) and the 
     * given annotation, the matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param type       The type to use
     * @param annotation The annotation to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> bind(JType type, Class<? extends Annotation> annotation) {
        return this.bind(Matchers.type(type).and(Matchers.annotation(annotation)));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on exact type equality) and the 
     * given annotation, the matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param type       The type to use
     * @param annotation The annotation to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> exactBind(JType type, Class<? extends Annotation> annotation) {
        return this.bind(Matchers.exactType(type).and(Matchers.annotation(annotation)));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on assignment rules) and the 
     * given annotation, the matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param token      The type to use
     * @param annotation The annotation to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> bind(JTypeToken<?> token, Class<? extends Annotation> annotation) {
        return this.bind(Matchers.type(token).and(Matchers.annotation(annotation)));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on exact type equality) and the 
     * given annotation, the matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param token      The type to use
     * @param annotation The annotation to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> exactBind(JTypeToken<?> token, Class<? extends Annotation> annotation) {
        return this.bind(Matchers.exactType(token).and(Matchers.annotation(annotation)));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on assignment rules) and the 
     * given name, the matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param type The type to use
     * @param name The name to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> bind(Type type, String name) {
        return this.bind(Matchers.type(type).and(Matchers.name(name)));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on exact type equality) and the 
     * given name, the matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param type The type to use
     * @param name The name to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> exactBind(Type type, String name) {
        return this.bind(Matchers.exactType(type).and(Matchers.name(name)));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on assignment rules) and the 
     * given name, the matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param type The type to use
     * @param name The name to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> bind(JType type, String name) {
        return this.bind(Matchers.type(type).and(Matchers.name(name)));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on exact type equality) and the 
     * given name, the matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param type The type to use
     * @param name The name to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> exactBind(JType type, String name) {
        return this.bind(Matchers.exactType(type).and(Matchers.name(name)));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on assignment rules) and the 
     * given name, the matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param token The type to use
     * @param name  The name to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> bind(JTypeToken<?> token, String name) {
        return this.bind(Matchers.type(token).and(Matchers.name(name)));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on exact type equality) and the 
     * given name, the matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param token The type to use
     * @param name  The name to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> exactBind(JTypeToken<?> token, String name) {
        return this.bind(Matchers.exactType(token).and(Matchers.name(name)));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on assignment rules), the given 
     * name and the given annotation, the matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param type       The type to use
     * @param name       The name to use
     * @param annotation The annotation to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> bind(Type type, String name, Class<? extends Annotation> annotation) {
        return this.bind(Matchers.type(type).and(Matchers.annotation(annotation)).and(Matchers.name(name)));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on exact type equality), the given 
     * name and the given annotation, the matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param type       The type to use
     * @param name       The name to use
     * @param annotation The annotation to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> exactBind(Type type, String name, Class<? extends Annotation> annotation) {
        return this.bind(Matchers.exactType(type).and(Matchers.annotation(annotation)).and(Matchers.name(name)));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on assignment rules), the given 
     * name and the given annotation, the matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param type       The type to use
     * @param name       The name to use
     * @param annotation The annotation to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> bind(JType type, String name, Class<? extends Annotation> annotation) {
        return this.bind(Matchers.type(type).and(Matchers.annotation(annotation)).and(Matchers.name(name)));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on exact type equality), the given 
     * name and the given annotation, the matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param type       The type to use
     * @param name       The name to use
     * @param annotation The annotation to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> exactBind(JType type, String name, Class<? extends Annotation> annotation) {
        return this.bind(Matchers.exactType(type).and(Matchers.annotation(annotation)).and(Matchers.name(name)));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on assignment rules), the given 
     * name and the given annotation, the matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param token      The type to use
     * @param name       The name to use
     * @param annotation The annotation to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> bind(JTypeToken<?> token, String name, Class<? extends Annotation> annotation) {
        return this.bind(Matchers.type(token).and(Matchers.annotation(annotation)).and(Matchers.name(name)));
    }

    /**
     * Creates a new {@link BindingBuilder} with a matcher that matches the given type (based on exact type equality)), the given 
     * name and the given annotation, the matcher builder may then be used to bind to a value and continue back into this builder
     *
     * @param token      The type to use
     * @param name       The name to use
     * @param annotation The annotation to use
     * @return A new {@link BindingBuilder} referencing this {@link ReflectionInjectorBuilder}
     */
    default BindingBuilder<T> exactBind(JTypeToken<?> token, String name, Class<? extends Annotation> annotation) {
        return this.bind(Matchers.exactType(token).and(Matchers.annotation(annotation).and(Matchers.name(name))));
    }

}
