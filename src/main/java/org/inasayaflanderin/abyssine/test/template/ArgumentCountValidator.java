package org.inasayaflanderin.abyssine.test.template;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.jupiter.params.ArgumentCountValidationMode;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.platform.commons.util.Preconditions;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
public record ArgumentCountValidator(RepeatedParameterizedTestMethodContext methodContext, Arguments arguments) implements InvocationInterceptor {
    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArgumentCountValidator.class);

    public void interceptTestTemplateMethod(InvocationInterceptor.Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        this.validateArgumentCount(extensionContext, this.arguments);
        invocation.proceed();
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getRoot().getStore(NAMESPACE);
    }

    private void validateArgumentCount(ExtensionContext extensionContext, Arguments arguments) {
        ArgumentCountValidationMode argumentCountValidationMode = this.getArgumentCountValidationMode(extensionContext);
        switch (argumentCountValidationMode) {
            case DEFAULT:
            case NONE:
                return;
            case STRICT:
                int testParamCount = extensionContext.getRequiredTestMethod().getParameterCount();
                int argumentsCount = arguments.get().length;
                Preconditions.condition(testParamCount == argumentsCount, () -> String.format("Configuration error: the @ParameterizedTest has %s argument(s) but there were %s argument(s) provided.%nNote: the provided arguments are %s", testParamCount, argumentsCount, Arrays.toString(arguments.get())));
                return;
            default:
                throw new ExtensionConfigurationException("Unsupported argument count validation mode: " + argumentCountValidationMode);
        }
    }

    private ArgumentCountValidationMode getArgumentCountValidationMode(ExtensionContext extensionContext) {
        RepeatedParameterizedTest parameterizedTest = this.methodContext.annotation;
        return parameterizedTest.argumentCountValidation() != ArgumentCountValidationMode.DEFAULT ? parameterizedTest.argumentCountValidation() : this.getArgumentCountValidationModeConfiguration(extensionContext);
    }

    private ArgumentCountValidationMode getArgumentCountValidationModeConfiguration(ExtensionContext extensionContext) {
        String key = "junit.jupiter.params.argumentCountValidation";
        ArgumentCountValidationMode fallback = ArgumentCountValidationMode.NONE;
        ExtensionContext.Store store = this.getStore(extensionContext);
        return store.getOrComputeIfAbsent(key, (__) -> {
            Optional<String> optionalConfigValue = extensionContext.getConfigurationParameter(key);
            if (optionalConfigValue.isPresent()) {
                String configValue = optionalConfigValue.get();
                Optional<ArgumentCountValidationMode> enumValue = Arrays.stream(ArgumentCountValidationMode.values()).filter((mode) -> mode.name().equalsIgnoreCase(configValue)).findFirst();
                if (enumValue.isPresent()) {
                    log.info("Using ArgumentCountValidationMode {} set via the {}configuration parameter.", enumValue.get().name(), key);

                    return enumValue.get();
                } else {
                    log.warn("Invalid ArgumentCountValidationMode '{}' set via the '{}' configuration parameter. Falling back to the {} default value.", configValue, key, fallback.name());

                    return fallback;
                }
            } else {
                return fallback;
            }
        }, ArgumentCountValidationMode.class);
    }
}