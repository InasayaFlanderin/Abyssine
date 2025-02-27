package org.inasayaflanderin.abyssine.miscellaneous.sort;

import java.util.Random;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.isSort;

public class GrailTest {
    public static void main(String[] args) {
        Integer[] array = new Integer[1000000];
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt();
        }

        //GrailSort.grailCommonSort(array, Integer::compareTo);

        ParallelGrailSort<Integer> prs = new ParallelGrailSort<>();
        prs.runSort(array, array.length, Integer::compareTo);
        System.out.println("Sorted: " + isSort(array, Integer::compareTo));
    }
}