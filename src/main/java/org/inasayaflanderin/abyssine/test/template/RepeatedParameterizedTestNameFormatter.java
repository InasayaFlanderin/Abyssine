package org.inasayaflanderin.abyssine.test.template;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.platform.commons.util.StringUtils;

import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class RepeatedParameterizedTestNameFormatter {
    private record PlaceholderPosition(int index, String placeholder) { }

    private record ArgumentsContext(int invocationIndex, Arguments arguments, Object[] consumedArguments) { }

    @FunctionalInterface
    private interface PartialFormatter {
        PartialFormatter INDEX = (context, result) -> result.append(context.invocationIndex);
        PartialFormatter ARGUMENT_SET_NAME = (context, result) -> {
            if(!(context.arguments instanceof Arguments.ArgumentSet)) {
                log.error("When the display name pattern for a @RepeatedParameterizedTest contains {}, the arguments must be supplied as an ArgumentSet.", "{argumentSetName}");

                throw new ExtensionConfigurationException("");
            }
        };

        void append(ArgumentsContext argumentsContext, StringBuffer result);
    }

    private static class MessageFormatPartialFormatter implements PartialFormatter {
        private final MessageFormat messageFormat;
        private final int argumentMaxLength;

        MessageFormatPartialFormatter(String pattern, int argumentMaxLength) {
            this.messageFormat = new MessageFormat(pattern);
            this.argumentMaxLength = argumentMaxLength;
        }

        public synchronized void append(ArgumentsContext context, StringBuffer result) {
            this.messageFormat.format(this.makeReadable(context.consumedArguments), result, new FieldPosition(0));
        }

        private Object[] makeReadable(Object[] arguments) {
            Format[] formats = this.messageFormat.getFormatsByArgumentIndex();
            Object[] result = Arrays.copyOf(arguments, Math.min(arguments.length, formats.length), Object[].class);

            for(int i = 0; i < result.length; ++i) {
                if (formats[i] == null) {
                    result[i] = this.truncateIfExceedsMaxLength(StringUtils.nullSafeToString(arguments[i]));
                }
            }

            return result;
        }

        private String truncateIfExceedsMaxLength(String argument) {
            return argument != null && argument.length() > this.argumentMaxLength ? argument.substring(0, this.argumentMaxLength - 1) + '…' : argument;
        }
    }

    private static class CachingByArgumentsLengthPartialFormatter implements PartialFormatter {
        private final ConcurrentMap<Integer, PartialFormatter> cache = new ConcurrentHashMap<>(1);
        private final Function<Integer, PartialFormatter> factory;

        CachingByArgumentsLengthPartialFormatter(Function<Integer, PartialFormatter> factory) {
            this.factory = factory;
        }

        public void append(ArgumentsContext context, StringBuffer result) {
            this.cache.computeIfAbsent(context.consumedArguments.length, this.factory).append(context, result);
        }
    }

    private static class PartialFormatters {
        private final Map<String, RepeatedParameterizedTestNameFormatter.PartialFormatter> formattersByPlaceholder;
        private int minimumPlaceholderLength;

        private PartialFormatters() {
            this.formattersByPlaceholder = new LinkedHashMap<>();
            this.minimumPlaceholderLength = Integer.MAX_VALUE;
        }

        void put(String placeholder, RepeatedParameterizedTestNameFormatter.PartialFormatter formatter) {
            this.formattersByPlaceholder.put(placeholder, formatter);
            int newPlaceholderLength = placeholder.length();
            if (newPlaceholderLength < this.minimumPlaceholderLength) {
                this.minimumPlaceholderLength = newPlaceholderLength;
            }

        }

        PartialFormatter get(String placeholder) {
            return this.formattersByPlaceholder.get(placeholder);
        }

        Set<String> placeholders() {
            return this.formattersByPlaceholder.keySet();
        }
    }

    private final PartialFormatter[] partialFormatters;

    public RepeatedParameterizedTestNameFormatter(String pattern, String displayName, RepeatedParameterizedTestMethodContext methodContext, int argumentMaxLength) {
        List<PartialFormatter> result = new ArrayList<>();
        PartialFormatter argumentsWithNamesFormatter = new CachingByArgumentsLengthPartialFormatter((length) -> new MessageFormatPartialFormatter(IntStream.range(0, length).mapToObj((index) -> methodContext.getParameterName(index).map((name) -> name + "=").orElse("") + "{" + index + "}").collect(Collectors.joining(", ")), argumentMaxLength));
        PartialFormatters formatters = new PartialFormatters();
        formatters.put(RepeatedParameterizedTest.INDEX_PLACEHOLDER, PartialFormatter.INDEX);
        formatters.put(RepeatedParameterizedTest.DISPLAY_NAME_PLACEHOLDER, (context, results) -> results.append(displayName));
        formatters.put(RepeatedParameterizedTest.ARGUMENT_SET_NAME_PLACEHOLDER, PartialFormatter.ARGUMENT_SET_NAME);
        formatters.put(RepeatedParameterizedTest.ARGUMENTS_WITH_NAMES_PLACEHOLDER, argumentsWithNamesFormatter);
        formatters.put("{arguments}", new CachingByArgumentsLengthPartialFormatter((length) -> new MessageFormatPartialFormatter(IntStream.range(0, length).mapToObj((index) -> "{" + index + "}").collect(Collectors.joining(", ")), argumentMaxLength)));
        formatters.put(RepeatedParameterizedTest.ARGUMENT_SET_NAME_OR_ARGUMENTS_WITH_NAMES_PLACEHOLDER, (context, results) -> {
            PartialFormatter formatterToUse = context.arguments instanceof Arguments.ArgumentSet ? PartialFormatter.ARGUMENT_SET_NAME : argumentsWithNamesFormatter;
            formatterToUse.append(context, results);
        });

        PlaceholderPosition position;
        for (String unparsedSegment = pattern; StringUtils.isNotBlank(unparsedSegment); unparsedSegment = unparsedSegment.substring(position.index + position.placeholder.length())) {
            position = null;

            if (unparsedSegment.length() >= formatters.minimumPlaceholderLength) {
                for(String placeholder : formatters.placeholders()) {
                    int index = unparsedSegment.indexOf(placeholder);
                    if (index >= 0) {
                        if (index < formatters.minimumPlaceholderLength) {
                            position = new PlaceholderPosition(index, placeholder);
                        }

                        if (position == null || index < position.index) {
                            position = new PlaceholderPosition(index, placeholder);
                        }
                    }
                }
            }

            if (position == null) {
                result.add(determineNonPlaceholderFormatter(unparsedSegment, argumentMaxLength));

                break;
            }

            if (position.index > 0) {
                String before = unparsedSegment.substring(0, position.index);
                result.add(determineNonPlaceholderFormatter(before, argumentMaxLength));
            }

            result.add(formatters.get(position.placeholder));

            if ("{currentRepetition}".equals(position.placeholder) || "{totalRepetitions}".equals(position.placeholder)) break;
        }

        this.partialFormatters = result.toArray(new PartialFormatter[0]);
    }

    private static PartialFormatter determineNonPlaceholderFormatter(String segment, int argumentMaxLength) {
        if (segment.contains("{currentRepetition}") && segment.contains("{totalRepetitions}")) return (context, result) -> result.append(segment);

        return segment.contains("{") ? new MessageFormatPartialFormatter(segment, argumentMaxLength) : (context, result) -> result.append(segment);
    }

    public String format(int invocationIndex, Arguments arguments, Object[] consumedArguments, int currentRepetition, int totalRepetitions) {
        ArgumentsContext argumentsContext = new ArgumentsContext(invocationIndex, arguments, Arrays.stream(consumedArguments)
                .map(argument -> argument instanceof Named<?> ? ((Named<?>) argument).getName() : argument).toArray());
        StringBuffer result = new StringBuffer();

        for(PartialFormatter partialFormatter : this.partialFormatters) partialFormatter.append(argumentsContext, result);

        return result.toString().replace("{currentRepetition}", String.valueOf(currentRepetition)).replace("{totalRepetitions}", String.valueOf(totalRepetitions));
    }
}