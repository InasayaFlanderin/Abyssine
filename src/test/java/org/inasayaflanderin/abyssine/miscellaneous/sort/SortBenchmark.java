package org.inasayaflanderin.abyssine.miscellaneous.sort;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class SortBenchmark {
    private static final Random rng = new Random();
    private static final Double[] array = new Double[1000];

    @Setup(Level.Iteration)
    public void setUp() {
        for (int i = 0; i < array.length; i++) array[i] = rng.nextDouble();
    }

    @Benchmark
    @Measurement(iterations = 100, timeUnit = TimeUnit.MILLISECONDS)
    public void selection() {
        Sort.selection(array, Double::compareTo);
    }
}