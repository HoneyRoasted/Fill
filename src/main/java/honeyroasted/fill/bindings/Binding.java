package honeyroasted.fill.bindings;

import honeyroasted.fill.InjectionResult;
import honeyroasted.fill.InjectionTarget;

public interface Binding {

    boolean claims(InjectionTarget target);

    InjectionResult handle(InjectionTarget target);

}
