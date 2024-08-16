package honeyroasted.fill.bindings;

import honeyroasted.fill.Inject;
import honeyroasted.jype.system.resolver.reflection.TypeToken;
import honeyroasted.jype.system.solver.bounds.TypeBound;
import honeyroasted.jype.type.Type;

import java.lang.annotation.Annotation;

/**
 * Utility interface for constructing common {@link Matcher}s
 */
public interface Matchers {

    /**
     * Creates a {@link Matcher} that matches {@link  honeyroasted.fill.InjectionTarget}s by their names
     *
     * @param name The name to match
     * @return A new {@link Matcher}
     */
    static Matcher name(String name) {
        return (target, system) -> target.name().equals(name) || (target.has(Inject.class) && target.get(Inject.class).value().equals(name));
    }

    /**
     * Creates a {@link Matcher} that matches {@link honeyroasted.fill.InjectionTarget}s with the given annotation
     *
     * @param type The annotation type to match
     * @return A new {@link Matcher}
     */
    static Matcher annotation(Class<? extends Annotation> type) {
        return (target, system) -> target.has(type);
    }

    /**
     * Creates a {@link Matcher} that matches {@link honeyroasted.fill.InjectionTarget}s with the given type
     *
     * @param type The type to match
     * @return A new {@link Matcher}
     */
    static Matcher type(java.lang.reflect.Type type) {
        return (target, system) -> system.operations().isCompatible(system.tryResolve(type), target.type(), TypeBound.Compatible.Context.ASSIGNMENT);
    }

    /**
     * Creates a {@link Matcher} that matches {@link honeyroasted.fill.InjectionTarget}s with the given type
     *
     * @param type The type to match
     * @return A new {@link Matcher}
     */
    static Matcher type(Type type) {
        return (target, system) -> system.operations().isCompatible(type, target.type(), TypeBound.Compatible.Context.ASSIGNMENT);
    }

    /**
     * Creates a {@link Matcher} that matches {@link honeyroasted.fill.InjectionTarget}s with the given type
     *
     * @param token The type to match
     * @return A new {@link Matcher}
     */
    static Matcher type(TypeToken<?> token) {
        return (target, system) -> system.operations().isCompatible(token.resolve(system), target.type(), TypeBound.Compatible.Context.ASSIGNMENT);
    }


}
