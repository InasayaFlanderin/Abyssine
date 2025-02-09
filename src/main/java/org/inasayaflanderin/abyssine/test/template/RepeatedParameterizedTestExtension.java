package org.inasayaflanderin.abyssine.test.template;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.support.AnnotationConsumerInitializer;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.util.Preconditions;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
public class RepeatedParameterizedTestExtension implements TestTemplateInvocationContextProvider {
    public boolean supportsTestTemplate(ExtensionContext context) {
        if(context.getTestMethod().isEmpty()) return false;

        Method templateMethod = context.getTestMethod().get();
        Optional<RepeatedParameterizedTest> annotation = AnnotationSupport.findAnnotation(templateMethod, RepeatedParameterizedTest.class);

        if(annotation.isEmpty()) return false;

        RepeatedParameterizedTestMethodContext repeatedParameterizedTestMethodContext = new RepeatedParameterizedTestMethodContext(templateMethod, annotation.get());
        Preconditions.condition(repeatedParameterizedTestMethodContext.hasPotentialValidSignature(),
                () -> "@RepeatedParameterizedTest method [" + templateMethod.toGenericString() + "] declares formal parameters in an invalid order: argument aggregators must be declared after any indexed arguments and before any arguments resolved by another ParameterResolver.");
        getStore(context).put("context", repeatedParameterizedTestMethodContext);

        return true;
    }

    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        RepeatedParameterizedTestMethodContext testMethodContext = getMethodContext(context);
        Method testMethod = testMethodContext.templateMethod;
        RepeatedParameterizedTest repeatedParameterizedTest = AnnotationSupport.findAnnotation(testMethod, RepeatedParameterizedTest.class).get();
        var totalRepetition = repeatedParameterizedTest.value();
        Preconditions.condition(totalRepetition > 0, () -> "Configuration error: @RepeatedParameterizedTest on method " + testMethod + " must be declared with a positive 'value'.");
        AtomicInteger failureCount = new AtomicInteger();
        int failureThreshold = repeatedParameterizedTest.failureThreshold();

        if (failureThreshold != Integer.MAX_VALUE)
            Preconditions.condition(failureThreshold > 0 && failureThreshold < totalRepetition, () -> "Configuration error: @RepeatedParameterizedTest on method " + testMethod + " with 'failureThreshold' must be positive and less than total repetition.");

        AtomicLong invocationCount = new AtomicLong();
        List<ArgumentsSource> argumentsSources = AnnotationSupport.findRepeatableAnnotations(testMethod, ArgumentsSource.class);
        Preconditions.notEmpty(argumentsSources, "Configuration error: You must configure at least one arguments source for this @RepeatedParameterizedTest");
        String pattern = testMethodContext.annotation.name();
        Preconditions.notBlank(pattern.trim(), "Configuration error: @RepeatedParameterizedTest on method " + testMethod + " must be declared with a non-empty name.");
        RepeatedParameterizedTestNameFormatter nameFormatter = new RepeatedParameterizedTestNameFormatter(pattern, context.getDisplayName(), testMethodContext, context.getConfigurationParameter("junit.jupiter.params.displayname.argument.maxlength", Integer::parseInt).orElse(512));

        return argumentsSources.stream().map(ArgumentsSource::value)
                .map(clazz -> RepeatedParameterizedTestHelper.instantiate(ArgumentsProvider.class, clazz, context))
                .map(provider -> AnnotationConsumerInitializer.initialize(testMethod, provider)).flatMap(provider -> {
            try {
                return provider.provideArguments(context);
            } catch (Exception e) {
                log.error("Error while providing arguments");

                throw new RuntimeException(e);
            }
        }).flatMap(argument -> {
            invocationCount.incrementAndGet();

            return IntStream.rangeClosed(1, totalRepetition)
                    .mapToObj(repetition -> new DefaultRepetitionInfo(repetition, totalRepetition, failureCount, failureThreshold))
                    .map(repetitionInfo -> (TestTemplateInvocationContext) new RepeatedParameterizedTestInvocationContext(testMethodContext, argument, repetitionInfo, invocationCount.intValue(), nameFormatter));
        }).onClose(() -> Preconditions.condition(invocationCount.get() > 0L || testMethodContext.annotation.allowZeroInvocations(), "Configuration error: You must configure at least one set of arguments for this @RepeatedParameterizedTest"));
    }
    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(RepeatedParameterizedTestExtension.class, context.getRequiredTestMethod()));
    }

    private RepeatedParameterizedTestMethodContext getMethodContext(ExtensionContext context) {
        return getStore(context).get("context", RepeatedParameterizedTestMethodContext.class);
    }
}