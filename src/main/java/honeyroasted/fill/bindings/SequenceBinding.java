package honeyroasted.fill.bindings;

import honeyroasted.fill.InjectionException;
import honeyroasted.fill.InjectionResult;
import honeyroasted.fill.InjectionTarget;

import java.util.Arrays;
import java.util.List;

public class SequenceBinding implements Binding {
    private List<Binding> bindings;

    public static SequenceBinding of(Binding... bindings) {
        return new SequenceBinding(Arrays.asList(bindings));
    }

    public SequenceBinding(List<Binding> bindings) {
        this.bindings = bindings;
    }

    @Override
    public boolean claims(InjectionTarget target) {
        return this.bindings.stream().anyMatch(b -> b.claims(target));
    }

    @Override
    public InjectionResult handle(InjectionTarget target) {
        InjectionResult result = InjectionResult.ignore();

        for (Binding binding : this.bindings) {
            if (binding.claims(target)) {
                result = binding.handle(target);

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
