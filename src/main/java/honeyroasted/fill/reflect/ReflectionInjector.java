package honeyroasted.fill.reflect;

import honeyroasted.fill.InjectionException;
import honeyroasted.fill.InjectionResult;
import honeyroasted.fill.InjectionTarget;
import honeyroasted.fill.Injector;
import honeyroasted.fill.bindings.Binding;
import honeyroasted.fill.bindings.BindingBuilder;
import honeyroasted.jype.system.TypeSystem;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An {@link Injector} utilizing reflection to inject into fields, methods, and constructors
 */
public class ReflectionInjector implements Injector {
    private Binding binding;
    private TypeSystem typeSystem;
    private BiPredicate<InjectionTarget, Object> dummyObjectMatcher;

    private Function<Class<?>, Collection<Field>> fieldAggregator;
    private Function<Class<?>, Collection<Method>> methodAggregator;
    private Function<Class<?>, Collection<Constructor<?>>> constructorAggregator;

    /**
     * Creates a new {@link ReflectionInjector} with the given {@link Binding}
     *
     * @param binding               The binding for this injector
     * @param system                The {@link TypeSystem} to use for type logic
     * @param dummyObjectMatcher    The predicate to use for testing if a value is over-writable
     * @param fieldAggregator       The function used to obtain injectable field candidates from a given {@link Class}
     * @param methodAggregator      The function used to obtain injectable method candidates from a given {@link Class}
     * @param constructorAggregator The function used to obtain injectable constructor candidates from a given {@link Class}
     */
    public ReflectionInjector(Binding binding, TypeSystem system, BiPredicate<InjectionTarget, Object> dummyObjectMatcher, Function<Class<?>, Collection<Field>> fieldAggregator, Function<Class<?>, Collection<Method>> methodAggregator, Function<Class<?>, Collection<Constructor<?>>> constructorAggregator) {
        this.binding = binding;
        this.typeSystem = system;
        this.dummyObjectMatcher = dummyObjectMatcher;
        this.fieldAggregator = fieldAggregator;
        this.methodAggregator = methodAggregator;
        this.constructorAggregator = constructorAggregator;
    }

    /**
     * @return A {@link ReflectionInjectorBuilder} with all the bindings of this {@link ReflectionInjector}
     */
    public ReflectionInjectorBuilder toBuilder() {
        ReflectionInjectorBuilder builder = builder();
        builder.bind(this.binding);
        return builder;
    }

    @Override
    public <T> T create(Class<T> cls) {
        List<InjectionTarget> maxTargets = null;
        Constructor max = null;

        for (Constructor constructor : this.constructorAggregator.apply(cls)) {
            List<InjectionTarget> targets = Stream.of(constructor.getParameters()).map(p -> new InjectionTarget(this.typeSystem, p)).collect(Collectors.toList());
            if (targets.stream().allMatch(t -> this.binding.claims(this.typeSystem, t))) {
                if (max == null || max.getParameterCount() < constructor.getParameterCount()) {
                    max = constructor;
                    maxTargets = targets;
                }
            }
        }

        if (max != null) {
            List<Object> parameters = new ArrayList<>();
            for (InjectionTarget target : maxTargets) {
                InjectionResult result = this.binding.handle(this.typeSystem, target);

                if (result.type() == InjectionResult.Type.SET) {
                    parameters.add(result.value());
                } else if (result.type() == InjectionResult.Type.ERROR) {
                    throw new InjectionException(String.valueOf(result.value()));
                } else {
                    throw new InjectionException("Cannot ignore constructor param");
                }
            }

            max.trySetAccessible();
            try {
                return (T) max.newInstance(parameters.toArray());
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new InjectionException("Failed to inject into constructor", e);
            }
        } else {
            throw new InjectionException("Could not find applicable injection constructor for " + cls.getName());
        }
    }

    @Override
    public void inject(Object object) {
        if (object != null) {
            this.fieldAggregator.apply(object.getClass()).stream().filter(f -> !Modifier.isStatic(f.getModifiers())).forEach(f -> tryInjection(f, object));
            this.methodAggregator.apply(object.getClass()).stream().filter(m -> !Modifier.isStatic(m.getModifiers())).forEach(m -> tryInjection(m, object));
        }
    }

    @Override
    public void injectStatic(Class<?> cls) {
        this.fieldAggregator.apply(cls).stream().filter(f -> Modifier.isStatic(f.getModifiers())).forEach(f -> tryInjection(f, null));
        this.methodAggregator.apply(cls).stream().filter(m -> Modifier.isStatic(m.getModifiers())).forEach(m -> tryInjection(m, null));
    }

    private void tryInjection(Method method, Object src) {
        if (method.getParameterCount() > 0) {
            List<InjectionTarget> targets = Stream.of(method.getParameters()).map(p -> new InjectionTarget(this.typeSystem, p)).collect(Collectors.toList());

            if (targets.stream().allMatch(t -> this.binding.claims(this.typeSystem, t))) {
                List<Object> parameters = new ArrayList<>();
                for (InjectionTarget target : targets) {
                    InjectionResult result = this.binding.handle(this.typeSystem, target);

                    if (result.type() == InjectionResult.Type.SET) {
                        parameters.add(result.value());
                    } else if (result.type() == InjectionResult.Type.ERROR) {
                        throw new InjectionException(String.valueOf(result.type()));
                    } else {
                        return;
                    }
                }

                method.trySetAccessible();
                try {
                    method.invoke(src, parameters.toArray());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new InjectionException("Failed to inject into method " + method.getName(), e);
                }
            }
        }
    }

    private void tryInjection(Field field, Object src) {
        InjectionTarget target = new InjectionTarget(this.typeSystem, field);
        if (this.binding.claims(this.typeSystem, target)) {
            field.trySetAccessible();

            try {
                Object obj = field.get(src);

                if (this.dummyObjectMatcher.test(target, obj)) {
                    InjectionResult result = this.binding.handle(this.typeSystem, target);
                    if (result.type() == InjectionResult.Type.SET) {
                        Object value = result.value();
                        if (value != null) {
                            field.set(src, value);
                        }
                    } else if (result.type() == InjectionResult.Type.ERROR) {
                        throw new InjectionException(String.valueOf(result.type()));
                    }
                }
            } catch (IllegalAccessException e) {
                throw new InjectionException("Failed to inject into field " + field.getName(), e);
            }
        }
    }

    /**
     * @return A new {@link BindingBuilder}
     */
    public static ReflectionInjectorBuilder builder() {
        return new ReflectionInjectorBuilder();
    }

}
