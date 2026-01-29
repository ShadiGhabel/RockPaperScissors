package core;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class DIContainer {
    private static final Map<Class<?>, Class<?>> CLASS_REGISTRY = new HashMap<>();
    private static final Map<Class<?>, Object> INSTANCE_CACHE = new HashMap<>();

    public static <T> void register(Class<T> interfaceClass, Class<? extends T> implementationClass) {
        CLASS_REGISTRY.put(interfaceClass, implementationClass);
    }

    @SuppressWarnings("unchecked")
    public static <T> T resolve(Class<T> interfaceClass) {
        if (INSTANCE_CACHE.containsKey(interfaceClass)) {
            return (T) INSTANCE_CACHE.get(interfaceClass);
        }

        Class<?> implementationClass = CLASS_REGISTRY.get(interfaceClass);
        if (implementationClass == null) {
            throw new RuntimeException("No implementation registered for: " + interfaceClass.getName());
        }

        try {
            Constructor<?> constructor = implementationClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();
            INSTANCE_CACHE.put(interfaceClass, instance);
            return (T) instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of: " + implementationClass.getName(), e);
        }
    }

    public static void clear() {
        INSTANCE_CACHE.clear();
        CLASS_REGISTRY.clear();
    }
}
