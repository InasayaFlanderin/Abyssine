package org.inasayaflanderin.abyssine.miscellaneous;

import net.jqwik.api.*;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.isSort;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SortTest {
    @Property(tries = 1000000)
    void selection(@ForAll Double[] array) {
        Sort.selection(array, Double::compareTo, 0, array.length);

        assertTrue(isSort(array, Double::compareTo, 0, array.length), "Array is not sorted");
    }

    @Property(tries = 1000000)
    void doubleSelection(@ForAll Double[] array) {
        Sort.doubleSelection(array, Double::compareTo, 0, array.length);

        assertTrue(isSort(array, Double::compareTo, 0, array.length), "Array is not sorted");
    }

    @Property(tries = 1000000)
    void insertion(@ForAll Double[] array) {
        Sort.insertion(array, Double::compareTo, 0, array.length);

        assertTrue(isSort(array, Double::compareTo, 0, array.length), "Array is not sorted");
    }

    @Property(tries = 1000000)
    void binaryInsertion(@ForAll Double[] array) {
        Sort.binaryInsertion(array, Double::compareTo, 0, array.length);

        assertTrue(isSort(array, Double::compareTo, 0, array.length), "Array is not sorted");
    }

    @Property(tries = 1000000)
    void bubble(@ForAll Double[] array) {
        Sort.bubble(array, Double::compareTo, 0, array.length);

        assertTrue(isSort(array, Double::compareTo, 0, array.length), "Array is not sorted");
    }

    @Property(tries = 1000000)
    void shaker(@ForAll Double[] array) {
        Sort.shaker(array, Double::compareTo, 0, array.length);

        assertTrue(isSort(array, Double::compareTo, 0, array.length), "Array is not sorted");
    }

    @Property(tries = 1000000)
    void quickIterative(@ForAll Double[] array) {
        Sort.quickIterative(array, Double::compareTo, 0, array.length);

        assertTrue(isSort(array, Double::compareTo, 0, array.length), "Array is not sorted");
    }

    @Property(tries = 1000000)
    void quickRecursive(@ForAll Double[] array) {
        Sort.quickRecursive(array, Double::compareTo, 0, array.length);

        assertTrue(isSort(array, Double::compareTo, 0, array.length), "Array is not sorted");
    }

    @Property(tries = 1000000)
    void quickParallel(@ForAll Double[] array) throws InterruptedException {
        Sort.quickParallel(array, Double::compareTo, 0, array.length);

        assertTrue(isSort(array, Double::compareTo, 0, array.length), "Array is not sorted");
    }

    @Property(tries = 1000000)
    void mergeIterative(@ForAll Double[] array) {
        Sort.mergeIterative(array, Double::compareTo, 0, array.length);

        assertTrue(isSort(array, Double::compareTo, 0, array.length), "Array is not sorted");
    }

    @Property(tries = 1000000)
    void mergeRecursive(@ForAll Double[] array) {
        Sort.mergeRecursive(array, Double::compareTo, 0, array.length);

        assertTrue(isSort(array, Double::compareTo, 0, array.length), "Array is not sorted");
    }

    @Property(tries = 1000000)
    void mergeParallel(@ForAll Double[] array) throws InterruptedException {
        Sort.mergeParallel(array, Double::compareTo, 0, array.length);

        assertTrue(isSort(array, Double::compareTo, 0, array.length), "Array is not sorted");
    }

    @Property(tries = 1000000)
    void heap(@ForAll Double[] array) {
        Sort.heap(array, Double::compareTo, 0, array.length);

        assertTrue(isSort(array, Double::compareTo, 0, array.length), "Array is not sorted");
    }

    @Property(tries = 1000000)
    void comb(@ForAll Double[] array) {
        Sort.comb(array, Double::compareTo, 0, array.length);

        assertTrue(isSort(array, Double::compareTo, 0, array.length), "Array is not sorted");
    }
}