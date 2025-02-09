package org.inasayaflanderin.abyssine.miscellaneous.sort;

import org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ImpracticalSortTest {
    Double[] array;
    Random rng = new Random();

    static <D> Stream<BiConsumer<D[], Comparator<D>>> initialImpracticalSortTest() {
        return Stream.of(
                ImpracticalSort::pancake,
                ImpracticalSort::stooge,
                ImpracticalSort::slow
        );
    }

    @BeforeEach
    public void setUp() {
        array = new Double[Math.abs(rng.nextInt(100))];

        Arrays.fill(array, rng.nextDouble());
    }

    @ParameterizedTest
    @MethodSource("initialImpracticalSortTest")
    public void test(BiConsumer<Double[], Comparator<Double>> sort) {
        assertAll(
            () -> {
                sort.accept(array, Comparator.naturalOrder());
                assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
            },

            () -> {
                sort.accept(array, Comparator.reverseOrder());
                assertTrue(RandomAccessUtils.isSort(array, Comparator.reverseOrder()));
            }
        );
    }
}