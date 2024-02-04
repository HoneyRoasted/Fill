package honeyroasted.fill.reflect;

import honeyroasted.fill.DummyObject;
import honeyroasted.fill.InjectionTarget;
import honeyroasted.fill.Injector;
import honeyroasted.fill.bindings.Binding;
import honeyroasted.fill.InjectorBuilder;
import honeyroasted.fill.bindings.SequenceBinding;
import honeyroasted.jype.system.TypeSystem;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * An {@link InjectorBuilder} for building {@link ReflectionInjector}s
 */
public class ReflectionInjectorBuilder implements InjectorBuilder<ReflectionInjectorBuilder> {
    private List<Binding> bindings = new ArrayList<>();
    private TypeSystem system = TypeSystem.RUNTIME;
    private BiPredicate<InjectionTarget, Object> dummyObjectMatcher = (it, obj) ->
            Objects.equals(obj, getDefault(it.rawType())) || obj instanceof DummyObject;

    private Function<Class<?>, Collection<Field>> fieldAggregator = Aggregators.ANNOTATED_FIELDS;
    private Function<Class<?>, Collection<Method>> methodAggregator = Aggregators.ANNOTATED_METHODS;
    private Function<Class<?>, Collection<Constructor<?>>> constructorAggregator = Aggregators.ANNOTATED_CONSTRUCTORS;


    /**
     * Appends all the bindings from the given builder to this builder
     *
     * @param other The other builder
     * @return This, for method chaining
     */
    public ReflectionInjectorBuilder append(ReflectionInjectorBuilder other) {
        this.bindings.addAll(other.bindings);
        return this;
    }

    /**
     * Sets the {@link TypeSystem} to use for the resulting injector
     *
     * @param system The type system
     * @return This, for method chaining
     */
    public ReflectionInjectorBuilder typeSystem(TypeSystem system) {
        this.system = system;
        return this;
    }

    /**
     * Sets the dummy objet matcher for this builder. By default, it is set to return true for any objet
     * which is equal to the default field type for the given {@link InjectionTarget} or is an instance of
     * {@link DummyObject}.
     *
     * @param matcher The new dummy objet matcher
     * @return This, for method chaining
     */
    public ReflectionInjectorBuilder dummyObjectMatcher(BiPredicate<InjectionTarget, Object> matcher) {
        this.dummyObjectMatcher = matcher;
        return this;
    }

    /**
     * Sets the field aggregator for this builder. By default, it is set to return all fields which are annotated
     * with an injection annotation.
     *
     * @param fieldAggregator The new field aggregator
     * @return This, for method chaining
     */
    public ReflectionInjectorBuilder fieldAggregator(Function<Class<?>, Collection<Field>> fieldAggregator) {
        this.fieldAggregator = fieldAggregator;
        return this;
    }

    /**
     * Sets the field method for this builder. By default, it is set to return all methods which are annotated
     * with an injection annotation.
     *
     * @param methodAggregator The new method aggregator
     * @return This, for method chaining
     */
    public ReflectionInjectorBuilder methodAggregator(Function<Class<?>, Collection<Method>> methodAggregator) {
        this.methodAggregator = methodAggregator;
        return this;
    }

    /**
     * Sets the constructor aggregator for this builder. By default, it is set to return all constructors which are
     * annotated with an injection annotation.
     *
     * @param constructorAggregator The new constructor aggregator
     * @return This, for method chaining
     */
    public ReflectionInjectorBuilder constructorAggregator(Function<Class<?>, Collection<Constructor<?>>> constructorAggregator) {
        this.constructorAggregator = constructorAggregator;
        return this;
    }

    /**
     * Adds one or more bindings to this builder
     *
     * @param bindings The bindings to add
     * @return This, for method chaining
     */
    @Override
    public ReflectionInjectorBuilder bind(Binding... bindings) {
        Collections.addAll(this.bindings, bindings);
        return this;
    }

    /**
     * @return A new {@link ReflectionInjector} with the bindings from this {@link ReflectionInjectorBuilder}
     */
    public Injector build() {
        return new ReflectionInjector(new SequenceBinding(this.bindings), this.system, this.dummyObjectMatcher, this.fieldAggregator, this.methodAggregator, this.constructorAggregator);
    }

    private static Object getDefault(Class<?> cls) {
        if (cls.isPrimitive()) {
            if (cls == boolean.class) {
                return false;
            } else if (cls == byte.class) {
                return (byte) 0;
            } else if (cls == short.class) {
                return (short) 0;
            } else if (cls == char.class) {
                return (char) 0;
            } else if (cls == int.class) {
                return (int) 0;
            } else if (cls == long.class) {
                return (long) 0;
            } else if (cls == float.class) {
                return (float) 0;
            } else if (cls == double.class) {
                return (double) 0;
            }
        }
        return null;
    }

}
