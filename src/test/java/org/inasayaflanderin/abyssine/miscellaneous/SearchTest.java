package org.inasayaflanderin.abyssine.miscellaneous;

import net.jqwik.api.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SearchTest {
    @Provide
    Arbitrary<Tuple.Tuple2<Integer[], Integer>> arrayWithValidIndex() {
        return Arbitraries.integers().array(Integer[].class)
                .ofMinSize(1).ofMaxSize(100)
                .map(array -> {
                    Integer[] uniqueArray = java.util.Arrays.stream(array)
                            .distinct()
                            .toArray(Integer[]::new);

                    int length = uniqueArray.length;
                    int validIndex = Arbitraries.integers().between(0, length - 1).sample();

                    return Tuple.of(uniqueArray, validIndex);
                });
    }

    @Property(tries = 1000000)
    void linearSearchTest(@ForAll("arrayWithValidIndex") Tuple.Tuple2<Integer[], Integer> tuple) {
        var array = tuple.get1();
        var index = tuple.get2();

        int result = Search.linearSearch(array, Integer::compareTo, array[index], 0, array.length);

        assertEquals(index, result);
    }

    @Property(tries = 1000000)
    void binarySearchTest(@ForAll("arrayWithValidIndex") Tuple.Tuple2<Integer[], Integer> tuple) {
        var array = tuple.get1();
        var index = tuple.get2();

        Arrays.sort(array, Integer::compareTo);

        int result = Search.binarySearch(array, Integer::compareTo, array[index], 0, array.length);

        assertEquals(index, result);
    }

    @Property(tries = 1000000)
    void ternarySearchTest(@ForAll("arrayWithValidIndex") Tuple.Tuple2<Integer[], Integer> tuple) {
        var array = tuple.get1();
        var index = tuple.get2();

        Arrays.sort(array, Integer::compareTo);

        int result = Search.ternarySearch(array, Integer::compareTo, array[index], 0, array.length);

        assertEquals(index, result);
    }
}