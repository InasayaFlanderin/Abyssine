package org.inasayaflanderin.abyssine.config;

import lombok.NonNull;
import org.inasayaflanderin.abyssine.exceptions.AbyssineException;
import org.inasayaflanderin.abyssine.parallel.ReentrantLock;
import org.inasayaflanderin.abyssine.primitives.Pair;

import java.util.Properties;
import java.util.function.BiConsumer;

public class AbyssineProperties {
    private static volatile AbyssineProperties properties;
    private Properties propertiesContent;
    private final ReentrantLock propertyLock;

    public enum PropertiesType {
        GLOBAL,
        LOCAL
    }

    private AbyssineProperties() {
        propertyLock = new ReentrantLock("Property lock");
        propertyLock.lock();
        try {
            propertiesContent = new Properties();
            loadDefaultProperties();
        } finally {
            propertyLock.unlock();
        }
    }

    public static AbyssineProperties getProperties() {
        if(properties == null) {
            synchronized(AbyssineProperties.class) {
                properties = new AbyssineProperties();
            }
        }

        return properties;
    }

    public boolean containsProperty(@NonNull String key) {
        return propertiesContent.containsKey(key.toLowerCase());
    }

    public void addProperty(@NonNull String key, @NonNull Object value, @NonNull PropertiesType type) {
        propertyLock.lock();
        try {
            if (containsProperty(key)) propertiesContent.remove(key);

            propertiesContent.put(key.toLowerCase(), new Pair<>(type, value));
        } finally {
            propertyLock.unlock();
        }
    }

    public void addProperty(@NonNull Properties properties, @NonNull PropertiesType type) {
        properties.forEach((key, value) -> {
            if(!(key instanceof String)) throw new AbyssineException("Key properties needed to be a string");

            addProperty((String) key, value, type);
        });
    }

    @SuppressWarnings("unchecked")
    public Object getProperty(@NonNull String key) {
        return ((Pair<PropertiesType, ? super Object>) propertiesContent.get(key)).getSecond();
    }

    private void loadDefaultProperties() {
        addProperty("cpu_load_computation_internal", 1000L, PropertiesType.GLOBAL);
    }

    public void forEach(BiConsumer<? super Object, ? super Object> action) {
        propertiesContent.forEach(action);
    }
}
