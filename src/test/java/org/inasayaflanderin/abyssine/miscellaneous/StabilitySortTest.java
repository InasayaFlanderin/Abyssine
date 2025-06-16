package org.inasayaflanderin.abyssine.miscellaneous;

import net.jqwik.api.*;
import org.inasayaflanderin.abyssine.primitives.Pair;

import java.util.Comparator;
import java.util.HashMap;
import java.util.function.IntFunction;
import java.util.stream.Gatherer;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.isSort;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
* This class test stability of sorting algorithms
* */
public class StabilitySortTest {
    // Duplicator is the provider part, do not touch
    @Provide
    Arbitrary<Pair<Integer, Integer>[]> duplicator() {
        return Arbitraries.integers().between(0, 100).stream()
                .map(s -> s.gather(Gatherer.<Integer, HashMap<Integer, Integer>, Pair<Integer, Integer>>ofSequential(
                    HashMap::new,
                    (state, element, downstream) -> {
                        state.putIfAbsent(element, -1);
                        state.put(element, state.get(element) + 1);
                        return downstream.push(new Pair<>(element, state.get(element)));
                    }
                )).toArray((IntFunction<Pair<Integer, Integer>[]>) Pair[]::new));
    }

    @Property(tries = 1000000)
    void insertion(@ForAll("duplicator") Pair<Integer, Integer>[] array) {
        Sort.insertion(array, Comparator.comparing(Pair::first), 0, array.length);
        assertTrue(isSort(array, Comparator.comparing((Pair<Integer, Integer> i) -> i.first()).thenComparing(Pair::second), 0, array.length),"Array is not stable");
    }

    @Property(tries = 1000000)
    void bubble(@ForAll("duplicator") Pair<Integer, Integer>[] array) {
        Sort.bubble(array, Comparator.comparing(Pair::first), 0, array.length);
        assertTrue(isSort(array, Comparator.comparing((Pair<Integer, Integer> i) -> i.first()).thenComparing(Pair::second), 0, array.length),"Array is not stable");
    }

    @Property(tries = 1000000)
    void shaker(@ForAll("duplicator") Pair<Integer, Integer>[] array) {
        Sort.shaker(array, Comparator.comparing(Pair::first), 0, array.length);
        assertTrue(isSort(array, Comparator.comparing((Pair<Integer, Integer> i) -> i.first()).thenComparing(Pair::second), 0, array.length),"Array is not stable");
    }

    @Property(tries = 1000000)
    void mergeIterative(@ForAll("duplicator") Pair<Integer, Integer>[] array) {
        Sort.mergeIterative(array, Comparator.comparing(Pair::first), 0, array.length);
        assertTrue(isSort(array, Comparator.comparing((Pair<Integer, Integer> i) -> i.first()).thenComparing(Pair::second), 0, array.length),"Array is not stable");
    }

    @Property(tries = 1000000)
    void mergeRecursive(@ForAll("duplicator") Pair<Integer, Integer>[] array) {
        Sort.mergeRecursive(array, Comparator.comparing(Pair::first), 0, array.length);
        assertTrue(isSort(array, Comparator.comparing((Pair<Integer, Integer> i) -> i.first()).thenComparing(Pair::second), 0, array.length),"Array is not stable");
    }

    @Property(tries = 1000000)
    void mergeParallel(@ForAll("duplicator") Pair<Integer, Integer>[] array) throws InterruptedException {
        Sort.mergeParallel(array, Comparator.comparing(Pair::first), 0, array.length);
        assertTrue(isSort(array, Comparator.comparing((Pair<Integer, Integer> i) -> i.first()).thenComparing(Pair::second), 0, array.length),"Array is not stable");
    }
}