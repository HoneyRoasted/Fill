package honeyroasted.fill.bindings;

import honeyroasted.jype.system.resolver.reflection.TypeToken;
import honeyroasted.jype.system.solver.TypeBound;
import honeyroasted.jype.system.solver.solvers.CompatibilityTypeSolver;
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
        return (target, system) -> target.name().equals(name);
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
        return (target, system) -> new CompatibilityTypeSolver()
                .bind(new TypeBound.Compatible(system.tryResolve(type), target.type()))
                .solve(system)
                .success();
    }

    /**
     * Creates a {@link Matcher} that matches {@link honeyroasted.fill.InjectionTarget}s with the given type
     *
     * @param type The type to match
     * @return A new {@link Matcher}
     */
    static Matcher type(Type type) {
        return (target, system) -> new CompatibilityTypeSolver()
                .bind(new TypeBound.Compatible(type, target.type()))
                .solve(system)
                .success();
    }

    /**
     * Creates a {@link Matcher} that matches {@link honeyroasted.fill.InjectionTarget}s with the given type
     *
     * @param token The type to match
     * @return A new {@link Matcher}
     */
    static Matcher type(TypeToken<?> token) {
        return (target, system) -> new CompatibilityTypeSolver()
                .bind(new TypeBound.Compatible(token.resolve(system), target.type()))
                .solve(system)
                .success();
    }


}
