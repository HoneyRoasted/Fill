package honeyroasted.fill.bindings;

import honeyroasted.fill.InjectionResult;
import honeyroasted.fill.InjectionTarget;

public class NonBinding implements Binding {
    public static final Binding INSTANCE = new NonBinding();

    @Override
    public boolean claims(InjectionTarget target) {
        return false;
    }

    @Override
    public InjectionResult handle(InjectionTarget target) {
        return InjectionResult.error("This should be unreachable");
    }

}
