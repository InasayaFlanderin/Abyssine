package org.inasayaflanderin.abyssine.experiments;

import lombok.extern.slf4j.Slf4j;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.Positive;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@State(Scope.Benchmark)
public class Po2LTest {
    private static final Random rng = new Random();
    private static int value;

    int po2l1(int value) {
        var result = value;

        result |= result >> 1;
        result |= result >> 2;
        result |= result >> 4;
        result |= result >> 8;
        result |= result >> 16;
        result -= result >> 1;

        return result;
    }

    int po2l2(int value) {
        var mid = 1;

        while(mid > 0 && mid < value) mid <<= 1;

        return mid >>> 1;
    }

    @Setup(Level.Iteration)
    public void setUp() {
        value = Math.abs(rng.nextInt());
    }

    @Benchmark
    public void po2l1Benchmark(Blackhole bh) {
        bh.consume(po2l1(value));
    }

    @Benchmark
    public void po2l2Benchmark(Blackhole bh) {
        bh.consume(po2l2(value));
    }

    @Property(tries = 1000000)
    void test(@ForAll @Positive int value) {
        assertEquals(po2l1(value), po2l2(value));
    }

    public static void main(String[] args) {
        try {
            Options options = new OptionsBuilder()
                    .mode(Mode.All)
                    .timeUnit(TimeUnit.NANOSECONDS)
                    .warmupIterations(10)
                    .warmupTime(TimeValue.milliseconds(1))
                    .measurementIterations(1000)
                    .measurementTime(TimeValue.milliseconds(1))
                    .forks(1)
                    .include(Po2LTest.class.getSimpleName())
                    .build();
            new Runner(options).run();
        } catch(RunnerException e) {
            log.error("Error running JMH benchmark", e);
        }
    }
}