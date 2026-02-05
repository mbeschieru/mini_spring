package framework.context;

import framework.annotations.AutoWired;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BeanFactory {

    private final BeanRegistry registry;
    private final Map<Class<?>, Object> singletons = new HashMap<>();
    private final Set<Class<?>> creating = new HashSet<>();

    public BeanFactory(BeanRegistry registry) {
        this.registry = registry;
    }

    public <T> T getBean(final Class<T> beanClass){

        final Class<?> concreteClass = this.registry.resolveType(beanClass);

        if (singletons.containsKey(concreteClass)) {
            return beanClass.cast(singletons.get(concreteClass));
        }

        if (!this.registry.contains(concreteClass)) {
            throw new RuntimeException("No bean registered for " + concreteClass);
        }

        if (creating.contains(concreteClass)) {
            throw new RuntimeException("Circular dependency for " + concreteClass);
        }

        creating.add(concreteClass);

        try {
            final Object instance = createBean(concreteClass);
            return beanClass.cast(instance);
        } finally {
            creating.remove(concreteClass);
        }
    }

    private Object createBean(final Class<?> concreteClass) {
        try {

            final Constructor<?> constructor = this.selectAnnotatedConstructor(concreteClass);
            final Class<?>[] paramTypes = constructor.getParameterTypes();
            final Object[] args = new Object[paramTypes.length];
            for (int i = 0 ; i < paramTypes.length; i++) {
                args[i] = getBean(paramTypes[i]);
            }

            final Object instance = constructor.newInstance(args);
            singletons.put(concreteClass, instance);

            return instance;

        } catch (final Exception e) {
            throw new RuntimeException("Failed to create bean " + concreteClass, e);
        }
    }

    private Constructor<?> selectAnnotatedConstructor(final Class<?> beanClass){

        final Constructor<?>[] constructors = beanClass.getConstructors();

        Constructor<?> autowiredConstructor = null;

        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(AutoWired.class)) {
                if (autowiredConstructor != null) {
                    throw new RuntimeException("@Autowired found in multiple constructors in " + beanClass.getName());
                }
                autowiredConstructor = constructor;
            }
        }

        //autowired found
        if (autowiredConstructor != null) {
            return autowiredConstructor;
        }

        //first constructor
        if (constructors.length == 1) {
            return constructors[0];
        }

        //no-args constructor
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                return constructor;
            }
        }

        throw new RuntimeException("No constructor with @AutoWired or no arg constructor found for class " + beanClass.getName());
    }
}
