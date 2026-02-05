package framework.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BeanRegistry {
    private final Map<Class<?>, Class<?>> typeMapping = new HashMap<>();

    private final Set<Class<?>> registeredBeans = new HashSet<>();

    public void registerBean(final Class<?> beanClass) {
        registeredBeans.add(beanClass);
        resolveInterfaces(beanClass);
    }

    public boolean contains(final Class<?> beanClass) {
        return registeredBeans.contains(beanClass);
    }

    public void resolveInterfaces(final Class<?> beanClass) {
        final Class<?>[] interfaces = beanClass.getInterfaces();
        for (Class<?> iface : interfaces) {
            if (typeMapping.containsKey(iface)){
                throw new RuntimeException("Found more then one implementation for interface" + iface);
            }
            typeMapping.put(iface, beanClass);
        }
    }

    public Class<?> resolveType(final Class<?> requestedType) {
        if (requestedType.isInterface()) {
            final Class<?> implementation = typeMapping.get(requestedType);
            if (implementation == null) {
                throw new RuntimeException("No implementation found for: " + requestedType);
            }
            System.out.println(implementation);
            return implementation;
        }
        return requestedType;
    }
}
