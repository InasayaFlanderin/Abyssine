package org.inasayaflanderin.abyssine.miscellaneous.sort;

import java.util.Comparator;
import java.util.Random;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.isSort;

public class RefactorTest {
    public static void main(String[] args) {
        Double[] array = new Double[1000000];
        Random rng = new Random();

        for (int i = 0; i < array.length; i++) {
            array[i] = rng.nextDouble();
        }

        WikiSorter.sort(array, Comparator.naturalOrder());

        System.out.println(isSort(array, Comparator.naturalOrder()));
    }
}
