package org.inasayaflanderin.abyssine.miscellaneous;

import java.util.*;

//TODO: Replace with our own math implementation
//TODO: Test from binarySearch after doing Sort algorithm
public class Search {
    /**
     * @param array the array that will search in
     * @param comparator the order of the elements
     * @param target the element that needs to be searched
     * @param start the start index (inclusive) of the array
     * @param end the end index (exclusive) of the array
     * @param <D> the type of the elements in the array
     * @return the index of the element if found, -1 if not found
     */
    public static <D> int linearSearch(D[] array, Comparator<D> comparator, D target, int start, int end) {
        return linearSearch(Arrays.asList(array), comparator, target, start, end);
    }

    /**
     * @param list the list that will search in
     * @param comparator the order of the elements
     * @param target the element that needs to be searched
     * @param start the start index (inclusive) of the list
     * @param end the end index (exclusive) of the list
     * @param <D> the type of the elements in the list
     * @return the index of the element if found, -1 if not found
     */
    public static <D> int linearSearch(List<D> list, Comparator<D> comparator, D target, int start, int end) {
        for(var i = start; i < end; i++) if(comparator.compare(list.get(i), target) == 0) return i;

        return -1;
    }

    /**
     * @param array the array that will search in
     * @param comparator the order of the elements
     * @param target the element that needs to be searched
     * @param start the start index (inclusive) of the array
     * @param end the end index (exclusive) of the array
     * @param <D> the type of the elements in the array
     * @return the index of the element if found, -(insertPoint + 1) if not found
     */
    public static <D> int binarySearch(D[] array, Comparator<D> comparator, D target, int start, int end) {
        return binarySearch(Arrays.asList(array), comparator, target, start, end);
    }

    /**
     * @param list the list that will search in
     * @param comparator the order of the elements
     * @param target the element that needs to be searched
     * @param start the start index (inclusive) of the list
     * @param end the end index (exclusive) of the list
     * @param <D> the type of the elements in the list
     * @return the index of the element if found, -(insertPoint + 1) if not found
     */
    public static <D> int binarySearch(List<D> list, Comparator<D> comparator, D target, int start, int end) {
        var low = start;
        var high = end - 1;

        while(low <= high) {
            var mid = (low + high) >>> 1;
            var cmp = comparator.compare(list.get(mid), target);

            if(cmp < 0) low = mid + 1;
            else if(cmp > 0) high = mid - 1;
            else return mid;
        }

        return -(low + 1);
    }

    /**
     * @param array the array that will search in
     * @param comparator the order of the elements
     * @param target the element that needs to be searched
     * @param start the start index (inclusive) of the array
     * @param end the end index (exclusive) of the array
     * @param <D> the type of the elements in the array
     * @return the index of the element if found, -(insertPoint + 1) if not found
     */
    public static <D> int ternarySearch(D[] array, Comparator<D> comparator, D target, int start, int end) {
        return ternarySearch(Arrays.asList(array), comparator, target, start, end);
    }

    /**
     * @param list the list that will search in
     * @param comparator the order of the elements
     * @param target the element that needs to be searched
     * @param start the start index (inclusive) of the list
     * @param end the end index (exclusive) of the list
     * @param <D> the type of the elements in the list
     * @return the index of the element if found, -(insertPoint + 1) if not found
     */
    public static <D> int ternarySearch(List<D> list, Comparator<D> comparator, D target, int start, int end) {
        var low = start;
        var high = end - 1;

        while(low <= high) {
            var mid1 = low + (high - low) / 3;
            var mid2 = high - (high - low) / 3;
            var cmp1 = comparator.compare(list.get(mid1), target);
            var cmp2 = comparator.compare(list.get(mid2), target);

            if(cmp1 == 0) return mid1;
            if(cmp2 == 0) return mid2;

            if(cmp1 < 0) low = mid1 + 1;
            else if(cmp2 > 0) high = mid2 - 1;
            else low = mid1 + 1;
        }

        return -(low + 1);
    }

    /**
     * @param array the array that will search in
     * @param comparator the order of the elements
     * @param target the element that needs to be searched
     * @param start the start index (inclusive) of the array
     * @param end the end index (exclusive) of the array
     * @param <D> the type of the elements in the array
     * @return the index of the element if found, -1 if not found
     */
    public static <D> int jumpSearch(D[] array, Comparator<D> comparator, D target, int start, int end) {
        return jumpSearch(Arrays.asList(array), comparator, target, start, end);
    }

    /**
     * @param list the list that will search in
     * @param comparator the order of the elements
     * @param target the element that needs to be searched
     * @param start the start index (inclusive) of the list
     * @param end the end index (exclusive) of the list
     * @param <D> the type of the elements in the list
     * @return the index of the element if found, -1 if not found
     */
    public static <D> int jumpSearch(List<D> list, Comparator<D> comparator, D target, int start, int end) {
        var n = end - start;
        var step = (int) Math.sqrt(n);
        var prev = start;

        while(comparator.compare(list.get(Math.min(step, n) - 1), target) < 0) {
            prev = step;
            step += (int) Math.sqrt(n);
            if(prev >= n) return -1;
        }

        while(comparator.compare(list.get(prev), target) < 0) prev++;

        if(comparator.compare(list.get(prev), target) == 0) return prev;

        return -1;
    }

    /**
     * @param array the array that will search in
     * @param comparator the order of the elements
     * @param target the element that needs to be searched
     * @param start the start index (inclusive) of the array
     * @param end the end index (exclusive) of the array
     * @param <D> the type of the elements in the array
     * @return the index of the element if found, -(insertPoint + 1) if not found
     */
    public static <D> int exponentialSearch(D[] array, Comparator<D> comparator, D target, int start, int end) {
        return exponentialSearch(Arrays.asList(array), comparator, target, start, end);
    }

    /**
     * @param list the list that will search in
     * @param comparator the order of the elements
     * @param target the element that needs to be searched
     * @param start the start index (inclusive) of the list
     * @param end the end index (exclusive) of the list
     * @param <D> the type of the elements in the list
     * @return the index of the element if found, -(insertPoint + 1) if not found
     */
    public static <D> int exponentialSearch(List<D> list, Comparator<D> comparator, D target, int start, int end) {
        if (comparator.compare(list.get(start), target) == 0) return start;

        var i = 1;

        while (i < end && comparator.compare(list.get(i), target) <= 0) i *= 2;

        return binarySearch(list, comparator, target, i / 2, Math.min(i, end));
    }

    public static <D> int eytzingerSearch(D[] array, Comparator<D> comparator, D target, int start, int end) {
        return eytzingerSearch(Arrays.asList(array), comparator, target, start, end);
    }

    public static <D> int eytzingerSearch(List<D> list, Comparator<D> comparator, D target, int start, int end) {
        var searchList = new LinkedList<D>(list.subList(start, end));
        var heap = new LinkedList<D>();
        buildEytzinger(searchList, heap, 0, 1);

        int k = 1;

        while(k < heap.size()) {
            if(comparator.compare(heap.get(k - 1), target) >= 0) k *= 2;
            else k = 2 * k + 1;
        }

        k >>= Integer.numberOfTrailingZeros(~k);

        return k < 0 ? -1 : k - 1 + start;
    }

    private static <D> int buildEytzinger(List<D> list, List<D> heap, int i, int k) {
        if(k <= list.size()) {
            i = buildEytzinger(list, heap, i, 2 * k);
            heap.set(k - 1, list.get(i++));
            i = buildEytzinger(list, heap, i, 2 * k + 1);
        }

        return i;
    }
}