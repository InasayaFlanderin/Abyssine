package org.inasayaflanderin.abyssine.miscellaneous.sort;

import java.util.Comparator;
import java.util.List;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.copy;
import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.swap;

public class GrailSort {
    public static <D> void grail(List<D> data, Comparator<D> comparator) {
        if(data.size() < 16) {
            Sort.binaryInsertion(data, comparator);
        }

        var blockLength = 4;

        while(blockLength * blockLength < data.size()) blockLength *= 2;

        var keyLength = (data.size() - 1) / blockLength + 1;
        var idealKey = keyLength + blockLength;
        var keyFound = 1;
        var firstKey = 0;
        var currentKey = 1;

        while(currentKey < data.size() && keyFound < idealKey) {
            var insertPos = 0;
            var right = keyFound;

            while(insertPos < right) {
                var middle = (insertPos + right) >>> 1;
                var compare = comparator.compare(data.get(middle + firstKey), data.get(currentKey));

                if(compare == 0) {
                    insertPos = -1;

                    break;
                } else if(compare < 0) insertPos = middle + 1;
                else right = middle;
            }

            if(insertPos != -1) {

            }
        }
    }

    private static <D> void rotate(List<D> data, int start, int leftLength, int rightLength) {
        var length = Math.min(leftLength, rightLength);

        while(length > 1) {
            if(leftLength <= rightLength) {
                do {
                    swapBlockForward(data, start, start + leftLength, leftLength);
                    start += leftLength;
                    rightLength -= leftLength;
                } while(leftLength <= rightLength);

                length = rightLength;
            } else {
                do {
                    swapBlockBackward(data, start + leftLength - rightLength, start + leftLength, rightLength);
                    leftLength -= rightLength;
                } while(leftLength > rightLength);

                length = leftLength;
            }
        }

        if(length == 1) {
            if(leftLength == 1) {
                D item = data.get(start);
                copy(data, start + 1, data, start, rightLength);
                data.set(start + rightLength, item);
            } else {
                D item = data.get(start + leftLength);
                copy(data, start, data, start + 1, leftLength);
                data.set(start, item);
            }
        }
    }

    private static <D> void swapBlockForward(List<D> data, int firstStart, int secondStart, int blockLength) {
        for (int i = 0; i < blockLength; i++) swap(data, firstStart + i, secondStart + i);
    }

    private static <D> void swapBlockBackward(List<D> data, int firstStart, int secondStart, int blockLength) {
        for(int i = blockLength - 1; i >= 0; i--) swap(data, firstStart + i, secondStart + i);
    }
}