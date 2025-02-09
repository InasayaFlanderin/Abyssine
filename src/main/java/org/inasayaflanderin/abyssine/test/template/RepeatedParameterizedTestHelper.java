package org.inasayaflanderin.abyssine.test.template;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.reflect.Constructor;

public class RepeatedParameterizedTestHelper {
    static <T> T instantiate(Class<T> interfaceClazz, Class<? extends T> implementationClass, ExtensionContext context) {
        return context.getExecutableInvoker().invoke(findConstructor(interfaceClazz, implementationClass));
    }

    @SuppressWarnings("unchecked")
    static <T> Constructor<? extends T> findConstructor(Class<T> interfaceClazz, Class<? extends T> implementationClass) {
        Preconditions.condition(!ReflectionUtils.isInnerClass(implementationClass),
                () -> "The " + interfaceClazz.getSimpleName() + " [" + implementationClass.getSimpleName() + "] must either be a top-level class or a static nested class.");
        Constructor<? extends T>[] constructors = (Constructor<? extends T>[]) implementationClass.getDeclaredConstructors();

        if(constructors.length == 1) return constructors[0];

        for(Constructor<? extends T> constructor : constructors) if(constructor.getParameterCount() == 0) return constructor;

        throw new JUnitException("Failed to find constructor for " + interfaceClazz.getSimpleName() + " [" + implementationClass.getSimpleName() + "]. Please ensure that a no-argument or a single constructor exists.");
    }
}