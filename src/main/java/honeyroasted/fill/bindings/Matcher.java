package honeyroasted.fill.bindings;

import honeyroasted.fill.InjectionResult;
import honeyroasted.fill.InjectionTarget;
import honeyroasted.jype.system.TypeSystem;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a predicate which can match {@link InjectionTarget}s
 */
public interface Matcher extends BiPredicate<InjectionTarget, TypeSystem> {

    /**
     * Creates a {@link Binding} from this matcher which claims all {@link InjectionTarget}s this matcher matches,
     * and which handles injections using the given function
     *
     * @param factory The injection function
     * @return A new {@link Binding}
     */
    default Binding to(BiFunction<InjectionTarget, TypeSystem, InjectionResult> factory) {
        return new SimpleBinding(this, factory);
    }

    /**
     * Creates a {@link Binding} from this matcher which claims all {@link InjectionTarget}s this matcher matches,
     * and which handles injections by injecting the given object
     *
     * @param instance The object to inject
     * @return A new {@link Binding}
     */
    default Binding toInstance(Object instance) {
        return new SimpleBinding(this, (target, system) -> InjectionResult.of(instance));
    }

    /**
     * Creates a {@link Binding} from this matcher which claims all {@link InjectionTarget}s this matcher matches,
     * and which handles injections by injecting the result of the given supplier
     *
     * @param provider The supplier to generate the injection value
     * @return A new {@link Binding}
     */
    default Binding toProvider(Supplier<Object> provider) {
        return new SimpleBinding(this, (target, system) -> InjectionResult.of(provider.get()));
    }

    /**
     * Creates a {@link Binding} from this matcher which claims all {@link InjectionTarget}s this matcher matches,
     * and which handles injections using the given function
     *
     * @param factory The function to generate the injection value
     * @return A new {@link Binding}
     */
    default Binding toFactory(Function<InjectionTarget, Object> factory) {
        return new SimpleBinding(this, (target, system) -> InjectionResult.of(factory.apply(target)));
    }

    @Override
    default Matcher and(BiPredicate<? super InjectionTarget, ? super TypeSystem> other) {
        return (target, system) -> test(target, system) && other.test(target, system);
    }

    @Override
    default Matcher or(BiPredicate<? super InjectionTarget, ? super TypeSystem> other) {
        return (target, system) -> test(target, system) || other.test(target, system);
    }

    @Override
    default Matcher negate() {
        return (target, system) -> !test(target, system);
    }

}
