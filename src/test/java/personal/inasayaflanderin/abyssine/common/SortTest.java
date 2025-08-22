package personal.inasayaflanderin.abyssine.common;

import java.util.LinkedList;
import java.util.Comparator;
import java.lang.reflect.Array;
import java.util.function.Consumer;

import net.jqwik.api.*;
import static org.junit.jupiter.api.Assertions.*;

import personal.inasayaflanderin.abyssine.primitives.Pair;
import static personal.inasayaflanderin.abyssine.common.RandomAccessUtils.isSort;

/*
 *This class test the correctness of sorting algorithms by checking two things:
 *- The ability to sort the algorithms in specific order
 *- The ability not to replace any elements by ensuring they're hold the correct unique Id ( which is use by uniqueElements of Integer )
 * First, it will collect all unique Id into a list, then after sorted, they will eliminate the existing Id
 * If any Id is replaced, then it will remain in the list while others is gone
 * */
public class SortTest {
	private static final Comparator<Pair<Double, Integer>> comparator = Comparator.comparing(Pair::first, Double::compareTo);

	@Provide
	Arbitrary<Pair<Double, Integer>[]> generator() {
		return Arbitraries.integers().array(Integer[].class).uniqueElements().flatMap(integerArray -> 
			Arbitraries.doubles().array(Double[].class).ofSize(integerArray.length).map(doubleArray -> {
				var result = (Pair<Double, Integer>[]) Array.newInstance(Pair.class, integerArray.length);

				for(var i = 0; i < integerArray.length; i++) result[i] = new Pair<Double, Integer>(doubleArray[i], integerArray[i]);

				return result;
			})
		);
	}

	boolean verify(Pair<Double, Integer>[] array, Consumer<Pair<Double, Integer>[]> function) {
		var idChecker = new LinkedList<Integer>();

		for(var i = 0; i < array.length; i++) idChecker.add(array[i].second());

		function.accept(array);
		var isSorted = isSort(array, comparator, 0, array.length);
		var uniqueRemain = true;

		for(var i = 0; i < array.length; i++) idChecker.remove(array[i].second());

		if(!idChecker.isEmpty()) uniqueRemain = false;

		return isSorted && uniqueRemain;
	}

	@Property(tries = 1000000)
	void selection(@ForAll("generator") Pair<Double, Integer>[] array) {
		assertTrue(verify(array, arr -> Sort.selection(arr, comparator, 0, array.length)), "Selection sort has problems");
	}

	@Property(tries = 1000000)
	void doubleSelection(@ForAll("generator") Pair<Double, Integer>[] array) {
		assertTrue(verify(array, arr -> Sort.doubleSelection(arr, comparator, 0, array.length)), "Double selection sort has problems");
	}

	@Property(tries = 1000000)
	void insertion(@ForAll("generator") Pair<Double, Integer>[] array) {
		assertTrue(verify(array, arr -> Sort.insertion(arr, comparator, 0, array.length)), "Double selection sort has problems");
	}

	@Property(tries = 1000000)
	void binaryInsertion(@ForAll("generator") Pair<Double, Integer>[] array) {
		assertTrue(verify(array, arr -> Sort.binaryInsertion(arr, comparator, 0, array.length)), "Double selection sort has problems");
	}
}
