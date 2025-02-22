package org.inasayaflanderin.abyssine.miscellaneous.sort;

import lombok.extern.slf4j.Slf4j;
import net.jqwik.api.*;
import org.inasayaflanderin.abyssine.exception.ParallelExecutionException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.isSort;
import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.swap;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class Po2LSortTest {
    static ExecutorService executors = Executors.newVirtualThreadPerTaskExecutor();

    private static <D> void bitonicNetworkExecute(List<D> data, Comparator<D> comparator, int left, int length, Function<Integer, Integer> getPowerOfTwoLess) {
        if(length > 1) {
            var mid = length / 2;
            List<Callable<Void>> tasks = List.of(
                    () -> {
                        bitonicNetworkExecute(data, comparator.reversed(), left, mid, getPowerOfTwoLess);

                        return null;
                    },
                    () -> {
                        bitonicNetworkExecute(data, comparator, left + mid, length - mid, getPowerOfTwoLess);

                        return null;
                    }
            );

            try {
                executors.invokeAll(tasks);
            } catch (InterruptedException e) {
                log.error("Thread got interrupted!");

                throw new ParallelExecutionException(e.getMessage());
            }

            bitonicMerge(data, comparator, left, length, getPowerOfTwoLess);
        }
    }

    private static <D> void bitonicMerge(List<D> data, Comparator<D> comparator, int left, int length, Function<Integer, Integer> getPowerOfTwoLess) {
        if(length > 1) {
            var mid = getPowerOfTwoLess.apply(length);

            for(int i = left; i < left + length - mid; i++) if(comparator.compare(data.get(i), data.get(i + mid)) > 0) swap(data, i, i + mid);

            List<Callable<Void>> tasks = List.of(
                    () -> {
                        System.out.println("Left: " + left + " Mid: " + mid);

                        bitonicMerge(data, comparator, left, mid, getPowerOfTwoLess);

                        return null;
                    },
                    () -> {
                        System.out.println("Left: " + (left + mid) + " Length: " + (length - mid));

                        bitonicMerge(data, comparator, left + mid, length - mid, getPowerOfTwoLess);

                        return null;
                    }
            );

            try {
                executors.invokeAll(tasks);
            } catch (InterruptedException e) {
                log.error("Thread got interrupted!");

                throw new ParallelExecutionException(e.getMessage());
            }
        }
    }

    static int po2l1(int value) {
        var result = value;

        result |= result >> 1;
        result |= result >> 2;
        result |= result >> 4;
        result |= result >> 8;
        result |= result >> 16;
        result -= result >> 1;

        return result;
    }

    static int po2l2(int value) {
        var mid = 1;

        while(mid > 0 && mid < value) mid <<= 1;

        return mid >>> 1;
    }

    @Property(tries = 100) // cant use
    void testpo2l1(@ForAll("provider") Integer[] values) {
        bitonicNetworkExecute(Arrays.asList(values), Integer::compareTo, 0, values.length, Po2LSortTest::po2l1);

        assertTrue(isSort(values, Integer::compareTo));
    }

    @Property(tries = 100)
    void testpo2l2(@ForAll("provider") Integer[] values) {
        bitonicNetworkExecute(Arrays.asList(values), Integer::compareTo, 0, values.length, Po2LSortTest::po2l2);

        assertTrue(isSort(values, Integer::compareTo));
    }

    @Provide
    Arbitrary<Integer[]> provider() {
        return Arbitraries.integers().list().ofMaxSize(10000).filter(list -> list.size() > 10).map(list -> list.toArray(new Integer[0]));
    }
}