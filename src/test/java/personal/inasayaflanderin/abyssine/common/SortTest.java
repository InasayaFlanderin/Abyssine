package personal.inasayaflanderin.abyssine.common;

import java.util.LinkedList;
import java.util.Comparator;

import net.jqwik.api.Property;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Combinators;
import static org.junit.jupiter.api.Assertions.*;

import personal.inasayaflanderin.abyssine.primitives.Pair;
import static personal.inasayaflanderin.abyssine.common.RandomAccessUtils.isSort;

public class SortTest {
	private static final Comparator<Double> comparator = Comparator.comparing(Pair::first, Double::compareTo);

	@Provide
	Arbitrary<Pair<Double, Integer>[]> generator() {
		return Arbitraries.intergers().array(Integer[].class).uniqueElements().flatMap(integerArray -> 
			Arbitraries.doubles().array(Double[].class).ofSize(integerArray.length).flatMap(doubleArray -> {
				Pair<Double, Integer>[] result = new Pair<Double, Integer>[integerArray.length];

				for(var i = 0; i < integerArray.length; i++) result[i] = new Pair<Double, Integer>(doubleArray[i], integerArray[i]);

				return result;
			})
		);
	}

	@Property(tries = 1000000)
	void selection(@ForAll("generator") Pair<Double, Integer>[] array) {
		var idChecker = new LinkedList<Integer>();

		for(var i = 0; i < array.length; i++) idChecker.add(array[i].second());

		Sort.selection(array, comparator, 0, array.length);

		var isSorted = isSort(array, comparator, 0, array.length);
		var uniqueRemain = true;

		for(var i = 0; i < array.length; i++) idChecker.remove(array[i].second());

		if(!idChecker.isEmpty()) uniqueRemain = false;

		assertTrue(isSorted && uniqueRemain, "Selection sort has problems");
	}
}
