package honeyroasted.fill.bindings;

import honeyroasted.fill.InjectionResult;
import honeyroasted.fill.InjectionTarget;

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
    boolean claims(InjectionTarget target);

    /**
     * Handles the given {@link InjectionTarget}
     *
     * @param target The injection target
     * @return The {@link InjectionResult} of handling the given target
     */
    InjectionResult handle(InjectionTarget target);

}
