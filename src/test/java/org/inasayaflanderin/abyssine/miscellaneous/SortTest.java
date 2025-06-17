package org.inasayaflanderin.abyssine.miscellaneous;

import net.jqwik.api.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class SortTest {
    @Property(tries = 1000000)
    void selection(@ForAll Double[] array) {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.selection(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }

    @Property(tries = 1000000)
    void doubleSelection(@ForAll Double[] array) {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.doubleSelection(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }

    @Property(tries = 1000000)
    void insertion(@ForAll Double[] array) {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.insertion(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }

    @Property(tries = 1000000)
    void binaryInsertion(@ForAll Double[] array) {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.binaryInsertion(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }

    @Property(tries = 1000000)
    void bubble(@ForAll Double[] array) {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.bubble(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }

    @Property(tries = 1000000)
    void shaker(@ForAll Double[] array) {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.shaker(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }

    @Property(tries = 1000000)
    void quickIterative(@ForAll Double[] array) {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.quickIterative(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }

    @Property(tries = 1000000)
    void quickRecursive(@ForAll Double[] array) {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.quickRecursive(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }

    @Property(tries = 1000000)
    void quickParallel(@ForAll Double[] array) throws InterruptedException {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.quickParallel(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }

    @Property(tries = 1000000)
    void mergeIterative(@ForAll Double[] array) {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.mergeIterative(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }

    @Property(tries = 1000000)
    void mergeRecursive(@ForAll Double[] array) {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.mergeRecursive(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }

    @Property(tries = 1000000)
    void mergeParallel(@ForAll Double[] array) throws InterruptedException {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.mergeParallel(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }

    @Property(tries = 1000000)
    void heap(@ForAll Double[] array) {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.heap(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }

    @Property(tries = 1000000)
    void comb(@ForAll Double[] array) {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.comb(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }

    @Property(tries = 1000000)
    void shell(@ForAll Double[] array) {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.shell(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }

    @Property(tries = 1000000)
    void cycle(@ForAll Double[] array) {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.cycle(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }

    @Property(tries = 1000000)
    void patience(@ForAll Double[] array) {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.patience(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }

    @Property(tries = 1000000)
    void exchange(@ForAll Double[] array) {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.exchange(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }

    @Property(tries = 1000000)
    void oddEven(@ForAll Double[] array) {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.oddEven(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }

    @Property(tries = 1000000)
    void circleRecursive(@ForAll Double[] array) {
        var copy = Arrays.copyOf(array, array.length);
        Arrays.sort(copy, Double::compareTo);
        Sort.circleRecursive(array, Double::compareTo, 0, array.length);
        assertArrayEquals(array, copy, "Array is not sorted");
    }
}