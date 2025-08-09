package personal.inasayaflanderin.abyssine.common;

/**
 *This class contains utility classes for objects which elements can be access at any given index like array or list
 **/

import java.util.List;
import java.util.Arrays;
import java.util.Comparator;

public final class RandomAccessUtils {
	/**
	 *Check {@link #swap(List, int, int) swap} for more detail
	 **/
	public static <D> void swap(D[] array, int i, int j) {
		var temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	/**
	 *@param array array needed to swap
	 *@param i first index to be swapped
	 *@param j second index to be swapped
	 **/
	public static <D> void swap(List<D> list, int i, int j) {
		list.set(i, list.set(j, list.get(i)));
	}

	public static <D> boolean isSort(D[] array, Comparator<D> comparator, int start, int end) {
		return isSort(Arrays.asList(array), comparator, start, end);
	}

	public static <D> boolean isSort(List<D> list, Comparator<D> comparator, int start, int end) {
		var isSorted = true;

		for(var i = start; i < end - 1; i++) if(comparator.compare(list.get(i), list.get(i + 1)) > 0) {
			isSorted = false;
			break;
		}

		return isSorted;
	}
}
