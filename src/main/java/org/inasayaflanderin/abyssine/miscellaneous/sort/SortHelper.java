package org.inasayaflanderin.abyssine.miscellaneous.sort;

import java.util.List;

class SortHelper {
    public static <T> void swap(List<T> list, int firstIndex, int secondIndex) {
        T temp = list.get(firstIndex);
        list.set(firstIndex, list.get(secondIndex));
        list.set(secondIndex, temp);
    }
}
