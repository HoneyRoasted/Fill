package honeyroasted.fill.bindings;

import honeyroasted.fill.InjectionResult;
import honeyroasted.fill.InjectionTarget;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An implementation of {@link Binding} which claims bindings that pass a {@link Predicate} and handles bindings with
 * a {@link Function}
 */
public class SimpleBinding implements Binding {
    private Predicate<InjectionTarget> claimTest;
    private Function<InjectionTarget, InjectionResult> result;

    /**
     * Creates a new {@link SimpleBinding} that claims {@link InjectionTarget}s with the given predicate and handles bindings
     * with the given function
     *
     * @param claimTest The claim predicate
     * @param result The binding handler
     */
    public SimpleBinding(Predicate<InjectionTarget> claimTest, Function<InjectionTarget, InjectionResult> result) {
        this.claimTest = claimTest;
        this.result = result;
    }

    @Override
    public boolean claims(InjectionTarget target) {
        return this.claimTest.test(target);
    }

    @Override
    public InjectionResult handle(InjectionTarget target) {
        return this.result.apply(target);
    }
}
