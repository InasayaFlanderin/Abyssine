package org.inasayaflanderin.abyssine.miscellaneous.sort;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.Random;

@State(Scope.Benchmark)
public class GrailBenchmark {
    private static final Double[] data = new Double[1000000];
    private static final Random rng = new Random();

    @Setup
    public void setup() {
        for (int i = 0; i < data.length; i++) data[i] = rng.nextDouble();
    }

    @Benchmark
    public void grailSort() {
        GrailSort.grailCommonSort(data, Double::compareTo);
    }

    @Benchmark
    public void grailSortParallel() { //winner 2x
        ParallelGrailSort.grail(data, Double::compareTo);
    }

    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
                .include(GrailBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(10)
                .warmupTime(TimeValue.milliseconds(1))
                .measurementIterations(100)
                .measurementTime(TimeValue.milliseconds(1))
                .mode(Mode.All)
                .build();

        new Runner(options).run();
    }
}