package honeyroasted.fill;

import honeyroasted.fill.bindings.Binding;
import honeyroasted.fill.bindings.Matcher;
import honeyroasted.fill.bindings.Matchers;
import honeyroasted.fill.bindings.SequenceBinding;
import honeyroasted.jype.TypeConcrete;
import honeyroasted.jype.system.TypeSystem;
import honeyroasted.jype.system.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An injector capable of injecting fields, methods, and constructors
 */
public class Injector {
    private Binding binding;
    private TypeSystem typeSystem;
    private Injector.Tri<TypeSystem, InjectionTarget, Object> dummyObjectMatcher;

    /**
     * Creates a new {@link Injector} with the given {@link Binding}
     *
     * @param binding            The binding for this injector
     * @param system             The {@link TypeSystem} to use for type logic
     * @param dummyObjectMatcher The predicate to use for testing if a value is over-writable
     */
    public Injector(Binding binding, TypeSystem system, Injector.Tri<TypeSystem, InjectionTarget, Object> dummyObjectMatcher) {
        this.binding = binding;
        this.typeSystem = system;
        this.dummyObjectMatcher = dummyObjectMatcher;
    }

    /**
     * @return A {@link Builder} with all the bindings of this {@link Injector}
     */
    public Builder toBuilder() {
        Builder builder = builder();
        builder.bind(this.binding);
        return builder;
    }


