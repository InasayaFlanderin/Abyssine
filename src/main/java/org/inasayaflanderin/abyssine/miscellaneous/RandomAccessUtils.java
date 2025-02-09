package org.inasayaflanderin.abyssine.miscellaneous;

import org.inasayaflanderin.abyssine.miscellaneous.rng.RandomGenerators;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class RandomAccessUtils {
    public static <D> void swap(List<D> list, int firstIndex, int secondIndex) {
        D temp = list.get(firstIndex);
        list.set(firstIndex, list.get(secondIndex));
        list.set(secondIndex, temp);
    }

    public static <D> void swap(D[] array, int firstIndex, int secondIndex) {
        D temp = array[firstIndex];
        array[firstIndex] = array[secondIndex];
        array[secondIndex] = temp;
    }

    public static <D> boolean isSort(List<D> list, Comparator<D> comparison) {
        return IntStream.range(0, list.size() - 1).parallel()
                .noneMatch(i -> comparison.compare(list.get(i), list.get(i + 1)) > 0);
    }

    public static <D> boolean isSort(D[] array, Comparator<D> comparison) {
        return isSort(Arrays.asList(array), comparison);
    }

    public static <D> void flip(List<D> list, int fromIndex, int toIndex) {
        int left = fromIndex;
        int right = toIndex - 1;

        while(left < right) {
            swap(list, left, right);
            left++;
            right--;
        }
    }

    public static <D> void flip(D[] array, int fromIndex, int toIndex) {
        flip(Arrays.asList(array), fromIndex, toIndex);
    }

    public static <D> void shuffle(D[] array, RandomGenerators rng) {
        shuffle(Arrays.asList(array), rng);
    }

    public static <D> void shuffle(List<D> list, RandomGenerators rng) {
        for (int i = 0; i < list.size(); i++) swap(list, i, (int) (rng.next() * list.size()));
    }

    public static <D> int binarySearch(D[] array, int fromIndex, int toIndex, D key, Comparator<D> comparator) {
        return Arrays.binarySearch(array, fromIndex, toIndex, key, comparator);
    }

    public static <D> int binarySearch(List<D> list, int fromIndex, int toIndex, D key, Comparator<D> comparator) {
        if (fromIndex > toIndex) throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        if (fromIndex < 0 || toIndex > list.size()) throw new ArrayIndexOutOfBoundsException(fromIndex);

        var low = fromIndex;
        var high = toIndex - 1;

        while(low <= high) {
            var mid = (low + high) >>> 1;
            var midDatum = list.get(mid);
            var compare = comparator.compare(midDatum, key);

            if(compare < 0) low = mid + 1;
            else if(compare > 0) high = mid - 1;
            else return mid;
        }

        return -(low + 1);
    }
}