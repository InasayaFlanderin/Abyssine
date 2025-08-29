package personal.inasayaflanderin.abyssine.common;

import java.util.HashMap;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.stream.Gatherer;

import net.jqwik.api.*;
import static org.junit.jupiter.api.Assertions.*;

import personal.inasayaflanderin.abyssine.primitives.Pair;
import static personal.inasayaflanderin.abyssine.common.RandomAccessUtils.isSort;

/*
 *This class test the stability of sorting algorithms by checking if the order of appearance of the same value elements unchanged
 *Checking the order alone is not possible as:
 *- The order of different value elements will be changed upon sorting
 *- The order of same value elements cannot be checked since it will get lower when it move to different value
 *That mean, to check it, we need to check on priority, the value first, then to the order of appearance 
 * */
public class StableSortTest {
	private static final Comparator<Pair<Integer, Integer>> sortingComparator = Comparator.comparing(Pair::first, Integer::compareTo);
	private static final Comparator<Pair<Integer, Integer>> checkingComparator = Comparator.<Pair<Integer,Integer>, Integer>comparing(Pair::first, Integer::compareTo)
												.thenComparing(Pair::second, Integer::compareTo);

	@Provide
	Arbitrary<Pair<Integer, Integer>[]> generator() {
		return Arbitraries.integers().array(Integer[].class).map(array -> {
			return Stream.of(array).gather(Gatherer.<Integer, HashMap, Pair<Integer, Integer>>ofSequential(
				HashMap::new,
				(state, item, downstream) -> {
					if(!state.containsKey(item)) state.put(item, 0);

					downstream.push(new Pair<Integer, Integer>(item, (Integer) state.get(item)));
					state.put(item, (Integer) state.get(item) + 1);

					return true;
				}
			)).toArray(Pair[]::new);
		});
	}

	@Property(tries = 1000000)
	void insertion(@ForAll("generator") Pair<Integer, Integer>[] array) {
		Sort.insertion(array, sortingComparator, 0, array.length);
		assertTrue(isSort(array, checkingComparator, 0, array.length));
	}

	@Property(tries = 1000000)
	void binaryInsertion(@ForAll("generator") Pair<Integer, Integer>[] array) {
		Sort.binaryInsertion(array, sortingComparator, 0, array.length);
		assertTrue(isSort(array, checkingComparator, 0, array.length));
	}

	@Property(tries = 1000000)
	void bubble(@ForAll("generator") Pair<Integer, Integer>[] array) {
		Sort.bubble(array, sortingComparator, 0, array.length);
		assertTrue(isSort(array, checkingComparator, 0, array.length));
	}

	@Property(tries = 1000000)
	void shaker(@ForAll("generator") Pair<Integer, Integer>[] array) {
		Sort.shaker(array, sortingComparator, 0, array.length);
		assertTrue(isSort(array, checkingComparator, 0, array.length));
	}
}
