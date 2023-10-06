package honeyroasted.fill.bindings;

import honeyroasted.jype.system.solver.TypeBound;
import honeyroasted.jype.system.solver.solvers.AssignabilityTypeSolver;
import honeyroasted.jype.type.Type;

import java.lang.annotation.Annotation;

/**
 * Utility interface for constructing common {@link Matcher}s
 */
public interface Matchers {

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
    static Matcher type(Class<?> type) {
        return (target, system) -> new AssignabilityTypeSolver()
                .bind(new TypeBound.Subtype(system.resolve(type).get(), target.type()))
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
        return (target, system) -> new AssignabilityTypeSolver()
                .bind(new TypeBound.Subtype(type, target.type()))
                .solve(system)
                .success();
    }


}
