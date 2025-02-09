package org.inasayaflanderin.abyssine.test.template;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ArgumentCountValidationMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@TestTemplate
@ExtendWith(RepeatedParameterizedTestExtension.class)
public @interface RepeatedParameterizedTest {
    String DISPLAY_NAME_PLACEHOLDER = "{displayName}";
    String INDEX_PLACEHOLDER = "{index}";
    String ARGUMENTS_WITH_NAMES_PLACEHOLDER = "{argumentsWithNames}";
    String ARGUMENT_SET_NAME_PLACEHOLDER = "{argumentSetName}";
    String ARGUMENT_SET_NAME_OR_ARGUMENTS_WITH_NAMES_PLACEHOLDER = "{argumentsSetNameOrArgumentsWithNames}";
    String CURRENT_REPETITION_PLACEHOLDER = "{currentRepetition}";
    String TOTAL_REPETITIONS_PLACEHOLDER = "{totalRepetitions}";
    String SHORT_DISPLAY_NAME = "[" + INDEX_PLACEHOLDER + "] repetition " + CURRENT_REPETITION_PLACEHOLDER + " of " + TOTAL_REPETITIONS_PLACEHOLDER + " :: " + ARGUMENT_SET_NAME_OR_ARGUMENTS_WITH_NAMES_PLACEHOLDER;
    String LONG_DISPLAY_NAME = "[" + INDEX_PLACEHOLDER + "] repetition " + CURRENT_REPETITION_PLACEHOLDER + " of " + TOTAL_REPETITIONS_PLACEHOLDER + " :: " + DISPLAY_NAME_PLACEHOLDER;

    String name() default SHORT_DISPLAY_NAME;
    boolean autoCloseArguments() default true;
    boolean allowZeroInvocations() default false;
    int failureThreshold() default Integer.MAX_VALUE;
    ArgumentCountValidationMode argumentCountValidation() default ArgumentCountValidationMode.DEFAULT;

    int value();
}