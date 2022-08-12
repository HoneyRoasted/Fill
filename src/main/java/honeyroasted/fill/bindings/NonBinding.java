package honeyroasted.fill.bindings;

import honeyroasted.fill.InjectionResult;
import honeyroasted.fill.InjectionTarget;
import honeyroasted.jype.system.TypeSystem;

/**
 * A {@link Binding} implementation that claims no targets
 */
public class NonBinding implements Binding {
    /**
     * An instance of {@link NonBinding}
     */
    public static final Binding INSTANCE = new NonBinding();

    @Override
    public boolean claims(TypeSystem system, InjectionTarget target) {
        return false;
    }

    @Override
    public InjectionResult handle(TypeSystem system, InjectionTarget target) {
        return InjectionResult.error("This should be unreachable");
    }
}
