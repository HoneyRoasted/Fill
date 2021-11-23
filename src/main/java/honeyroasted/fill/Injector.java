package honeyroasted.fill;

import honeyroasted.fill.bindings.Binding;
import honeyroasted.fill.bindings.Matcher;
import honeyroasted.fill.bindings.Matchers;
import honeyroasted.fill.bindings.SequenceBinding;
import honeyroasted.javatype.informal.TypeInformal;

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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Injector {
    private Binding binding;

    public Injector(Binding binding) {
        this.binding = binding;
    }

    public Builder toBuilder() {
        Builder builder = builder();
        builder.bind(this.binding);
        return builder;
    }

    public <T> T createAndInject(Class<T> cls) {
        T t = create(cls);
        inject(t);
        return t;
    }

    public <T> T create(Class<T> cls) {
        List<InjectionTarget> targets = null;
        Constructor max = null;

        for (Constructor constructor : cls.getDeclaredConstructors()) {
            if (Stream.of(constructor.getAnnotations()).anyMatch(a -> a.annotationType().isAnnotationPresent(InjectionAnnotation.class)) || constructor.getParameterCount() == 0) {
                targets = Stream.of(constructor.getParameters()).map(InjectionTarget::new).collect(Collectors.toList());
                if (targets.stream().allMatch(t -> this.binding.claims(t))) {
                    if (max == null || max.getParameterCount() < constructor.getParameterCount()) {
                        max = constructor;
                    }
                }
            }
        }

        if (max != null) {
            if (targets.stream().allMatch(t -> this.binding.claims(t))) {
                List<Object> parameters = new ArrayList<>();
                for (InjectionTarget target : targets) {
                    InjectionResult result = this.binding.handle(target);

                    if (result.type() == InjectionResult.Type.SET) {
                        parameters.add(result.value());
                    } else if (result.type() == InjectionResult.Type.ERROR) {
                        throw new InjectionException(String.valueOf(result.type()));
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
            }
        } else {
            throw new InjectionException("Could not find applicable injection constructor for " + cls.getName());
        }

        return null;
    }

    public void inject(Object object) {
        if (object != null) {
            getFields(object.getClass()).stream().filter(f -> !Modifier.isStatic(f.getModifiers())).forEach(f -> tryInjection(f, object));
            getAllMethods(object.getClass()).stream().filter(m -> !Modifier.isStatic(m.getModifiers())).forEach(m -> tryInjection(m, object));
        }
    }

    public void injectStatic(Class<?> cls) {
        getFields(cls).stream().filter(f -> Modifier.isStatic(f.getModifiers())).forEach(f -> tryInjection(f, null));
        getAllMethods(cls).stream().filter(m -> Modifier.isStatic(m.getModifiers())).forEach(m -> tryInjection(m, null));
    }

    private void tryInjection(Method method, Object src) {
        if (Stream.of(method.getAnnotations()).anyMatch(a -> a.annotationType().isAnnotationPresent(InjectionAnnotation.class))) {
            List<InjectionTarget> targets = Stream.of(method.getParameters()).map(InjectionTarget::new).collect(Collectors.toList());

            if (targets.stream().allMatch(t -> this.binding.claims(t))) {
                List<Object> parameters = new ArrayList<>();
                for (InjectionTarget target : targets) {
                    InjectionResult result = this.binding.handle(target);

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
        if (Stream.of(field.getAnnotations()).anyMatch(a -> a.annotationType().isAnnotationPresent(InjectionAnnotation.class))) {
            InjectionTarget target = new InjectionTarget(field);
            if (this.binding.claims(target)) {
                field.trySetAccessible();

                try {
                    Object obj = field.get(src);

                    if (Objects.equals(obj, getDefault(field.getType())) || obj instanceof DummyObject) {
                        InjectionResult result = this.binding.handle(target);
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
        for (Method m : methods) types.put(methodKey(m), pkgIndependent);
        for (Class<?> current = cl; current != null; current = current.getSuperclass()) {
            for (Method m : current.getDeclaredMethods()) {
                final int mod = m.getModifiers(),
                        access = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;
                if (!Modifier.isStatic(mod)) switch (mod & access) {
                    case Modifier.PUBLIC:
                        continue;
                    default:
                        Set<Package> pkg =
                                types.computeIfAbsent(methodKey(m), key -> new HashSet<>());
                        if (pkg != pkgIndependent && pkg.add(current.getPackage())) break;
                        else continue;
                    case Modifier.PROTECTED:
                        if (types.putIfAbsent(methodKey(m), pkgIndependent) != null) continue;
                        // otherwise fall-through
                    case Modifier.PRIVATE:
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<Binding> bindings = new ArrayList<>();

        public Builder append(Builder other) {
            this.bindings.addAll(other.bindings);
            return this;
        }

        public Builder bind(Binding binding) {
            this.bindings.add(binding);
            return this;
        }

        public Builder bind(Binding... bindings) {
            this.bindings.addAll(Arrays.asList(bindings));
            return this;
        }

        public MatcherBuilder bind(Matcher matcher) {
            return new MatcherBuilder(matcher, this);
        }

        public MatcherBuilder bind(Class<?> type) {
            return new MatcherBuilder(Matchers.type(type), this);
        }

        public MatcherBuilder bind(TypeInformal type) {
            return new MatcherBuilder(Matchers.type(type), this);
        }

        public MatcherBuilder bind(Class<?> type, Class<? extends Annotation> annotation) {
            return new MatcherBuilder(Matchers.type(type).and(Matchers.annotation(annotation)), this);
        }

        public MatcherBuilder bind(TypeInformal type, Class<? extends Annotation> annotation) {
            return new MatcherBuilder(Matchers.type(type).and(Matchers.annotation(annotation)), this);
        }

        public Injector build() {
            return new Injector(new SequenceBinding(this.bindings));
        }

    }

    public static class MatcherBuilder {
        private Matcher matcher;
        private Builder builder;

        public MatcherBuilder(Matcher matcher, Builder builder) {
            this.matcher = matcher;
            this.builder = builder;
        }

        public Builder toInstance(Object instance) {
            return this.builder.bind(this.matcher.toInstance(instance));
        }

        public Builder toProvider(Supplier<Object> provider) {
            return this.builder.bind(this.matcher.toProvider(provider));
        }

        public Builder toFactory(Function<InjectionTarget, Object> factory) {
            return this.builder.bind(this.matcher.toFactory(factory));
        }

        public Builder to(Function<InjectionTarget, InjectionResult> factory) {
            return this.builder.bind(this.matcher.to(factory));
        }

    }

}
