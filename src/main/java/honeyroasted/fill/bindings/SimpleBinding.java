package honeyroasted.fill.bindings;

import honeyroasted.fill.InjectionResult;
import honeyroasted.fill.InjectionTarget;

import java.util.function.Function;
import java.util.function.Predicate;

public class SimpleBinding implements Binding {
    private Predicate<InjectionTarget> claimTest;
    private Function<InjectionTarget, InjectionResult> result;

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
