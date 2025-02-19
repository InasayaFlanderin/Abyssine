package org.inasayaflanderin.abyssine.miscellaneous.sort;

import java.util.Comparator;
import java.util.Random;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.isSort;

public class RefactorTest {
    public static void main(String[] args) {
        Double[] array = new Double[1000000];
        Double[] array1 = new Double[100000];
        Double[] array2 = new Double[10000];
        Double[] array3 = new Double[1000];
        Double[] array4 = new Double[100];
        Double[] array5 = new Double[10];
        Double[] array6 = new Double[10000000];
        Double[] array7 = new Double[11111110];
        Random rng = new Random();

        for (int i = 0; i < array.length; i++) array[i] = rng.nextDouble();
        for (int i = 0; i < array1.length; i++) array1[i] = rng.nextDouble();
        for (int i = 0; i < array2.length; i++) array2[i] = rng.nextDouble();
        for (int i = 0; i < array3.length; i++) array3[i] = rng.nextDouble();
        for (int i = 0; i < array4.length; i++) array4[i] = rng.nextDouble();
        for (int i = 0; i < array5.length; i++) array5[i] = rng.nextDouble();
        for (int i = 0; i < array6.length; i++) array6[i] = rng.nextDouble();
        for (int i = 0; i < array7.length; i++) array7[i] = rng.nextDouble();

        WikiSorter.sort(array, Comparator.naturalOrder());
        WikiSorter.sort(array1, Comparator.naturalOrder());
        WikiSorter.sort(array2, Comparator.naturalOrder());
        WikiSorter.sort(array3, Comparator.naturalOrder());
        WikiSorter.sort(array4, Comparator.naturalOrder());
        WikiSorter.sort(array5, Comparator.naturalOrder());
        WikiSorter.sort(array6, Comparator.naturalOrder());
        WikiSorter.sort(array7, Comparator.naturalOrder());

        System.out.println(isSort(array, Comparator.naturalOrder()));
        System.out.println(isSort(array1, Comparator.naturalOrder()));
        System.out.println(isSort(array2, Comparator.naturalOrder()));
        System.out.println(isSort(array3, Comparator.naturalOrder()));
        System.out.println(isSort(array4, Comparator.naturalOrder()));
        System.out.println(isSort(array5, Comparator.naturalOrder()));
        System.out.println(isSort(array6, Comparator.naturalOrder()));
        System.out.println(isSort(array7, Comparator.naturalOrder()));
    }
}