    /**
     * Creates a new instance of the given class by attempting to inject into a constructor,
     * then injects the instance's fields and methods
     *
     * @param cls The cls to instantiate
     * @param <T> The type of the class
     * @return A new instance of {@code T}
     */
    public <T> T createAndInject(Class<T> cls) {
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
    public <T> T create(Class<T> cls) {
        List<InjectionTarget> maxTargets = null;
        Constructor max = null;

        for (Constructor constructor : cls.getDeclaredConstructors()) {
            if (Stream.of(constructor.getAnnotations()).anyMatch(a -> a.annotationType().isAnnotationPresent(InjectionAnnotation.class)) || Stream.of(constructor.getParameters()).allMatch(p ->
                    Stream.of(p.getAnnotations()).anyMatch(a -> a.annotationType().isAnnotationPresent(InjectionAnnotation.class)))) {
                List<InjectionTarget> targets = Stream.of(constructor.getParameters()).map(p -> new InjectionTarget(this.typeSystem, p)).collect(Collectors.toList());
                if (targets.stream().allMatch(t -> this.binding.claims(this.typeSystem, t))) {
                    if (max == null || max.getParameterCount() < constructor.getParameterCount()) {
                        max = constructor;
                        maxTargets = targets;
                    }
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

    /**
     * Attempts to inject the appropriate fields, and call the appropriate injection methods, on the given object
     *
     * @param object The object to inject into
     */
    public void inject(Object object) {
        if (object != null) {
            getFields(object.getClass()).stream().filter(f -> !Modifier.isStatic(f.getModifiers())).forEach(f -> tryInjection(f, object));
            getAllMethods(object.getClass()).stream().filter(m -> !Modifier.isStatic(m.getModifiers())).forEach(m -> tryInjection(m, object));
        }
    }

    /**
     * Attempts to inject the appropriate static fields, and call the appropriate injection methods, on the given object
     *
     * @param cls The class to inject into
     */
    public void injectStatic(Class<?> cls) {
        getFields(cls).stream().filter(f -> Modifier.isStatic(f.getModifiers())).forEach(f -> tryInjection(f, null));
        getAllMethods(cls).stream().filter(m -> Modifier.isStatic(m.getModifiers())).forEach(m -> tryInjection(m, null));
    }

    private void tryInjection(Method method, Object src) {
        if (method.getParameterCount() > 0) {
            if (Stream.of(method.getAnnotations()).anyMatch(a -> a.annotationType().isAnnotationPresent(InjectionAnnotation.class)) ||
                    Stream.of(method.getParameters()).allMatch(p -> Stream.of(p.getAnnotations()).anyMatch(a -> a.annotationType().isAnnotationPresent(InjectionAnnotation.class)))) {
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
    }

    private void tryInjection(Field field, Object src) {
        if (Stream.of(field.getAnnotations()).anyMatch(a -> a.annotationType().isAnnotationPresent(InjectionAnnotation.class))) {
            InjectionTarget target = new InjectionTarget(this.typeSystem, field);
            if (this.binding.claims(this.typeSystem, target)) {
                field.trySetAccessible();

                try {
                    Object obj = field.get(src);

                    if (this.dummyObjectMatcher.test(this.typeSystem, target, obj)) {
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

    private static List<Field> getFields(Class<?> cls) {
        List<Field> fields = new ArrayList<>();
        getFields(cls, fields);
        return fields;
    }

    private static void getFields(Class<?> cls, List<Field> fields) {
        if (cls != null) {
            Collections.addAll(fields, cls.getDeclaredFields());
            getFields(cls.getSuperclass(), fields);
        }
    }

    private static Set<Method> getAllMethods(Class<?> cl) {
        Set<Method> methods = new LinkedHashSet<>();
        Collections.addAll(methods, cl.getMethods());
        Map<Object, Set<Package>> types = new HashMap<>();
        final Set<Package> pkgIndependent = Collections.emptySet();
        for (Method m : methods) {
            types.put(methodKey(m), pkgIndependent);
        }
        for (Class<?> current = cl; current != null; current = current.getSuperclass()) {
            for (Method m : current.getDeclaredMethods()) {
                final int mod = m.getModifiers(),
                        access = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;
                if (!Modifier.isStatic(mod)) {
                    switch (mod & access) {
                        case Modifier.PUBLIC:
                            continue;
                        default:
                            Set<Package> pkg =
                                    types.computeIfAbsent(methodKey(m), key -> new HashSet<>());
                            if (pkg != pkgIndependent && pkg.add(current.getPackage())) {
                                break;
                            } else {
                                continue;
                            }
                        case Modifier.PROTECTED:
                            if (types.putIfAbsent(methodKey(m), pkgIndependent) != null) {
                                continue;
                            }
                            // otherwise fall-through
                        case Modifier.PRIVATE:
                    }
                }
                methods.add(m);
            }
        }
        return methods;
    }

    private static Object methodKey(Method m) {
        return Arrays.asList(m.getName(),
                MethodType.methodType(m.getReturnType(), m.getParameterTypes()));
    }

    /**
     * @return A new {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A class for building {@link Injector}s
     */
    public static class Builder {
        private List<Binding> bindings = new ArrayList<>();
        private TypeSystem system = TypeSystem.GLOBAL;
        private Injector.Tri<TypeSystem, InjectionTarget, Object> dummyObjectMatcher = (ts, it, obj) ->
                Objects.equals(obj, getDefault(it.rawType())) || obj instanceof DummyObject;


        /**
         * Appends all the bindings from the given builder to this builder
         *
         * @param other The other builder
         * @return This, for method chaining
         */
        public Builder append(Builder other) {
            this.bindings.addAll(other.bindings);
            return this;
        }

        /**
         * Sets the {@link TypeSystem} to use for the resulting injector
         *
         * @param system The type system
         * @return This, for method chaining
         */
        public Builder typeSystem(TypeSystem system) {
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
        public Builder dummyObjectMatcher(Injector.Tri<TypeSystem, InjectionTarget, Object> matcher) {
            this.dummyObjectMatcher = matcher;
            return this;
        }

        /**
         * Adds a binding to this builder
         *
         * @param binding The binding to add
         * @return This, for method chaining
         */
        public Builder bind(Binding binding) {
            this.bindings.add(binding);
            return this;
        }

        /**
         * Adds multiple bindings to this builder
         *
         * @param bindings The bindings to add
         * @return This, for method chaining
         */
        public Builder bind(Binding... bindings) {
            this.bindings.addAll(Arrays.asList(bindings));
            return this;
        }

        /**
         * Creates a new {@link BindingBuilder} with the given matcher and returns it, the matcher builder may then be
         * used to bind to a value and continue back into this builder
         *
         * @param matcher The matcher to use
         * @return A new {@link BindingBuilder} referencing this {@link Builder}
         */
        public BindingBuilder bind(Matcher matcher) {
            return new BindingBuilder(matcher, this);
        }

        /**
         * Creates a new {@link BindingBuilder} with a matcher that matches the given type, the matcher builder may then be
         * used to bind to a value and continue back into this builder
         *
         * @param type The type to use
         * @return A new {@link BindingBuilder} referencing this {@link Builder}
         */
        public BindingBuilder bind(Class<?> type) {
            return new BindingBuilder(Matchers.type(type), this);
        }

        /**
         * Creates a new {@link BindingBuilder} with a matcher that matches the given type, the matcher builder may then be
         * used to bind to a value and continue back into this builder
         *
         * @param type The type to use
         * @return A new {@link BindingBuilder} referencing this {@link Builder}
         */
        public BindingBuilder bind(TypeConcrete type) {
            return new BindingBuilder(Matchers.type(type), this);
        }

        /**
         * Creates a new {@link BindingBuilder} with a matcher that matches the given type, the matcher builder may then be
         * used to bind to a value and continue back into this builder
         *
         * @param token The type to use
         * @return A new {@link BindingBuilder} referencing this {@link Builder}
         */
        public BindingBuilder bind(TypeToken<?> token) {
            return new BindingBuilder(Matchers.type(this.system.of(token).get()), this);
        }

        /**
         * Creates a new {@link BindingBuilder} with a matcher that matches the given type and the given annotation,
         * the matcher builder may then be used to bind to a value and continue back into this builder
         *
         * @param type       The type to use
         * @param annotation The annotation to use
         * @return A new {@link BindingBuilder} referencing this {@link Builder}
         */
        public BindingBuilder bind(Class<?> type, Class<? extends Annotation> annotation) {
            return new BindingBuilder(Matchers.type(type).and(Matchers.annotation(annotation)), this);
        }

        /**
         * Creates a new {@link BindingBuilder} with a matcher that matches the given type and the given annotation,
         * the matcher builder may then be used to bind to a value and continue back into this builder
         *
         * @param type       The type to use
         * @param annotation The annotation to use
         * @return A new {@link BindingBuilder} referencing this {@link Builder}
         */
        public BindingBuilder bind(TypeConcrete type, Class<? extends Annotation> annotation) {
            return new BindingBuilder(Matchers.type(type).and(Matchers.annotation(annotation)), this);
        }

        /**
         * @return A new {@link Injector} with the bindings from this {@link Builder}
         */
        public Injector build() {
            return new Injector(new SequenceBinding(this.bindings), this.system, this.dummyObjectMatcher);
        }

    }

    /**
     * An intermediate builder that takes a {@link Matcher} and {@link Builder} and constructs a {@link Binding} and
     * adds it to the {@link Builder}
     */
    public static class BindingBuilder {
        private Matcher matcher;
        private Builder builder;

        /**
         * Creates a new {@link BindingBuilder}
         *
         * @param matcher The matcher to build from
         * @param builder The parent builder
         */
        public BindingBuilder(Matcher matcher, Builder builder) {
            this.matcher = matcher;
            this.builder = builder;
        }

        /**
         * Creates a binding with the given instance and adds it to the parent builder
         *
         * @param instance The instance to use
         * @return The parent builder, for method chaining
         */
        public Builder toInstance(Object instance) {
            return this.builder.bind(this.matcher.toInstance(instance));
        }

        /**
         * Creates a binding with the given provider and adds it to the parent builder
         *
         * @param provider The provider to use
         * @return The parent builder, for method chaining
         */
        public Builder toProvider(Supplier<Object> provider) {
            return this.builder.bind(this.matcher.toProvider(provider));
        }

        /**
         * Creates a binding with the given factory and adds it to the parent builder
         *
         * @param factory The instance to use
         * @return The parent builder, for method chaining
         */
        public Builder toFactory(Function<InjectionTarget, Object> factory) {
            return this.builder.bind(this.matcher.toFactory(factory));
        }

        /**
         * Creates a binding with the given factory and adds it to the parent builder
         *
         * @param factory The instance to use
         * @return The parent builder, for method chaining
         */
        public Builder to(BiFunction<InjectionTarget, TypeSystem, InjectionResult> factory) {
            return this.builder.bind(this.matcher.to(factory));
        }

    }

    /**
     * Small functional interface for a predicate with three arguments
     *
     * @param <A> Predicate argument type
     * @param <B> Predicate argument type
     * @param <C> Predicate argument type
     */
    @FunctionalInterface
    public interface Tri<A, B, C> {

        /**
         * True or false, based on the implementation
         *
         * @param a Argument 1
         * @param b Argument 2
         * @param c Argument 3
         * @return A boolean value
         */
        boolean test(A a, B b, C c);

    }

}
