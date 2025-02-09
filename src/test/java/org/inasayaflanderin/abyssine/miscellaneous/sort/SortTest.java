package org.inasayaflanderin.abyssine.miscellaneous.sort;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class SortTest {
    Random rng = new Random();
    Double[] array;

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
                //ComparableSort::wiki
                //ComparableSort::grail
        );
    }

    @BeforeEach
    void setUp() {
        array = new Double[1000];

        IntStream.range(0, array.length).forEach(i -> array[i] = rng.nextDouble());
    }

    @ParameterizedTest
    @MethodSource("initialSort")
    public void testSort(BiConsumer<Double[], Comparator<Double>> sort) {
        assertAll(
                () -> {
                    sort.accept(array, Comparator.naturalOrder());

                    assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()), Arrays.toString(array));
                },

                () -> {
                    sort.accept(array, Comparator.reverseOrder());

                    assertTrue(RandomAccessUtils.isSort(array, Comparator.reverseOrder()));
                }
        );
    }
}