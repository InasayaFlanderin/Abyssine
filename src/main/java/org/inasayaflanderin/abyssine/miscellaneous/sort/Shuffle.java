package org.inasayaflanderin.abyssine.miscellaneous.sort;

import org.inasayaflanderin.abyssine.miscellaneous.rng.RandomGenerators;

import java.util.Arrays;
import java.util.List;

public class Shuffle {
    public static <T> void normalShuffle(T[] array, RandomGenerators rng) {
        normalShuffle(Arrays.asList(array), rng);
    }
    
    public static <T> void normalShuffle(List<T> list, RandomGenerators rng) {
        for(int i = 0; i < list.size(); i++) SortHelper.swap(list, i, (int) (rng.next() * list.size()));
    }

    public static <T> void FYShuffle(T[] array, RandomGenerators rng) {
        FYShuffle(Arrays.asList(array), rng);
    }

    public static <T> void FYShuffle(List<T> list, RandomGenerators rng) {
        for(int i = list.size() - 1; i > 0; i--) SortHelper.swap(list, i, (int) (rng.next() * i));
    }
}
