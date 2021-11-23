package honeyroasted.fill.bindings;

import honeyroasted.javatype.Types;
import honeyroasted.javatype.informal.TypeInformal;

import java.lang.annotation.Annotation;

public interface Matchers {

    static Matcher annotation(Class<? extends Annotation> type) {
        return target -> target.has(type);
    }

    static Matcher type(Class<?> type) {
        return target -> Types.type(type).isAssignableTo(target.type());
    }

    static Matcher type(TypeInformal type) {
        return target -> type.isAssignableTo(target.type());
    }


}
