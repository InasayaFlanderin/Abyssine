package org.inasayaflanderin.abyssine.test.template;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.params.provider.Arguments;

import java.util.*;

public class RepeatedParameterizedTestInvocationContext implements TestTemplateInvocationContext {
    private final RepeatedParameterizedTestMethodContext repeatedParameterizedTestMethodContext;
    private final Arguments arguments;
    private final DefaultRepetitionInfo defaultRepetitionInfo;
    private final int innovationIndex;
    private final RepeatedParameterizedTestNameFormatter nameFormatter;
    private final Object[] consumedArguments;

    public RepeatedParameterizedTestInvocationContext(RepeatedParameterizedTestMethodContext repeatedParameterizedTestMethodContext, Arguments arguments, DefaultRepetitionInfo defaultRepetitionInfo, int innovationIndex, RepeatedParameterizedTestNameFormatter nameFormatter) {
        this.repeatedParameterizedTestMethodContext = repeatedParameterizedTestMethodContext;
        this.arguments = arguments;
        this.innovationIndex = innovationIndex;
        this.defaultRepetitionInfo = defaultRepetitionInfo;
        this.nameFormatter = nameFormatter;

        if(repeatedParameterizedTestMethodContext.hasAggregator()) {
            this.consumedArguments = arguments.get();
        } else {
            var parameterCount = repeatedParameterizedTestMethodContext.parameterCount();
            this.consumedArguments = arguments.get().length > parameterCount ? Arrays.copyOf(arguments.get(), parameterCount) : arguments.get();
        }
    }

    public String getDisplayName(int innovationIndex) {
        return this.nameFormatter.format(this.innovationIndex, this.arguments, this.consumedArguments, this.defaultRepetitionInfo.getCurrentRepetition(), this.defaultRepetitionInfo.getTotalRepetitions());
    }

    public List<Extension> getAdditionalExtensions() {
        return Arrays.asList(new RepetitionParameterizedParameterResolver(this.repeatedParameterizedTestMethodContext, this.consumedArguments, this.defaultRepetitionInfo, this.innovationIndex), new ArgumentCountValidator(repeatedParameterizedTestMethodContext, arguments));
    }
}