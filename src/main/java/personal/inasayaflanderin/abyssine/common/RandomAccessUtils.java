package personal.inasayaflanderin.abyssine.common;

/**
 *This class contains utility classes for objects which elements can be access at any given index like array or list
 **/

import java.util.List;
import java.util.Arrays;
import java.util.Comparator;

public final class RandomAccessUtils {
	/**
	 *Check {@link RandomAccessUtils#swap(List, int, int) swap} for more detail
	 **/
	public static <D> void swap(D[] array, int i, int j) {
		var temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	/**
	 *@param list array needed to swap
	 *@param i first index to be swapped
	 *@param j second index to be swapped
	 **/
	public static <D> void swap(List<D> list, int i, int j) {
		list.set(i, list.set(j, list.get(i)));
	}

	/**
	 *Check {@link RandomAccessUtils#isSort(List, Comparator, int, int)} for more detail
	 * */
	public static <D> boolean isSort(D[] array, Comparator<D> comparator, int start, int end) {
		return isSort(Arrays.asList(array), comparator, start, end);
	}

	/**
	 *@param list the list that need to be checked
	 *@param comparator comparator for the data
	 *@param start start index (inclusive)
	 *@param end end index (exclusive)
	 * */
	public static <D> boolean isSort(List<D> list, Comparator<D> comparator, int start, int end) {
		var isSorted = true;

		for(var i = start; i < end - 1; i++) if(comparator.compare(list.get(i), list.get(i + 1)) > 0) {
			isSorted = false;
			break;
		}

		return isSorted;
	}

	/**
	 *Check {@link System#arraycopy(array, int, array, int, int)} for more detail
	 * */
	public static <D> void copy(D[] src, int srcPos, D[] dest, int destPos, int length) {
		System.arraycopy(src, srcPos, dest, destPos, length);
	}

	/**
	 *Check {@link System#arraycopy(array, int, array, int, int)} for more detail
	 * */
	@SuppressWarnings("unchecked")
	public static <D> void copy(List<D> src, int srcPos, List<D> dest, int destPos, int length) {
		D[] temporaryStorage = (D[]) new Object[length];

		for(var i = 0; i < length; i++) temporaryStorage[i] = src.get(srcPos + i);
		for(var i = 0; i < length; i++) dest.set(destPos + i, temporaryStorage[i]);
	}

	/**
	 **Check {@link System#arraycopy(array, int, array, int, int)} for more detail
	 * */
	public static <D> void copy(List<D> src, int srcPos, D[] dest, int destPos, int length) {
		copy(src, srcPos, Arrays.asList(dest), destPos, length);
	}

	/**
	 **Check {@link System#arraycopy(array, int, array, int, int)} for more detail
	 * */
	public static <D> void copy(D[] src, int srcPos, List<D> dest, int destPos, int length) {
		copy(Arrays.asList(src), srcPos, dest, destPos, length);
	}
}
