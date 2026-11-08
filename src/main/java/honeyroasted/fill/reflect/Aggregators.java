package honeyroasted.fill.reflect;

import honeyroasted.fill.InjectionAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A collection of some basic method, field, and constructor aggregators for use in a {@link ReflectionInjector}
 */
public interface Aggregators {
    /**
     * An aggregator that produces all fields of a given class that are annotated with an injection annotation. This
     * includes static fields, fields from superclasses, and private, protected, or package-protected fields.
     */
    Function<Class<?>, Collection<Field>> ANNOTATED_FIELDS = cls -> {
        Set<Field> fields = new LinkedHashSet<>();
        getFields(cls, fields, false);
        return fields;
    };

    /**
     * An aggregator that produces all fields of a given class. This includes static fields, fields from superclasses,
     * and private, protected, or package-protected fields.
     */
    Function<Class<?>, Collection<Field>> ALL_FIELDS = cls -> {
        Set<Field> fields = new LinkedHashSet<>();
        getFields(cls, fields, true);
        return fields;
    };

    /**
     * An aggregator that produces all methods of a given class that are annotated with an injection annotation
     * (or that have every parameter annotated with an injection annotation). This includes static methods, methods
     * from superclasses, and private, protected, or package-protected methods. This DOES NOT include non-annotated
     * methods with no parameters.
     */
    Function<Class<?>, Collection<Method>> ANNOTATED_METHODS = cls -> {
        Set<Method> methods = new LinkedHashSet<>();
        getMethods(cls, methods, true, false, new HashSet<>());
        return methods;
    };

    /**
     * An aggregator that produces all methods of a given class. This includes static methods, methods from superclasses,
     * and private, protected, or package-protected methods. Methods declared in {@link Object} are excluded.
     */
    Function<Class<?>, Collection<Method>> ALL_METHODS = cls -> {
        Set<Method> methods = new LinkedHashSet<>();
        getMethods(cls, methods, true, true, new HashSet<>());
        return methods;
    };

    /**
     * An aggregator that produces all constructors of a given class that are annotated with an injection annotation
     * (or that have every parameter annotated with an injection annotation). This includes non-annotated constructors
     * with no parameters.
     */
    Function<Class<?>, Collection<Constructor<?>>> ANNOTATED_CONSTRUCTORS = cls -> {
        Set<Constructor<?>> constructors = new LinkedHashSet<>();
        getConstructors(cls, constructors, false);
        return constructors;
    };

    /**
     * An aggregator that produces all constructors of a given class.
     */
    Function<Class<?>, Collection<Constructor<?>>> ALL_CONSTRUCTORS = cls -> {
        Set<Constructor<?>> constructors = new LinkedHashSet<>();
        getConstructors(cls, constructors, true);
        return constructors;
    };

    private static boolean hasInjectionAnnotation(AnnotatedElement elm) {
        for (Annotation anot : elm.getAnnotations()) {
            if (anot.annotationType().isAnnotationPresent(InjectionAnnotation.class)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isCandidate(Method method, boolean includeAll) {
        return includeAll || hasInjectionAnnotation(method) || (method.getParameterCount() > 0 && Stream.of(method.getParameters()).allMatch(Aggregators::hasInjectionAnnotation));
    }

    private static void getFields(Class<?> cls, Collection<Field> fields, boolean includeAll) {
        if (cls == null) {
            return;
        }

        for (Field field : cls.getDeclaredFields()) {
            if (!field.isSynthetic() && (includeAll || hasInjectionAnnotation(field))) {
                fields.add(field);
            }
        }

        getFields(cls.getSuperclass(), fields, includeAll);
    }

    private static void getConstructors(Class<?> cls, Collection<Constructor<?>> constructors, boolean includeAll) {
        if (cls == null) {
            return;
        }

        for (Constructor<?> cons : cls.getDeclaredConstructors()) {
            if (!cons.isSynthetic() && (includeAll || hasInjectionAnnotation(cons) || Stream.of(cons.getParameters()).allMatch(Aggregators::hasInjectionAnnotation))) {
                constructors.add(cons);
            }
        }
    }

    private static void getMethods(Class<?> cls, Collection<Method> methods, boolean first, boolean includeAll, Set<Object> seen) {
        if (cls == null || cls == Object.class) {
            return;
        }

        if (first) {
            for (Method method : cls.getMethods()) {
                if (!method.isSynthetic() && method.getDeclaringClass() != Object.class && !Modifier.isStatic(method.getModifiers())) {
                    seen.add(methodKey(method));
                    if (isCandidate(method, includeAll)) {
                        //Add all public methods
                        methods.add(method);
                    }
                }
            }
        }

        for (Method m : cls.getDeclaredMethods()) {
            int mods = m.getModifiers();
            if (m.isSynthetic()) {
                continue;
            }

            if (Modifier.isStatic(mods) || Modifier.isPrivate(mods)) {
                if (isCandidate(m, includeAll)) {
                    methods.add(m);
                }
            } else if (Modifier.isProtected(mods) || !Modifier.isPublic(mods)) { //Method is protected or package-protected
                Object key = methodKey(m);
                if (!seen.contains(key)) {
                    seen.add(key);
                    if (isCandidate(m, includeAll)) {
                        methods.add(m);
                    }
                }
            }
        }

        getMethods(cls.getSuperclass(), methods, false, includeAll, seen);
    }

    private static Object methodKey(Method m) {
        return Arrays.asList(m.getName(), Arrays.asList(m.getParameterTypes()));
    }
}
