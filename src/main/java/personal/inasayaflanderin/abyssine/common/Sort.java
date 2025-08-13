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
		for(var i = start; i < start + (end - start + 1) >>> 1; i++) {
			var min = i;
			var max = i;

			for(int j = i + 1; j < end - i + start; j++) {
				if(comparator.compare(list.get(j), list.get(min)) < 0) min = j;
				if(comparator.compare(list.get(j), list.get(max)) > 0) max = j;
			}

			swap(list, min, i);

			if(max == i) max = min;

			swap(list, max, end - 1 - i + start);
		}
	}
}
