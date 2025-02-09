package org.inasayaflanderin.abyssine.test.template;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public record RepetitionParameterizedParameterResolver(RepeatedParameterizedTestMethodContext methodContext, Object[] arguments, DefaultRepetitionInfo repetitionInfo, int invocationIndex) implements ParameterResolver, TestWatcher, ExecutionCondition, AfterTestExecutionCallback {
    private record CloseableArgument(AutoCloseable autoCloseable) implements ExtensionContext.Store.CloseableResource {
        public void close() throws Throwable {
                this.autoCloseable.close();
            }
        }

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(RepetitionParameterizedParameterResolver.class);

    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Executable declaringExecutable = parameterContext.getDeclaringExecutable();
        Method testMethod = extensionContext.getTestMethod().orElse(null);
        int parameterIndex = parameterContext.getIndex();
        if (!declaringExecutable.equals(testMethod)) {
            return false;
        } else if (this.methodContext.isAggregator(parameterIndex)) {
            return true;
        } else if (this.methodContext.hasAggregator()) {
            return parameterIndex < this.methodContext.indexOfFirstAggregator();
        } else {
            return parameterIndex < this.arguments.length;
        }
    }

    public TestInstantiationAwareExtension.ExtensionContextScope getTestInstantiationExtensionContextScope(ExtensionContext rootContext) {
        return ExtensionContextScope.TEST_METHOD;
    }

    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return this.methodContext.resolve(parameterContext, extensionContext, this.extractPayloads(this.arguments), this.invocationIndex);
    }

    private Object[] extractPayloads(Object[] arguments) {
        return Arrays.stream(arguments).map((argument) -> argument instanceof Named ? ((Named<?>) argument).getPayload() : argument).toArray();
    }

    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        int failureThreshold = this.repetitionInfo.getFailureThreshold();
        return this.repetitionInfo.getFailureCount() >= failureThreshold ? ConditionEvaluationResult.disabled("Failure threshold [" + failureThreshold + "] exceeded") : ConditionEvaluationResult.enabled("Failure threshold not exceeded");
    }

    public void afterTestExecution(ExtensionContext context) {
        RepeatedParameterizedTest parameterizedTest = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), RepeatedParameterizedTest.class).get();

        if (parameterizedTest.autoCloseArguments()) {
            ExtensionContext.Store store = context.getStore(NAMESPACE);
            AtomicInteger argumentIndex = new AtomicInteger();
            Stream<Object> var10000 = Arrays.stream(this.arguments);
            Objects.requireNonNull(AutoCloseable.class);
            var10000 = var10000.filter(AutoCloseable.class::isInstance);
            Objects.requireNonNull(AutoCloseable.class);
            var10000.map(AutoCloseable.class::cast).map(CloseableArgument::new).forEach((closeable) -> store.put("closeableArgument#" + argumentIndex.incrementAndGet(), closeable));
        }
    }

    public void testFailed(ExtensionContext context, Throwable cause) {
        this.repetitionInfo.failureCount().incrementAndGet();
    }
}