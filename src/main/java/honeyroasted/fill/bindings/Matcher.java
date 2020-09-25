package honeyroasted.fill.bindings;

import honeyroasted.fill.InjectionResult;
import honeyroasted.fill.InjectionTarget;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Matcher extends Predicate<InjectionTarget> {

    default Binding to(Function<InjectionTarget, InjectionResult> factory) {
        return new SimpleBinding(this, factory);
    }

    default Binding toInstance(Object instance) {
        return new SimpleBinding(this, target -> InjectionResult.of(instance));
    }

    default Binding toProvider(Supplier<Object> provider) {
        return new SimpleBinding(this, target -> InjectionResult.of(provider.get()));
    }

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
