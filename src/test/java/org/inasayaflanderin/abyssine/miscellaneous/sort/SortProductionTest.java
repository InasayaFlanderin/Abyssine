package org.inasayaflanderin.abyssine.miscellaneous.sort;

import org.inasayaflanderin.abyssine.test.template.RepeatedParameterizedTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SortProductionTest {
    Random rng = new Random();
    Double[] array = new Double[1000];

    static <D> List<BiConsumer<D[], Comparator<D>>> initialSort() {
        return List.of(
                Sort::selection,
                Sort::doubleSelection,
                Sort::insertion,
                Sort::binaryInsertion,
                Sort::bubble,
                Sort::shaker,
                Sort::quick,
                Sort::merge,
                Sort::heap,
                Sort::comb,
                Sort::shell,
                Sort::cycle,
                Sort::patience,
                Sort::exchange,
                Sort::oddEven,
                Sort::circle,
                Sort::mergeInsertion,
                Sort::mergeInsertion2,
                Sort::tournament,
                Sort::tree,
                Sort::gnome,
                Sort::strand,
                Sort::bitonicNetwork,
                Sort::oddEvenNetwork,
                Sort::quickLL,
                Sort::dualPivotQuick,
                Sort::intro,
                Sort::tim,
                Sort::weave,
                Sort::smooth,
                Sort::cartesian,
                Sort::sqrt
        );
    }

    @BeforeEach
    void setUp() {
        for (int i = 0; i < array.length; i++) array[i] = rng.nextDouble();
    }

    @RepeatedParameterizedTest(1000)
    @MethodSource("initialSort")
    void test(BiConsumer<Double[], Comparator<Double>> sort) {
        sort.accept(array, Comparator.naturalOrder());

        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }
}