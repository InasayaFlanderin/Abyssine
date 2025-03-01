package org.inasayaflanderin.abyssine.miscellaneous.sort;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.Random;

@State(Scope.Benchmark)
public class GrailBenchmark {
    private static final Double[] data = new Double[10000];
    private static final Random rng = new Random();

    @Setup
    public void setup() {
        for (int i = 0; i < data.length; i++) data[i] = rng.nextDouble();
    }

    @Benchmark
    public void grailSort() {
        GrailSort.grailCommonSort(data, Double::compareTo);
    }
}