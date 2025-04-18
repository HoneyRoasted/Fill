package honeyroasted.fill.bindings;

import honeyroasted.fill.Inject;
import honeyroasted.jype.system.resolver.reflection.JTypeToken;
import honeyroasted.jype.system.solver.constraints.JTypeConstraints;
import honeyroasted.jype.type.JType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

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
     * Creates a {@link Matcher} that matches {@link honeyroasted.fill.InjectionTarget}s with the given type, based
     * on assignment rules. For example, if the given type is Object and the injection target type is Integer, this matcher will
     * return true, since Integer can be assigned to Object. For an exact type match, use {@link Matchers#exactType(Type)}.
     *
     * @param type The type to match
     * @return A new {@link Matcher}
     */
    static Matcher type(Type type) {
        return (target, system) -> system.operations().isCompatible(system.tryResolve(type), target.type(), JTypeConstraints.Compatible.Context.LOOSE_INVOCATION);
    }

    /**
     * Creates a {@link Matcher} tht matches {@link  honeyroasted.fill.InjectionTarget} with  given type, based
     * on exact type equality. For example, if the given type is Object nd the injection target type is Integer, this matcher will
     * return false, since Integer does not equal Object. For a type match based on assignment rules, use {@link Matchers#type(Type)}.
     *
     * @param type The type to match
     * @return A new {@link Matcher}
     */
    static Matcher exactType(Type type) {
        return (target, system) -> target.type().typeEquals(system.tryResolve(type));
    }

    /**
     * Creates a {@link Matcher} that matches {@link honeyroasted.fill.InjectionTarget}s with the given type, based
     * on assignment rules. For example, if the given type is Object and the given type is Integer, this matcher will
     * return true, since Integer can be assigned to Object. For an exact type match, use {@link Matchers#exactType(JType)}.
     *
     * @param type The type to match
     * @return A new {@link Matcher}
     */
    static Matcher type(JType type) {
        return (target, system) -> system.operations().isCompatible(type, target.type(), JTypeConstraints.Compatible.Context.LOOSE_INVOCATION);
    }

    /**
     * Creates a {@link Matcher} tht matches {@link  honeyroasted.fill.InjectionTarget} with  given type, based
     * on exact type equality. For example, if the given type is Object nd the injection target type is Integer, this matcher will
     * return false, since Integer does not equal Object. For a type match based on assignment rules, use {@link Matchers#type(JType)}.
     *
     * @param type The type to match
     * @return A new {@link Matcher}
     */
    static Matcher exactType(JType type) {
        return (target, system) -> target.type().typeEquals(type);
    }

    /**
     * Creates a {@link Matcher} that matches {@link honeyroasted.fill.InjectionTarget}s with the given type, based
     * on assignment rules. For example, if the given type is Object and the given type is Integer, this matcher will
     * return true, since Integer can be assigned to Object. For an exact type match, use {@link Matchers#exactType(JTypeToken)}.
     *
     * @param token The type to match
     * @return A new {@link Matcher}
     */
    static Matcher type(JTypeToken<?> token) {
        return (target, system) -> system.operations().isCompatible(token.resolve(system), target.type(), JTypeConstraints.Compatible.Context.LOOSE_INVOCATION);
    }

    /**
     * Creates a {@link Matcher} tht matches {@link  honeyroasted.fill.InjectionTarget} with  given type, based
     * on exact type equality. For example, if the given type is Object nd the injection target type is Integer, this matcher will
     * return false, since Integer does not equal Object. For a type match based on assignment rules, use {@link Matchers#type(JTypeToken)}.
     *
     * @param token The type to match
     * @return A new {@link Matcher}
     */
    static Matcher exactType(JTypeToken<?> token) {
        return (target, system) -> target.type().typeEquals(token.resolve(system));
    }


}
