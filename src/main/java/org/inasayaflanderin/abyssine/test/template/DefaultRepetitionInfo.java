package org.inasayaflanderin.abyssine.test.template;

import org.junit.jupiter.api.RepetitionInfo;

import java.util.concurrent.atomic.AtomicInteger;

public record DefaultRepetitionInfo(int getCurrentRepetition, int getTotalRepetitions, AtomicInteger failureCount, int getFailureThreshold) implements RepetitionInfo {
    public int getFailureCount() {
        return this.failureCount.get();
    }
}