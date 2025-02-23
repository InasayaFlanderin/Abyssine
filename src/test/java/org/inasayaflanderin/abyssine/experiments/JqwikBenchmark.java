package org.inasayaflanderin.abyssine.experiments;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.TimeValue;

public class JqwikBenchmark {
    Arbitrary<Double[]> provideArray() {
        return Arbitraries.doubles().list().ofMinSize(10).ofMaxSize(1000).map(list -> list.toArray(new Double[0]));
    }

    @Benchmark
    public void jqwikGenerate() {
        provideArray();
    }

    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
            .include(JqwikBenchmark.class.getSimpleName())
            .warmupIterations(1)
            .warmupTime(TimeValue.milliseconds(1))
            .measurementIterations(1000)
            .measurementTime(TimeValue.milliseconds(1))
            .forks(1)
            .build();

        new Runner(options).run();
    }
}