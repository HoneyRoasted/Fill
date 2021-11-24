package honeyroasted.fill.bindings;

import honeyroasted.fill.InjectionResult;
import honeyroasted.fill.InjectionTarget;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Represents a predicate which can match {@link InjectionTarget}s
 */
public interface Matcher extends Predicate<InjectionTarget> {

    /**
     * Creates a {@link Binding} from this matcher which claims all {@link InjectionTarget}s this matcher matches,
     * and which handles injections using the given function
     *
     * @param factory The injection function
     * @return A new {@link Binding}
     */
    default Binding to(Function<InjectionTarget, InjectionResult> factory) {
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
        return new SimpleBinding(this, target -> InjectionResult.of(instance));
    }

    /**
     * Creates a {@link Binding} from this matcher which claims all {@link InjectionTarget}s this matcher matches,
     * and which handles injections by injecting the result of the given supplier
     *
     * @param provider The supplier to generate the injection value
     * @return A new {@link Binding}
     */
    default Binding toProvider(Supplier<Object> provider) {
        return new SimpleBinding(this, target -> InjectionResult.of(provider.get()));
    }

    /**
     * Creates a {@link Binding} from this matcher which claims all {@link InjectionTarget}s this matcher matches,
     * and which handles injections using the given function
     *
     * @param factory The function to generate the injection value
     * @return A new {@link Binding}
     */
    default Binding toFactory(Function<InjectionTarget, Object> factory) {
        return new SimpleBinding(this, target -> InjectionResult.of(factory.apply(target)));
    }

    @Override
    default Matcher and(Predicate<? super InjectionTarget> other) {
        return t -> test(t) && other.test(t);
    }

    @Override
    default Matcher negate() {
        return t -> !test(t);
    }

    @Override
    default Matcher or(Predicate<? super InjectionTarget> other) {
        return t -> test(t) || other.test(t);
    }
}
