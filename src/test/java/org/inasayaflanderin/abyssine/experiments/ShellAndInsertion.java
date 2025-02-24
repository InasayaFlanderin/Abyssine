package org.inasayaflanderin.abyssine.experiments;

import org.inasayaflanderin.abyssine.miscellaneous.sort.Sort;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class ShellAndInsertion {
    Double[] array = new Double[10000];
    Random random = new Random();

    private static <D> void shellPass(List<D> data, Comparator<D> comparator, int start, int length, int gap) {
        for(int item = gap; item < length; item++) {
            D temp = data.get(start + item);
            int index = start + item;

            if(comparator.compare(data.get(index - gap), temp) < 0) {
                continue;
            }

            do {
                data.set(index, data.get(index - gap));
                index -= gap;
            } while(index - gap > start && comparator.compare(data.get(index - gap), temp) > 0);

            data.set(index, temp);
        }
    }

    private static <D> void shellSort(List<D> data, Comparator<D> comparator) {
        int k = 0;
        while((4 << (2*k)) + (3 << k) + 1 < data.size()) {
            k++;
        }

        while(k-- > 0) {
            int gap = (4 << (2*k)) + (3 << k) + 1;
            shellPass(data, comparator, 0, data.size(), gap);
        }

        shellPass(data, comparator, 0, data.size(), 3);
        Sort.binaryInsertion(data, comparator);
    }

    @Setup
    public void setUp() {
        for(int i = 0; i < array.length; i++) {
            array[i] = random.nextDouble();
        }
    }

    @Benchmark
    public void insertion() {
        Sort.binaryInsertion(array, Double::compareTo);
    }

    @Benchmark
    public void shell() {
        shellSort(Arrays.asList(array), Double::compareTo);
    }

    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
                .include(ShellAndInsertion.class.getSimpleName())
                .forks(1)
                .warmupTime(TimeValue.milliseconds(1))
                .warmupIterations(1)
                .measurementIterations(1000)
                .measurementTime(TimeValue.milliseconds(1))
                .mode(Mode.All)
                .timeUnit(TimeUnit.MICROSECONDS)
                .build();

        new Runner(options).run();
    }
}