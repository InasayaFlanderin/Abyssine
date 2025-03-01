package org.inasayaflanderin.abyssine.miscellaneous.sort;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import java.util.Random;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.isSort;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GrailTest {
    public static void main(String[] args) {
        Double[] array = new Double[1000000];
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextDouble();
        }

        ParallelGrailSort<Double> prs = new ParallelGrailSort<>();
        prs.runSort(array, Double::compareTo);
        System.out.println("Sorted: " + isSort(array, Double::compareTo));
    }

    @Property(tries = 1000)
    public void testGrailSort(@ForAll Double[] array) {
        GrailSort.grailCommonSort(array, Double::compareTo);
        assertTrue(isSort(array, Double::compareTo));
    }
}