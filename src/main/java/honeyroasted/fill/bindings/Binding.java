package honeyroasted.fill.bindings;

import honeyroasted.fill.InjectionResult;
import honeyroasted.fill.InjectionTarget;
import honeyroasted.jype.system.TypeSystem;

/**
 * Represents an injection binding
 */
public interface Binding {

    /**
     * Test whether this {@link Binding} handles the given {@link InjectionTarget}
     *
     * @param target The injection target
     * @return True if this binding handles the given target
     */
    boolean claims(TypeSystem system, InjectionTarget target);

    /**
     * Handles the given {@link InjectionTarget}
     *
     * @param target The injection target
     * @return The {@link InjectionResult} of handling the given target
     */
    InjectionResult handle(TypeSystem system, InjectionTarget target);

}
