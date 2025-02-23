package org.inasayaflanderin.abyssine.miscellaneous.sort;

import java.util.Comparator;
import java.util.List;

public class GrailSort {
    public static <D> void grail(List<D> data, Comparator<D> comparator) {
        if(data.size() < 16) {
            Sort.binaryInsertion(data, comparator);
        }

        var blockLength = ;

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


            }
        }
    }
}