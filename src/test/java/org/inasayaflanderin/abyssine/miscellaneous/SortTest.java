package org.inasayaflanderin.abyssine.miscellaneous;

import net.jqwik.api.*;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.isSort;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SortTest {
    @Property(tries = 1000000)
    void selectionSortTest(@ForAll Double[] array) {
        Sort.selection(array, Double::compareTo, 0, array.length);

        assertTrue(isSort(array, Double::compareTo, 0, array.length), "Array is not sorted");
    }

    @Property(tries = 1000000)
    void doubleSelectionSortTest(@ForAll Double[] array) {
        Sort.doubleSelection(array, Double::compareTo, 0, array.length);

        assertTrue(isSort(array, Double::compareTo, 0, array.length), "Array is not sorted");
    }

    @Property(tries = 1000000)
    void insertionSortTest(@ForAll Double[] array) {
        Sort.insertion(array, Double::compareTo, 0, array.length);

        assertTrue(isSort(array, Double::compareTo, 0, array.length), "Array is not sorted");
    }

    @Property(tries = 1000000)
    void binaryInsertionSortTest(@ForAll Double[] array) {
        Sort.binaryInsertion(array, Double::compareTo, 0, array.length);

        assertTrue(isSort(array, Double::compareTo, 0, array.length), "Array is not sorted");
    }
}