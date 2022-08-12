package honeyroasted.fill;

/**
 * An exception that occurs during injection
 */
public class InjectionException extends RuntimeException {

    /**
     * Creates a new {@link InjectionException} with the given message
     *
     * @param message The error message
     */
    public InjectionException(String message) {
        super(message);
    }

    /**
     * Creates a new {@link InjectionException} with the given message and cause
     *
     * @param message The error message
     * @param cause   The cause of the error
     */
    public InjectionException(String message, Throwable cause) {
        super(message, cause);
    }


}
