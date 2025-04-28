package honeyroasted.fill.bindings;

import honeyroasted.fill.InjectionException;
import honeyroasted.fill.InjectionResult;
import honeyroasted.fill.InjectionTarget;
import honeyroasted.jype.system.JTypeSystem;

import java.util.Arrays;
import java.util.List;

/**
 * An implementation of {@link Binding} which contains a list of other bindings and applies the first one which
 * claims a given target
 */
public class SequenceBinding implements Binding {
    private List<Binding> bindings;

    /**
     * Creates a new {@link SequenceBinding} with the given bindings
     *
     * @param bindings The bindings
     * @return A new {@link SequenceBinding}
     */
    public static SequenceBinding of(Binding... bindings) {
        return new SequenceBinding(Arrays.asList(bindings));
    }

    /**
     * Creates a new {@link SequenceBinding}
     *
     * @param bindings The bindings
     */
    public SequenceBinding(List<Binding> bindings) {
        this.bindings = bindings;
    }

    /**
     * Returns the children {@link Binding}s for this Sequence binding.
     * @return The children {@link Binding}s
     */
    public List<Binding> bindings() {
        return this.bindings;
    }

    @Override
    public boolean claims(JTypeSystem system, InjectionTarget target) {
        return this.bindings.stream().anyMatch(b -> b.claims(system, target));
    }

    @Override
    public InjectionResult handle(JTypeSystem system, InjectionTarget target) {
        InjectionResult result = InjectionResult.ignore();

        for (Binding binding : this.bindings) {
            if (binding.claims(system, target)) {
                result = binding.handle(system, target);

                if (result.type() == InjectionResult.Type.SET) {
                    return result;
                } else if (result.type() == InjectionResult.Type.ERROR) {
                    throw new InjectionException(String.valueOf(result.value()));
                }
            }
        }

        return result;
    }
}
