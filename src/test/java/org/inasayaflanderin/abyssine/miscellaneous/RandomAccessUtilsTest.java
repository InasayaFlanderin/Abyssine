package org.inasayaflanderin.abyssine.miscellaneous;

import org.junit.jupiter.api.RepeatedTest;

import java.util.Comparator;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomAccessUtilsTest {
    Double[] array = new Double[10000];
    Random rng = new Random();

    @RepeatedTest(1000000)
    public void isSortTestFalse() {
        for(int i = 0; i < array.length; i++) array[i] = rng.nextDouble();

        assertFalse(RandomAccessUtils.isSort(array, Double::compare, 0, array.length));
    }

    @RepeatedTest(1000000)
    public void isSortTestTrue() {
        double startPoint = rng.nextDouble();

        for(int i = 0; i < array.length; i++) array[i] = startPoint + i;

        assertTrue(RandomAccessUtils.isSort(array, Double::compare, 0, array.length));
    }

    @RepeatedTest(1000000)
    public void isReverse() {
        double startPoint = rng.nextDouble();
        Comparator<Double> comparator = Double::compare;

        for(int i = 0; i < array.length; i++) array[i] = startPoint + i;

        RandomAccessUtils.reverse(array, 0, array.length);

        assertTrue(RandomAccessUtils.isSort(array, comparator.reversed(), 0, array.length));
    }
}