package org.inasayaflanderin.abyssine.miscellaneous;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.copy;
import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.swap;

//TODO: Replace binarySearch with faster search algorithm with same result

/**
 * <p>This class hold of sorting algorithms that Abyssine supported, it doesn't contain non-comparable sorting algorithms such as Radix sort</p>
 * <p>The input as follows:</p>
 * <p>{@code array} the array that needs to be sorted</p>
 * <p>{@code list} the list that needs to be sorted</p>
 * <p>{@code comparator} the order of the elements</p>
 * <p>{@code start} the start index (inclusive) of the array</p>
 * <p>{@code end} the end index (exclusive) of the array</p>
 * <p>{@code <D>} the type of the elements in the array</p>
 */
public class Sort {
    public static <D> void selection(D[] array, Comparator<D> comparator, int start, int end) {
        selection(Arrays.asList(array), comparator, start, end);
    }

    public static <D> void selection(List<D> list, Comparator<D> comparator, int start, int end) {
        for (var i = start; i < end - 1; i++) {
            var minIndex = i;

            for (var j = i + 1; j < end; j++) if (comparator.compare(list.get(j), list.get(minIndex)) < 0) minIndex = j;

            swap(list, i, minIndex);
        }
    }

    public static <D> void doubleSelection(D[] array, Comparator<D> comparator, int start, int end) {
        doubleSelection(Arrays.asList(array), comparator, start, end);
    }

    public static <D> void doubleSelection(List<D> list, Comparator<D> comparator, int start, int end) {
        for (var i = start; i < (start + end) >>> 1; i++) {
            var minIndex = i;
            var maxIndex = i;

            for (var j = i + 1; j < end - i; j++) {
                if (comparator.compare(list.get(j), list.get(minIndex)) < 0) minIndex = j;
                if (comparator.compare(list.get(j), list.get(maxIndex)) > 0) maxIndex = j;
            }

            swap(list, i, minIndex);

            if(maxIndex == i) maxIndex = minIndex;

            swap(list, end - i - 1, maxIndex);
        }
    }

    public static <D> void insertion(D[] array, Comparator<D> comparator, int start, int end) {
        insertion(Arrays.asList(array), comparator, start, end);
    }

    public static <D> void insertion(List<D> list, Comparator<D> comparator, int start, int end) {
        for (var i = start + 1; i < end; i++) {
            var key = list.get(i);
            var j = i - 1;

            while (j >= start && comparator.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }

            list.set(j + 1, key);
        }
    }

    public static <D> void binaryInsertion(D[] array, Comparator<D> comparator, int start, int end) {
        binaryInsertion(Arrays.asList(array), comparator, start, end);
    }

    public static <D> void binaryInsertion(List<D> list, Comparator<D> comparator, int start, int end) {
        for(var i = start + 1; i < end; i++) {
            var key = list.get(i);

            int j = Math.abs(Search.binarySearch(list, comparator, key, start, i) + 1);

            copy(list, j, list, j + 1, i - j);

            list.set(j, key);
        }
    }

    public static <D> void bubble(D[] array, Comparator<D> comparator, int start, int end) {
        bubble(Arrays.asList(array), comparator, start, end);
    }

    public static <D> void bubble(List<D> list, Comparator<D> comparator, int start, int end) {
        for (var i = start; i < end - 1; i++) {
            boolean swapped = false;

            for (var j = start; j < end - i - 1; j++) {
                if (comparator.compare(list.get(j), list.get(j + 1)) > 0) {
                    swap(list, j, j + 1);
                    swapped = true;
                }
            }

            if (!swapped) break;
        }
    }

    public static <D> void shaker(D[] array, Comparator<D> comparator, int start, int end) {
        shaker(Arrays.asList(array), comparator, start, end);
    }

    public static <D> void shaker(List<D> list, Comparator<D> comparator, int start, int end) {
        boolean swapped = true;

        while (swapped) {
            swapped = false;

            for (var i = start; i < end - 1; i++) {
                if (comparator.compare(list.get(i), list.get(i + 1)) > 0) {
                    swap(list, i, i + 1);
                    swapped = true;
                }
            }

            if (!swapped) break;

            swapped = false;

            for (var i = end - 2; i >= start; i--) {
                if (comparator.compare(list.get(i), list.get(i + 1)) > 0) {
                    swap(list, i, i + 1);
                    swapped = true;
                }
            }
        }
    }
}