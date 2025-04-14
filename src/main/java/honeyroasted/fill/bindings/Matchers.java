package honeyroasted.fill.bindings;

import honeyroasted.fill.Inject;
import honeyroasted.jype.system.resolver.reflection.JTypeToken;
import honeyroasted.jype.system.solver.constraints.JTypeConstraints;
import honeyroasted.jype.type.JType;

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
        return (target, system) -> system.operations().isCompatible(system.tryResolve(type), target.type(), JTypeConstraints.Compatible.Context.LOOSE_INVOCATION);
    }

    /**
     * Creates a {@link Matcher} that matches {@link honeyroasted.fill.InjectionTarget}s with the given type
     *
     * @param type The type to match
     * @return A new {@link Matcher}
     */
    static Matcher type(JType type) {
        return (target, system) -> system.operations().isCompatible(type, target.type(), JTypeConstraints.Compatible.Context.LOOSE_INVOCATION);
    }

    /**
     * Creates a {@link Matcher} that matches {@link honeyroasted.fill.InjectionTarget}s with the given type
     *
     * @param token The type to match
     * @return A new {@link Matcher}
     */
    static Matcher type(JTypeToken<?> token) {
        return (target, system) -> system.operations().isCompatible(token.resolve(system), target.type(), JTypeConstraints.Compatible.Context.LOOSE_INVOCATION);
    }


}
