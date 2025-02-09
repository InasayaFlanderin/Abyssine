package org.inasayaflanderin.abyssine.miscellaneous;

import org.inasayaflanderin.abyssine.miscellaneous.rng.MT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class RandomAccessUtilsTest {
    Integer[] array = new Integer[100];
    ArrayList<Integer> list = new ArrayList<>(100);


    @BeforeEach
    void setUp() {
        for(int i = 0; i < 100; i++) {
            array[i] = i;
            list.add(i);
        }
    }

    @Test
    void isSort() {
        for(int i = 0; i < 100; i++) {
            array[i] = i;
        }

        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Test
    void isSort2() {
        for(int i = 0; i < 100; i++) {
            array[i] = 100 - i;
        }
        assertTrue(RandomAccessUtils.isSort(array, Comparator.reverseOrder()));
    }

    @Test
    void flip() {
        Integer[] a = new Integer[100];
        Integer[] b = new Integer[100];

        for(int i = 0; i < 100; i++) {
            a[i] = i;
            b[i] = 99 - i;
        }

        RandomAccessUtils.flip(a, 0, 100);

        assertArrayEquals(a, b);
    }

    @Test
    void shuffleArray() {
        RandomAccessUtils.shuffle(array, new MT(100));
        assertFalse(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Test
    void shuffleList() {
        RandomAccessUtils.shuffle(list, new MT(100));
        assertFalse(RandomAccessUtils.isSort(list, Comparator.naturalOrder()));
    }
}