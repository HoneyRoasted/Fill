package honeyroasted.fill;

/**
 * The result of an attempted injection
 */
public class InjectionResult {
    private Type type;
    private Object value;

    /**
     * Creates a new {@link InjectionResult}
     *
     * @param type  The type of this result
     * @param value The value of this result
     */
    public InjectionResult(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Creates an {@link InjectionResult} with the given value. If the value is null, the result will be of the type
     * {@link Type#IGNORE}, otherwise it will be of the type {@link Type#SET}
     *
     * @param value The value of the result
     * @return A new {@link InjectionResult}
     */
    public static InjectionResult of(Object value) {
        return new InjectionResult(value == null ? Type.IGNORE : Type.SET, value);
    }

    /**
     * @return A new {@link InjectionResult} of the type {@link Type#IGNORE} with a null value
     */
    public static InjectionResult ignore() {
        return new InjectionResult(Type.IGNORE, null);
    }

    /**
     * Creates an {@link InjectionResult} of the type {@link Type#ERROR} with a value equal to the given error message
     *
     * @param message The error message
     * @return A new {@link InjectionResult}
     */
    public static InjectionResult error(String message) {
        return new InjectionResult(Type.ERROR, message);
    }

    /**
     * @return The value of this result (may be null)
     */
    public Object value() {
        return this.value;
    }

    /**
     * @return The type of this result
     */
    public Type type() {
        return this.type;
    }

    /**
     * Represents the different kinds of {@link InjectionResult}s
     */
    public enum Type {
        /**
         * Indicates that an injection should be ignored
         */
        IGNORE,
        /**
         * Indicates that an injection should be carried out
         */
        SET,
        /**
         * Indicates that an error occurred
         */
        ERROR
    }

}
