package honeyroasted.fill;

public class InjectionResult {
    private Type type;
    private Object value;

    public InjectionResult(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    public static InjectionResult of(Object value) {
        return new InjectionResult(value == null ? Type.IGNORE : Type.SET, value);
    }

    public static InjectionResult ignore() {
        return new InjectionResult(Type.IGNORE, null);
    }

    public static InjectionResult error(String message) {
        return new InjectionResult(Type.ERROR, message);
    }

    public Object value() {
        return this.value;
    }

    public Type type() {
        return this.type;
    }

    public enum Type {
        IGNORE,
        SET,
        ERROR
    }

}
