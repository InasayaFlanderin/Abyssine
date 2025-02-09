package org.inasayaflanderin.abyssine.test.template;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.aggregator.DefaultArgumentsAccessor;
import org.junit.jupiter.params.converter.ArgumentConverter;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.DefaultArgumentConverter;
import org.junit.jupiter.params.support.AnnotationConsumerInitializer;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class RepeatedParameterizedTestMethodContext {
    interface Resolver {
        Object resolve(ParameterContext parameterContext, Object[] arguments, int invocationIndex);
    }

    static class ConverterResolver implements Resolver {
        static final ConverterResolver DEFAULT = new ConverterResolver(DefaultArgumentConverter.INSTANCE);

        private final ArgumentConverter argumentConverter;

        ConverterResolver(ArgumentConverter converter) {
            this.argumentConverter = converter;
        }

        public Object resolve(ParameterContext parameterContext, Object[] arguments, int invocationIndex) {
            Object argument = arguments[parameterContext.getIndex()];

            return this.argumentConverter.convert(argument, parameterContext);
        }
    }

    static class AggregatorResolver implements Resolver {
        static final AggregatorResolver DEFAULT = new AggregatorResolver((accessor, context) -> accessor);

        private final ArgumentsAggregator argumentsAggregator;

        AggregatorResolver(ArgumentsAggregator aggregator) {
            this.argumentsAggregator = aggregator;
        }

        public Object resolve(ParameterContext parameterContext, Object[] arguments, int invocationIndex) {
            ArgumentsAccessor accessor = new DefaultArgumentsAccessor(parameterContext, invocationIndex, arguments);

            return this.argumentsAggregator.aggregateArguments(accessor, parameterContext);
        }
    }

    enum ResolverType {
        CONVERTER {
            Resolver createResolver(ParameterContext parameterContext, ExtensionContext extensionContext) {
                return AnnotationSupport.findAnnotation(parameterContext.getParameter(), ConvertWith.class).map(ConvertWith::value)
                        .map(clazz -> RepeatedParameterizedTestHelper.instantiate(ArgumentConverter.class, clazz, extensionContext))
                        .map(converter -> AnnotationConsumerInitializer.initialize(parameterContext.getParameter(), converter))
                        .map(ConverterResolver::new).orElse(ConverterResolver.DEFAULT);
            }
        },
        AGGREGATOR {
            Resolver createResolver(ParameterContext parameterContext, ExtensionContext extensionContext) {
                return AnnotationSupport.findAnnotation(parameterContext.getParameter(), AggregateWith.class).map(AggregateWith::value)
                        .map(clazz -> RepeatedParameterizedTestHelper.instantiate(ArgumentsAggregator.class, clazz, extensionContext))
                        .map(aggregator -> AnnotationConsumerInitializer.initialize(parameterContext.getParameter(), aggregator))
                        .map(AggregatorResolver::new).orElse(AggregatorResolver.DEFAULT);
            }
        };

        abstract RepeatedParameterizedTestMethodContext.Resolver createResolver(ParameterContext parameterContext, ExtensionContext extensionContext);
    }

    public final Method templateMethod;
    public final RepeatedParameterizedTest annotation;
    private final Parameter[] parameters;
    private final Resolver[] resolvers;
    private final List<ResolverType> resolverTypes;

    public RepeatedParameterizedTestMethodContext(Method templateMethod, RepeatedParameterizedTest annotation) {
        this.templateMethod = templateMethod;
        this.annotation = annotation;
        this.parameters = templateMethod.getParameters();
        this.resolvers = new Resolver[this.parameters.length];
        this.resolverTypes = new ArrayList<>(this.parameters.length);

        for (Parameter parameter : this.parameters) this.resolverTypes.add(ArgumentsAccessor.class.isAssignableFrom(parameter.getType()) ||
                AnnotationSupport.isAnnotated(parameter, AggregateWith.class) ? ResolverType.AGGREGATOR : ResolverType.CONVERTER);
    }

    public boolean hasPotentialValidSignature() {
        var indexOfPreviousAggregator = -1;

        for(int i = 0; i < this.parameters.length; ++i) {
            if(isAggregator(i)) {
                if(indexOfPreviousAggregator != -1 && i != indexOfPreviousAggregator + 1) return false;

                indexOfPreviousAggregator = i;
            }
        }

        return true;
    }

    public boolean isAggregator(int parameterIndex) {
        return this.resolverTypes.get(parameterIndex) == ResolverType.AGGREGATOR;
    }

    public boolean hasAggregator() {
        return this.resolverTypes.contains(ResolverType.AGGREGATOR);
    }

    public int parameterCount() {
        return this.parameters.length;
    }

    public int indexOfFirstAggregator() {
        return this.resolverTypes.indexOf(ResolverType.AGGREGATOR);
    }

    public Object resolve(ParameterContext parameterContext, ExtensionContext extensionContext, Object[] arguments, int invocationIndex) {
        return this.getResolver(parameterContext, extensionContext).resolve(parameterContext, arguments, invocationIndex);
    }

    private RepeatedParameterizedTestMethodContext.Resolver getResolver(ParameterContext parameterContext, ExtensionContext extensionContext) {
        int index = parameterContext.getIndex();
        if (this.resolvers[index] == null) {
            this.resolvers[index] = this.resolverTypes.get(index).createResolver(parameterContext, extensionContext);
        }

        return this.resolvers[index];
    }

    Optional<String> getParameterName(int parameterIndex) {
        if (parameterIndex >= this.parameters.length) {
            return Optional.empty();
        } else {
            Parameter parameter = this.parameters[parameterIndex];
            if (!parameter.isNamePresent()) {
                return Optional.empty();
            } else {
                return this.hasAggregator() && parameterIndex >= this.indexOfFirstAggregator() ? Optional.empty() : Optional.of(parameter.getName());
            }
        }
    }
}