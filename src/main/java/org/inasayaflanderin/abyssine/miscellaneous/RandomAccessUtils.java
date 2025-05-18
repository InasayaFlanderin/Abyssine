package org.inasayaflanderin.abyssine.miscellaneous;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * This class contains utility methods for arrays and lists.
 */
public class RandomAccessUtils {
    /**
     * Swaps the elements at the specified positions in the specified array.
     * @param array the array input
     * @param i the index of the first element
     * @param j the index of the second element
     * @param <D> the type of the elements in the array
     * @throws ArrayIndexOutOfBoundsException if the index is out of bounds
     */
    public static <D> void swap(D[] array, int i, int j) {
        var temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public static <D> void swap(List<D> list, int i, int j) {
        var temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    /**
     * Check if the array is sorted or not
     * @param array the array that needs to be checked
     * @param comparator the order of the elements
     * @param start the start index (inclusive) of the array
     * @param end the end index (exclusive) of the array
     * @param <D> the type of the elements in the array
     * @return true if the array is sorted, false otherwise
     */
    public static <D> boolean isSort(D[] array, Comparator<D> comparator, int start, int end) {
        return isSort(Arrays.asList(array), comparator, start, end);
    }

    public static <D> boolean isSort(List<D> list, Comparator<D> comparator, int start, int end) {
        return IntStream.range(start, end - 1).parallel()
                .noneMatch(i -> comparator.compare(list.get(i), list.get(i + 1)) > 0);
    }

    /**
     * Reversing the array with certain range
     * @param array the array that needs to be reversed
     * @param start the start index (inclusive) of the array
     * @param end the end index (exclusive) of the array
     * @param <D> the type of the elements in the array
     */
    public static <D> void reverse(D[] array, int start, int end) {
        reverse(Arrays.asList(array), start, end);
    }

    public static <D> void reverse(List<D> list, int start, int end) {
        var left = start;
        var right = end - 1;

        while(left < right) swap(list, left++, right--);
    }

    /**
     * Copy the elements in specified position with a certain range to another at specified position
     * @param src the array src of copy
     * @param srcPos the position that the copy start
     * @param dest the array destination where copied elements head to
     * @param destPos the position that the copied elements start
     * @param length the length that need to be copied
     * @param <D> the type of the elements in the array
     * @see System#arraycopy(Object, int, Object, int, int)
     */
    public static <D> void copy(D[] src, int srcPos, D[] dest, int destPos, int length) {
        System.arraycopy(src, srcPos, dest, destPos, length);
    }

    @SuppressWarnings("unchecked")
    public static <D> void copy(List<D> src, int srcPos, List<D> dest, int destPos, int length) {
        var temp = (D[]) new Object[length];

        for(var i = 0; i < length; i++) temp[i] = src.get(srcPos + i);
        for(var i = 0; i < length; i++) dest.set(destPos + i, temp[i]);
    }

    public static <D> void copy(D[] src, int srcPos, List<D> dest, int destPos, int length) {
        copy(Arrays.asList(src), srcPos, dest, destPos, length);
    }

    public static <D> void copy(List<D> src, int srcPos, D[] dest, int destPos, int length) {
        copy(src, srcPos, Arrays.asList(dest), destPos, length);
    }
}