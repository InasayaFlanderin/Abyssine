package org.inasayaflanderin.abyssine.miscellaneous;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.copy;
import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.swap;

//TODO: Replace binarySearch with faster search algorithm with same result
//TODO: Replace ForkJoinPool when using parallel stream with our own implementation

/**
 * <p>This class hold of sorting algorithms that Abyssine supported, it doesn't contain non-comparable sorting algorithms such as Radix sort</p>
 * <p>The input as follows:</p>
 * <p>{@code array} the array that needs to be sorted</p>
 * <p>{@code list} the list that needs to be sorted</p>
 * <p>{@code comparator} the order of the elements</p>
 * <p>{@code start} the start index (inclusive) of the array</p>
 * <p>{@code end} the end index (exclusive) of the array</p>
 * <p>{@code <D>} the type of the elements in the array</p>
 * <p>The parallelism part is performed through ForkJoinPool currently</p>
 */
public class Sort {
    private static final ForkJoinPool fjp = ForkJoinPool.commonPool();

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
            var j = Math.abs(Search.binarySearch(list, comparator, key, start, i) + 1);
            copy(list, j, list, j + 1, i - j);
            list.set(j, key);
        }
    }

    public static <D> void bubble(D[] array, Comparator<D> comparator, int start, int end) {
        bubble(Arrays.asList(array), comparator, start, end);
    }

    public static <D> void bubble(List<D> list, Comparator<D> comparator, int start, int end) {
        for (var i = start; i < end - 1; i++) {
            var swapped = false;

            for (var j = start; j < end - i - 1; j++) {
                if (comparator.compare(list.get(j), list.get(j + 1)) > 0) {
                    swap(list, j, j + 1);
                    swapped = true;
                }
            }

            if (!swapped) break;
        }
    }

    /**
     * Also known as cocktail shaker sort
     */
    public static <D> void shaker(D[] array, Comparator<D> comparator, int start, int end) {
        shaker(Arrays.asList(array), comparator, start, end);
    }

    public static <D> void shaker(List<D> list, Comparator<D> comparator, int start, int end) {
        var swapped = true;

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

    public static <D> void quickIterative(D[] array, Comparator<D> comparator, int start, int end) {
        quickIterative(Arrays.asList(array), comparator, start, end);
    }

    public static <D> void quickIterative(List<D> list, Comparator<D> comparator, int start, int end) {
        var stack = new Stack<Integer>();
        stack.push(start);
        stack.push(end);

        while (!stack.isEmpty()) {
            var endIndex = stack.pop();
            var startIndex = stack.pop();

            if (endIndex - startIndex <= 1) continue;

            var pivotIndex = partition(list, comparator, startIndex, endIndex);

            if (pivotIndex > startIndex) {
                stack.push(startIndex);
                stack.push(pivotIndex);
            }
            if (pivotIndex + 1 < endIndex) {
                stack.push(pivotIndex + 1);
                stack.push(endIndex);
            }
        }
    }

    public static <D> void quickRecursive(D[] array, Comparator<D> comparator, int start, int end) {
        quickRecursive(Arrays.asList(array), comparator, start, end);
    }

    public static <D> void quickRecursive(List<D> list, Comparator<D> comparator, int start, int end) {
        if (start < end) {
            var pivotIndex = partition(list, comparator, start, end);
            quickRecursive(list, comparator, start, pivotIndex);
            quickRecursive(list, comparator, pivotIndex + 1, end);
        }
    }

    public static <D> void quickParallel(D[] array, Comparator<D> comparator, int start, int end) throws InterruptedException {
        quickParallel(Arrays.asList(array), comparator, start, end);
    }

    public static <D> void quickParallel(List<D> list, Comparator<D> comparator, int start, int end) throws InterruptedException {
        if(start < end) {
            var pivotIndex = partition(list, comparator, start, end);
            List<Callable<Void>> tasks = List.of(
                    () -> {
                        quickParallel(list, comparator, start, pivotIndex);
                        return null;
                    },
                    () -> {
                        quickParallel(list, comparator, pivotIndex + 1, end);
                        return null;
                    }
            );
            fjp.invokeAll(tasks);
        }
    }

    public static <D> void mergeIterative(D[] array, Comparator<D> comparator, int start, int end) {
        mergeIterative(Arrays.asList(array), comparator, start, end);
    }

    public static <D> void mergeIterative(List<D> list, Comparator<D> comparator, int start, int end) {
        for (var size = 1; size < end - start; size <<= 1) {
            for (var startIndex = start; startIndex < end; startIndex += size << 1) {
                int mid = Math.min(startIndex + size, end);
                int endIndex = Math.min(startIndex + (size << 1), end);
                merge(list, new LinkedList<>(list.subList(startIndex, mid)), new LinkedList<>(list.subList(mid, endIndex)), comparator, startIndex);
            }
        }
    }

    public static <D> void mergeRecursive(D[] array, Comparator<D> comparator, int start, int end) {
        mergeRecursive(Arrays.asList(array), comparator, start, end);
    }

    public static <D> void mergeRecursive(List<D> list, Comparator<D> comparator, int start, int end) {
        if (end - start > 1) {
            var mid = (start + end) >>> 1;
            mergeRecursive(list, comparator, start, mid);
            mergeRecursive(list, comparator, mid, end);
            merge(list, new LinkedList<>(list.subList(start, mid)), new LinkedList<>(list.subList(mid, end)), comparator, start);
        }
    }

    public static <D> void mergeParallel(D[] array, Comparator<D> comparator, int start, int end) throws InterruptedException {
        mergeParallel(Arrays.asList(array), comparator, start, end);
    }

    public static <D> void mergeParallel(List<D> list, Comparator<D> comparator, int start, int end) throws InterruptedException {
        if(end - start > 1) {
            var mid = (start + end) >>> 1;
            List<Callable<Void>> tasks = List.of(
                    () -> {
                        mergeParallel(list, comparator, start, mid);
                        return null;
                    },
                    () -> {
                        mergeParallel(list, comparator, mid, end);
                        return null;
                    }
            );
            fjp.invokeAll(tasks);
            merge(list, new LinkedList<>(list.subList(start, mid)), new LinkedList<>(list.subList(mid, end)), comparator, start);
        }
    }

    public static <D> void heap(D[] array, Comparator<D> comparator, int start, int end) {
        heap(Arrays.asList(array), comparator, start, end);
    }

    public static <D> void heap(List<D> list, Comparator<D> comparator, int start, int end) {
        var length = end - start;

        for(var i = start + (length >>> 1) - 1; i >= start; i--) heapify(list, comparator, start, end, i);

        for(var i = end - 1; i > start; i--) {
            swap(list, start, i);
            heapify(list, comparator, start, i, start);
        }
    }

    public static <D> void comb(D[] array, Comparator<D> comparator, int start, int end) {
        comb(Arrays.asList(array), comparator, start, end);
    }

    public static <D> void comb(List<D> list, Comparator<D> comparator, int start, int end) {
        var gap = end - start;
        var swapped = true;

        while(gap > 1 || swapped) {
            gap = Math.max(1, gap * 10 / 13);
            swapped = false;

            for(var i = start; i + gap < end; i++) {
                if(comparator.compare(list.get(i), list.get(i + gap)) > 0) {
                    swap(list, i, i + gap);
                    swapped = true;
                }
            }
        }
    }

    public static <D> void shell(D[] array, Comparator<D> comparator, int start, int end) {
        shell(Arrays.asList(array), comparator, start, end);
    }

    public static <D> void shell(List<D> list, Comparator<D> comparator, int start, int end) {
        var gap = end - start;

        do {
            gap = Math.max(1, gap * 10 / 23);

            for (var i = start + gap; i < end; i++) {
                var temp = list.get(i);
                var j = i;

                while (j - gap >= start && comparator.compare(list.get(j - gap), temp) > 0) {
                    list.set(j, list.get(j - gap));
                    j -= gap;
                }

                list.set(j, temp);
            }
        } while (gap != 1);
    }

    public static <D> void cycle(D[] array, Comparator<D> comparator, int start, int end) {
        cycle(Arrays.asList(array), comparator, start, end);
    }

    public static <D> void cycle(List<D> list, Comparator<D> comparator, int start, int end) {
        for(var cycleStart = start; cycleStart < end - 1; cycleStart++) {
            D datum = list.get(cycleStart);
            var pos = cycleFindSwap(list, comparator, start, end, cycleStart, datum);

            if(pos == -1) continue;

            while(pos != -1) pos = cycleFindSwap(list, comparator, start, end, cycleStart, datum);
        }
    }

    private static <D> int partition(List<D> list, Comparator<D> comparator, int start, int end) {
        var pivot = list.get(end - 1);
        var i = start - 1;

        for (var j = start; j < end - 1; j++) if (comparator.compare(list.get(j), pivot) < 0) swap(list, ++i, j);

        swap(list, i + 1, end - 1);

        return i + 1;
    }

    private static <D> void merge(List<D> data, List<D> leftCopy, List<D> rightCopy, Comparator<D> comparator, int start) {
        while(!leftCopy.isEmpty() && !rightCopy.isEmpty()) {
            if(comparator.compare(leftCopy.getFirst(), rightCopy.getFirst()) <= 0) data.set(start, leftCopy.removeFirst());
            else data.set(start, rightCopy.removeFirst());

            start++;
        }
        while(!leftCopy.isEmpty()) data.set(start++, leftCopy.removeFirst());
        while(!rightCopy.isEmpty()) data.set(start++, rightCopy.removeFirst());
    }

    private static <D> void heapify(List<D> list, Comparator<D> comparator, int start, int end, int i) {
        var largest = i;
        var left = -start + (i << 1) + 1;
        var right = -start + (i << 1) + 2;

        if(left < end && comparator.compare(list.get(left), list.get(largest)) > 0) largest = left;
        if(right < end && comparator.compare(list.get(right), list.get(largest)) > 0) largest = right;
        if(largest != i) {
            swap(list, i, largest);
            heapify(list, comparator, start, end, largest);
        }
    }

    private static <D> int cycleFindSwap(List<D> list, Comparator<D> comparator, int start, int end, int cycleStart, D datum) {
        var pos = cycleStart + (int) list.subList(start + 1, end).stream().filter(d -> comparator.compare(d, datum) < 0).count();

        if(pos == cycleStart) return -1;

        while(comparator.compare(datum, list.get(pos)) == 0) pos++;

        swap(list, cycleStart, pos);

        return pos;
    }
}