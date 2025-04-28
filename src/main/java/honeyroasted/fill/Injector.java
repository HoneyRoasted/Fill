package honeyroasted.fill;

/**
 * An injector capable of injecting fields, methods, and constructors
 *
 * @param <I> This type
 * @param <B> The associated {@link InjectorBuilder} type
 */
public interface Injector<I extends Injector<I, B>, B extends InjectorBuilder<B, I>> {

    /**
     * Copies this {@link Injector}'s settings into a builder of the appropriate type, and returns it.
     *
     * @return A new builder with this {@link Injector}'s settings
     */
    B toBuilder();

    /**
     * Creates a new instance of the given class by attempting to inject into a constructor,
     * then injects the instance's fields and methods
     *
     * @param cls The cls to instantiate
     * @param <T> The type of the class
     * @return A new instance of {@code T}
     */
    default <T> T createAndInject(Class<T> cls) {
        T t = create(cls);
        inject(t);
        return t;
    }

    /**
     * Creates a new instance of the given class by attempting to inject into a constructor
     *
     * @param cls The class to instantiate
     * @param <T> The type of the class
     * @return A new instance of {@code T}
     */
    <T> T create(Class<T> cls);

    /**
     * Attempts to inject the appropriate fields, and call the appropriate injection methods, on the given object
     *
     * @param object The object to inject into
     */
    void inject(Object object);

    /**
     * Attempts to inject the appropriate static fields, and call the appropriate static injection methods, on the given object
     *
     * @param cls The class to inject into
     */
    void injectStatic(Class<?> cls);
}
