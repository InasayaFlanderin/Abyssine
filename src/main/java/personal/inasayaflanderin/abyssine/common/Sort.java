package personal.inasayaflanderin.abyssine.common;

/**
 * Provide various kinds of sorting algorithms for using and experimenting which is very flexible for any objects
 * <p>start parametter is inclusive while end is exclusive</p>
 * */

import java.util.List;
import java.util.Arrays;
import java.util.Comparator;

import static personal.inasayaflanderin.abyssine.common.RandomAccessUtils.*;

//NOTE: passing array through Arrays.asList will cause extra overhead but prevent reimplementation
public final class Sort {
	public static <D> void selection(D[] array, Comparator<D> comparator, int start, int end) {
		selection(Arrays.asList(array), comparator, start, end);
	}

	public static <D> void selection(List<D> list, Comparator<D> comparator, int start, int end) {
		for(var i = start; i < end; i++) {
			var min = i;

			for(int j = i + 1; j < list.size(); j++) if(comparator.compare(list.get(j), list.get(min)) < 0) min = j;

			swap(list, i, min);
		}
	}

	public static <D> void doubleSelection(D[] array, Comparator<D> comparator, int start, int end) {
		doubleSelection(Arrays.asList(array), comparator, start, end);
	}

	public static <D> void doubleSelection(List<D> list, Comparator<D> comparator, int start, int end) {
		start--;

		while(++start < --end) {
			var min = start;
			var max = start;

			for(int i = start + 1; i <= end; i++) {
				if(comparator.compare(list.get(i), list.get(min)) < 0) min = i;
				if(comparator.compare(list.get(i), list.get(max)) > 0) max = i;
			}

			swap(list, min, start);

			if(max == start) max = min;

			swap(list, max, end);
		}
	}

	public static <D> void insertion(D[] array, Comparator<D> comparator, int start, int end) {
		insertion(Arrays.asList(array), comparator, start, end);
	}

	public static <D> void insertion(List<D> list, Comparator<D> comparator, int start, int end) {
		for(var i = start + 1; i < end; i++) {
			var datum = list.get(i);
			var j = i;

			while(j > start && comparator.compare(list.get(j - 1), datum) > 0) {
				list.set(j, list.get(j - 1));
				j--;
			}

			list.set(j, datum);
		}
	}

	public static <D> void binaryInsertion(D[] array, Comparator<D> comparator, int start, int end) {
		binaryInsertion(Arrays.asList(array), comparator, start, end);
	}

	public static <D> void binaryInsertion(List<D> list, Comparator<D> comparator, int start, int end) {
		for(var i = start + 1; i < end; i++) {
			var datum = list.get(i);
			var endSearch = Math.abs(binarySearch(list, comparator, datum, start, i) + 1);

			copy(list, endSearch, list, endSearch + 1, i - endSearch);
			list.set(endSearch, datum);
		}
	}

	private static <D> int binarySearch(List<D> list, Comparator<D> comparator, D key, int start, int end) {
		while(start < end) {
			var mid = start + ((end - start) >>> 1);

			if(comparator.compare(key, list.get(mid)) == 0) return mid;
			else if(comparator.compare(key, list.get(mid)) < 0) end = mid;
			else start = mid + 1;
		}

		return -(start+1);
	}
}
