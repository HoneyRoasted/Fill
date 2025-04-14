package honeyroasted.fill.bindings;

import honeyroasted.fill.InjectionResult;
import honeyroasted.fill.InjectionTarget;
import honeyroasted.jype.system.JTypeSystem;

/**
 * Represents an injection binding
 */
public interface Binding {

    /**
     * Test whether this {@link Binding} handles the given {@link InjectionTarget}
     *
     * @param target The injection target
     * @param system The {@link JTypeSystem} to use for type logic
     * @return True if this binding handles the given target
     */
    boolean claims(JTypeSystem system, InjectionTarget target);

    /**
     * Handles the given {@link InjectionTarget}
     *
     * @param target The injection target
     * @param system The {@link JTypeSystem} to use for type logic
     * @return The {@link InjectionResult} of handling the given target
     */
    InjectionResult handle(JTypeSystem system, InjectionTarget target);

}
