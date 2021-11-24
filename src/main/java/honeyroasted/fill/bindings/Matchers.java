package honeyroasted.fill.bindings;

import honeyroasted.javatype.Types;
import honeyroasted.javatype.informal.TypeInformal;

import java.lang.annotation.Annotation;

/**
 * Utility interface for constructing common {@link Matcher}s
 */
public interface Matchers {

    /**
     * Creates a {@link Matcher} that matches {@link honeyroasted.fill.InjectionTarget}s with the given annotation
     *
     * @param type The annotation type to match
     * @return A new {@link Matcher}
     */
    static Matcher annotation(Class<? extends Annotation> type) {
        return target -> target.has(type);
    }

    /**
     * Creates a {@link Matcher} that matches {@link honeyroasted.fill.InjectionTarget}s with the given type
     *
     * @param type The type to match
     * @return A new {@link Matcher}
     */
    static Matcher type(Class<?> type) {
        return target -> Types.type(type).isAssignableTo(target.type());
    }

    /**
     * Creates a {@link Matcher} that matches {@link honeyroasted.fill.InjectionTarget}s with the given type
     *
     * @param type The type to match
     * @return A new {@link Matcher}
     */
    static Matcher type(TypeInformal type) {
        return target -> type.isAssignableTo(target.type());
    }


}
