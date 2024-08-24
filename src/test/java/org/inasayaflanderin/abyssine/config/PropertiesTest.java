package org.inasayaflanderin.abyssine.config;

public class PropertiesTest {
    public static void main(String[] args) {
        AbyssineConfigurations.getConfigurations().getSystemCollector();
        AbyssineProperties props = AbyssineConfigurations.getConfigurations().getProperties();
        props.forEach((key, value) -> System.out.println(key.getClass() + ": " + key.toString() + ", " + value.getClass() + ": " + value.toString()));
    }
}
