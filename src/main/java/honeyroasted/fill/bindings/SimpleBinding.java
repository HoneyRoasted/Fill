package honeyroasted.fill.bindings;

import honeyroasted.fill.InjectionResult;
import honeyroasted.fill.InjectionTarget;
import honeyroasted.jype.system.JTypeSystem;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An implementation of {@link Binding} which claims bindings that pass a {@link Predicate} and handles bindings with
 * a {@link Function}
 */
public class SimpleBinding implements Binding {
    private BiPredicate<InjectionTarget, JTypeSystem> claimTest;
    private BiFunction<InjectionTarget, JTypeSystem, InjectionResult> result;

    /**
     * Creates a new {@link SimpleBinding} that claims {@link InjectionTarget}s with the given predicate and handles bindings
     * with the given function
     *
     * @param claimTest The claim predicate
     * @param result    The binding handler
     */
    public SimpleBinding(BiPredicate<InjectionTarget, JTypeSystem> claimTest, BiFunction<InjectionTarget, JTypeSystem, InjectionResult> result) {
        this.claimTest = claimTest;
        this.result = result;
    }

    @Override
    public boolean claims(JTypeSystem system, InjectionTarget target) {
        return this.claimTest.test(target, system);
    }

    @Override
    public InjectionResult handle(JTypeSystem system, InjectionTarget target) {
        return this.result.apply(target, system);
    }
}
