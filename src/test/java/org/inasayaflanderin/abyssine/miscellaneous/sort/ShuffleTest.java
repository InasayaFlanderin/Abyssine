package org.inasayaflanderin.abyssine.miscellaneous.sort;

import org.inasayaflanderin.abyssine.miscellaneous.rng.MT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

class ShuffleTest {
    Integer[] array = new Integer[100];
    ArrayList<Integer> list = new ArrayList<>(100);

    @BeforeEach
    void setUp() {
        for(int i = 0; i < 100; i++) {
            array[i] = i;
            list.add(i, i);
        }
    }

    @Test
    void normalShuffleArray() {
        Shuffle.normalShuffle(array, new MT(100));
        System.out.println(Arrays.toString(array));
    }

    @Test
    void normalShuffleList() {
        Shuffle.normalShuffle(list, new MT(100));
        System.out.println(Arrays.toString(list.toArray()));
    }

    @Test
    void FYShuffleArray() {
        Shuffle.FYShuffle(array, new MT(100));
        System.out.println(Arrays.toString(array));
    }

    @Test
    void FYShuffleList() {
        Shuffle.FYShuffle(list, new MT(100));
        System.out.println(Arrays.toString(list.toArray()));
    }
}