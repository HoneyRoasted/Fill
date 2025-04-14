package honeyroasted.fill.bindings;

import honeyroasted.fill.InjectionResult;
import honeyroasted.fill.InjectionTarget;
import honeyroasted.fill.InjectorBuilder;
import honeyroasted.fill.reflect.ReflectionInjectorBuilder;
import honeyroasted.jype.system.JTypeSystem;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An intermediate builder that takes a {@link Matcher} and {@link ReflectionInjectorBuilder} and constructs a {@link Binding} and
 * adds it to the {@link ReflectionInjectorBuilder}
 */
public class BindingBuilder<T extends InjectorBuilder<T>> {
    private Matcher matcher;
    private T builder;

    /**
     * Creates a new {@link BindingBuilder}
     *
     * @param matcher The matcher to build from
     * @param builder The parent builder
     */
    public BindingBuilder(Matcher matcher, T builder) {
        this.matcher = matcher;
        this.builder = builder;
    }

    /**
     * Creates a binding with the given instance and adds it to the parent builder
     *
     * @param instance The instance to use
     * @return The parent builder, for method chaining
     */
    public T toInstance(Object instance) {
        return this.builder.bind(this.matcher.toInstance(instance));
    }

    /**
     * Creates a binding with the given provider and adds it to the parent builder
     *
     * @param provider The provider to use
     * @return The parent builder, for method chaining
     */
    public T toProvider(Supplier<Object> provider) {
        return this.builder.bind(this.matcher.toProvider(provider));
    }

    /**
     * Creates a binding with the given factory and adds it to the parent builder
     *
     * @param factory The instance to use
     * @return The parent builder, for method chaining
     */
    public T toFactory(Function<InjectionTarget, Object> factory) {
        return this.builder.bind(this.matcher.toFactory(factory));
    }

    /**
     * Creates a binding with the given factory and adds it to the parent builder
     *
     * @param factory The instance to use
     * @return The parent builder, for method chaining
     */
    public T to(BiFunction<InjectionTarget, JTypeSystem, InjectionResult> factory) {
        return this.builder.bind(this.matcher.to(factory));
    }
}
