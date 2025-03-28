package org.inasayaflanderin.abyssine.experiments;

import net.jqwik.api.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@State(Scope.Benchmark)
public class SqrtLPo2 {
    private static int value;
    private static final Random rng = new Random();

    int sqrtlpo21(int value) {
        var result = 1;

        while(result * result < value) result *= 2;

        return result;
    }

    int sqrtlpo22(int value) {
        var result = (int) Math.ceil(Math.sqrt(value));
        result--;
        result |= result >> 1;
        result |= result >> 2;
        result |= result >> 4;
        result |= result >> 8;
        result |= result >> 16;
        result++;

        return result;
    }

    @Provide
    Arbitrary<Integer> provider() {
        return Arbitraries.integers().greaterOrEqual(17).filter(i -> i < 1 << 30);
    }

    @Property(tries = 1 << 29)
    public void test(@ForAll("provider") int value) {
        assertEquals(sqrtlpo21(value), sqrtlpo22(value));
    }

    @Setup
    public void setUp() {
        do {
            value = Math.abs(rng.nextInt());
        } while(value < 16 || value >= 1 << 30);
    }

    @Benchmark
    public void benchmarkSqrtLPo21(Blackhole bh) {
        bh.consume(sqrtlpo21(value));
    }

    @Benchmark
    public void benchmarkSqrtLPo22(Blackhole bh) {
        bh.consume(sqrtlpo22(value));
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(SqrtLPo2.class.getSimpleName())
                .forks(1)
                .warmupIterations(100)
                .warmupTime(TimeValue.milliseconds(1))
                .measurementIterations(10000)
                .measurementTime(TimeValue.milliseconds(1))
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.All)
                .build();

        new Runner(options).run();
    }
}