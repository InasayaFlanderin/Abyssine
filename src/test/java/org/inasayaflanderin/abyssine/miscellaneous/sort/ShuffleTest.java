package org.inasayaflanderin.abyssine.miscellaneous.sort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class ShuffleTest {
    int[] array = new int[100];
    ArrayList<Integer> list = new ArrayList<>(100);

    @BeforeEach
    void setUp() {
        for(int i = 0; i < 100; i++) {
            array[i] = i;
            list.set(i, i);
        }
    }

    @Test
    void normalShuffle() {
    }

    @Test
    void testNormalShuffle() {
    }

    @Test
    void FYShuffle() {
    }

    @Test
    void testFYShuffle() {
    }
}