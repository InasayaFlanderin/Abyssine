package org.inasayaflanderin.abyssine.miscellaneous.sort;

import java.util.Comparator;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.swap;

public class GrailSort {
    private static <D> void grailBlockSwap(D[] array, int a, int b, int blockLen) {
        for(int i = 0; i < blockLen; i++) {
            swap(array, a + i, b + i);
        }
    }

    private static <D> void grailRotate(D[] array, int start, int leftLen, int rightLen) {
        while(leftLen > 0 && rightLen > 0) {
            if(leftLen <= rightLen) {
                grailBlockSwap(array, start, start + leftLen, leftLen);
                start    += leftLen;
                rightLen -= leftLen;
            }
            else {
                grailBlockSwap(array, start + leftLen - rightLen, start + leftLen, rightLen);
                leftLen  -= rightLen;
            }
        }
    }

    private static <D> void grailInsertSort(D[] array, Comparator<D> cmp, int length) {
        for(int item = 1; item < length; item++) {
            int  left = item - 1;
            int right = item;

            while(left >= 0 && cmp.compare(array[left],
                    array[right]) > 0) {
                swap(array, left, right);
                left--;
                right--;
            }
        }
    }


    private static <D> int grailBinarySearchLeft(D[] array, Comparator<D> cmp, int start, int length, D target) {
        int  left = 0;
        int right = length;

        while(left < right) {
            int middle = (left + right) >>> 1;

            if(cmp.compare(array[start + middle], target) < 0) {
                left = middle + 1;
            }
            else {
                right = middle;
            }
        }
        return left;
    }

    private static <D> int grailBinarySearchRight(D[] array, Comparator<D> cmp, int start, int length, D target) {
        int  left = 0;
        int right = length;

        while(left < right) {
            int middle = (left + right) >>> 1;

            if(cmp.compare(array[start + middle], target) > 0) {
                right = middle;
            }
            else {
                left = middle + 1;
            }
        }

        return right;
    }

    private static <T> int binarySearchExclusive(T[] array, int start, int length, T target, Comparator<T> cmp) {
        int  left = 0;
        int right = length;

        while(left < right) {
            int middle = (left + right) >>> 1;

            int comp = cmp.compare(array[start + middle], target);
            if(comp == 0) {
                return -1;
            }
            else if(comp < 0) {
                left = middle + 1;
            }
            else {
                right = middle;
            }
        }
        return left;
    }

    private static <T> void insertBackwards(T[] array, int start, int length) {
        T item = array[start + length];
        System.arraycopy(array, start, array, start + 1, length);
        array[start] = item;
    }

    private static <T> int grailCollectKeys(T[] array, Comparator<T> cmp, int length, int idealKeys) {
        int keysFound = 1;
        int  firstKey = 0;
        int   currKey = 1;

        while(currKey < length && keysFound < idealKeys) {
            int insertPos = binarySearchExclusive(array, firstKey, keysFound, array[currKey], cmp);

            if(insertPos != -1) {
                grailRotate(array, firstKey, keysFound, currKey - (firstKey + keysFound));
                firstKey = currKey - keysFound;

                if(keysFound != insertPos) {
                    insertBackwards(array, firstKey + insertPos, keysFound - insertPos);
                }

                keysFound++;
            }
            currKey++;
        }

        grailRotate(array, 0, firstKey, keysFound);
        return keysFound;
    }

    private static <T> void grailPairwiseSwaps(T[] array, Comparator<T> cmp, int start, int length) {
        T  firstKey = array[start - 1];
        T secondKey = array[start - 2];

        sortPairs(array, cmp, start, length);

        array[start + length - 2] =  firstKey;
        array[start + length - 1] = secondKey;
    }

    private static <T> void sortPairs(T[] array, Comparator<T> cmp, int start, int length) {
        int index;
        for(index = 1; index < length; index += 2) {
            int  left = start + index - 1;
            int right = start + index;

            if(cmp.compare(array[left], array[right]) > 0) {
                array[ left - 2] = array[right];
                array[right - 2] = array[ left];
            }
            else {
                array[ left - 2] = array[ left];
                array[right - 2] = array[right];
            }
        }

        int left = start + index - 1;
        if(left < start + length) {
            array[left - 2] = array[left];
        }
    }

    private static <D> void grailMergeForwards(D[] array, Comparator<D> cmp, int start, int leftLen, int rightLen,
                                               int bufferOffset) {
        int buffer = start  - bufferOffset;
        int   left = start;
        int middle = start  +  leftLen;
        int  right = middle;
        int    end = middle + rightLen;

        while(right < end) {
            if(left == middle || cmp.compare(array[ left],
                    array[right]) > 0) {
                swap(array, buffer, right);
                right++;
            }
            else {
                swap(array, buffer,  left);
                left++;
            }
            buffer++;
        }

        if(buffer != left) {
            grailBlockSwap(array, buffer, left, middle - left);
        }
    }

    private static <D> void grailMergeBackwards(D[] array, Comparator<D> cmp, int start, int leftLen, int rightLen, int bufferOffset) {
        int end = start  -  1;
        int left = end    +  leftLen;
        int middle = left;
        int right = middle + rightLen;
        int buffer = right  + bufferOffset;

        while(left > end) {
            if(right == middle || cmp.compare(array[ left], array[right]) > 0) {
                swap(array, buffer,  left);
                left--;
            }
            else {
                swap(array, buffer, right);
                right--;
            }
            buffer--;
        }

        if(right != buffer) {
            while(right > middle) {
                swap(array, buffer, right);
                buffer--;
                right--;
            }
        }
    }

    private static <D> void grailBuildInPlace(D[] array, Comparator<D> cmp, int start, int length, int bufferLen) {
        for(int mergeLen = 2; mergeLen < bufferLen; mergeLen *= 2) {
            int fullMerge = 2 * mergeLen;

            int mergeIndex;
            int mergeEnd = start + length - fullMerge;

            for(mergeIndex = start; mergeIndex <= mergeEnd; mergeIndex += fullMerge) {
                grailMergeForwards(array, cmp, mergeIndex, mergeLen, mergeLen, mergeLen);
            }

            int leftOver = length - (mergeIndex - start);

            if(leftOver > mergeLen) {
                grailMergeForwards(array, cmp, mergeIndex, mergeLen, leftOver - mergeLen, mergeLen);
            }
            else {
                grailRotate(array, mergeIndex - mergeLen, mergeLen, leftOver);
            }

            start -= mergeLen;
        }

        int fullMerge  = 2 * bufferLen;
        int lastBlock  = length % fullMerge;
        int lastOffset = start + length - lastBlock;

        if(lastBlock <= bufferLen) {
            grailRotate(array, lastOffset, lastBlock, bufferLen);
        }
        else {
            grailMergeBackwards(array, cmp, lastOffset, bufferLen, lastBlock - bufferLen, bufferLen);
        }

        for(int mergeIndex = lastOffset - fullMerge; mergeIndex >= start; mergeIndex -= fullMerge) {
            grailMergeBackwards(array, cmp, mergeIndex, bufferLen, bufferLen, bufferLen);
        }
    }

    private static <D> void grailBuildBlocks(D[] array, Comparator<D> cmp, int start, int length, int bufferLen) {
        grailPairwiseSwaps(array, cmp, start, length);
        grailBuildInPlace(array, cmp, start - 2, length, bufferLen);
    }

    private static <D> int grailBlockSelectSort(D[] array, Comparator<D> cmp, int start, int medianKey,
                                                int blockCount, int blockLen) {
        for(int  firstBlock = 0; firstBlock < blockCount; firstBlock++) {
            int selectBlock = firstBlock;

            for(int currBlock = firstBlock + 1; currBlock < blockCount; currBlock++) {
                int compare = cmp.compare(array[start + (currBlock   * blockLen)],
                        array[start + (selectBlock * blockLen)]);

                if(compare < 0 || (compare == 0 && cmp.compare(array[currBlock],
                        array[selectBlock]) < 0)) {
                    selectBlock = currBlock;
                }
            }

            if(selectBlock != firstBlock) {
                grailBlockSwap(array, start + (firstBlock * blockLen), start + (selectBlock * blockLen), blockLen);
                swap(array, firstBlock, selectBlock);

                if(medianKey == firstBlock) {
                    medianKey = selectBlock;
                }
                else if(medianKey == selectBlock) {
                    medianKey = firstBlock;
                }
            }
        }

        return medianKey;
    }

    private static <D> void grailInPlaceBufferReset(D[] array, int start, int length, int bufferOffset) {
        int buffer =  start + length - 1;
        int  index = buffer - bufferOffset;

        while(buffer >= start) {
            swap(array, index, buffer);
            buffer--;
            index--;
        }
    }

    private static <D> void grailInPlaceBufferRewind(D[] array, int start, int leftBlock, int buffer) {
        while(leftBlock >= start) {
            swap(array, buffer, leftBlock);
            leftBlock--;
            buffer--;
        }
    }

    private static <D> boolean grailGetSubarray(D[] array, Comparator<D> cmp, int currentKey, int medianKey) {
        return cmp.compare(array[currentKey], array[medianKey]) >= 0;
    }

    private static <D> int grailCountLastMergeBlocks(D[] array, Comparator<D> cmp, int offset, int blockCount, int blockLen) {
        int blocksToMerge = 0;

        int lastRightFrag = offset + (blockCount * blockLen);
        int prevLeftBlock = lastRightFrag - blockLen;

        while(blocksToMerge < blockCount && cmp.compare(array[lastRightFrag],
                array[prevLeftBlock]) < 0) {
            blocksToMerge++;
            prevLeftBlock -= blockLen;
        }

        return blocksToMerge;
    }

    private static <D> void grailSmartMerge(D[] array, Comparator<D> cmp, int start, int leftLen, boolean leftOrigin,
                                 int rightLen, int bufferOffset, int[] currBlockLen, boolean[] currBlockOrigin) {
        int buffer = start  - bufferOffset;
        int   left = start;
        int middle = start  +  leftLen;
        int  right = middle;
        int    end = middle + rightLen;

        if (leftOrigin) {
            while(left < middle && right < end) {
                if(cmp.compare(array[left], array[right]) <  0) {
                    swap(array, buffer, left);
                    left++;
                }
                else {
                    swap(array, buffer, right);
                    right++;
                }
                buffer++;
            }
        } else {
            while(left < middle && right < end) {
                if(cmp.compare(array[left], array[right]) <= 0) {
                    swap(array, buffer, left);
                    left++;
                }
                else {
                    swap(array, buffer, right);
                    right++;
                }
                buffer++;
            }
        }

        if(left < middle) {
            currBlockLen[0] = middle - left;
            grailInPlaceBufferRewind(array, left, middle - 1, end - 1);
        }
        else {
            currBlockLen[0] = end - right;
            currBlockOrigin[0] = !leftOrigin;
        }
    }

    private static <D> void grailSmartLazyMerge(D[] array, Comparator<D> cmp, int start, int leftLen, boolean leftOrigin, int rightLen, int[] currBlockLen, boolean[] currBlockOrigin) {
        int middle = start + leftLen;

        if(!leftOrigin) {
            if(cmp.compare(array[middle - 1], array[middle]) >  0) {
                while(leftLen != 0) {
                    int mergeLen = grailBinarySearchLeft(array, cmp, middle, rightLen, array[start]);

                    if(mergeLen != 0) {
                        grailRotate(array, start, leftLen, mergeLen);

                        start    += mergeLen;
                        middle   += mergeLen;
                        rightLen -= mergeLen;
                    }

                    if(rightLen == 0) {
                        currBlockLen[0] = leftLen;
                        return;
                    }
                    else {
                        do {
                            start++;
                            leftLen--;
                        } while(leftLen != 0 && cmp.compare(array[start ],
                                array[middle]) <= 0);
                    }
                }
            }
        }
        else {
            if(cmp.compare(array[middle - 1], array[middle]) >= 0) {
                while(leftLen != 0) {
                    int mergeLen = grailBinarySearchRight(array, cmp, middle, rightLen, array[start]);

                    if(mergeLen != 0) {
                        grailRotate(array, start, leftLen, mergeLen);

                        start    += mergeLen;
                        middle   += mergeLen;
                        rightLen -= mergeLen;
                    }

                    if(rightLen == 0) {
                        currBlockLen[0] = leftLen;
                        return;
                    }
                    else {
                        do {
                            start++;
                            leftLen--;
                        } while(leftLen != 0 && cmp.compare(array[start ],
                                array[middle]) < 0);
                    }
                }
            }
        }

        currBlockLen[0] = rightLen;
        currBlockOrigin[0] = !leftOrigin;
    }

    private static <D> void grailMergeBlocks(D[] array, Comparator<D> cmp, int medianKey, int start,
                                  int blockCount, int blockLen, int lastMergeBlocks,
                                  int lastLen) {
        int buffer;

        int currBlock;
        int nextBlock = start + blockLen;

        int[] currBlockLen    = {blockLen};
        boolean[] currBlockOrigin = {grailGetSubarray(array, cmp, 0, medianKey)};

        for(int keyIndex = 1; keyIndex < blockCount; keyIndex++, nextBlock += blockLen) {
            boolean nextBlockOrigin;

            currBlock       = nextBlock - currBlockLen[0];
            nextBlockOrigin = grailGetSubarray(array, cmp, keyIndex, medianKey);

            if(nextBlockOrigin == currBlockOrigin[0]) {
                buffer = currBlock - blockLen;

                grailBlockSwap(array, buffer, currBlock, currBlockLen[0]);
                currBlockLen[0] = blockLen;
            }
            else {
                grailSmartMerge(array, cmp, currBlock, currBlockLen[0], currBlockOrigin[0],
                        blockLen, blockLen, currBlockLen, currBlockOrigin);
            }
        }

        currBlock = nextBlock - currBlockLen[0];
        buffer    = currBlock - blockLen;

        if(lastLen != 0) {
            if(currBlockOrigin[0]) {
                grailBlockSwap(array, buffer, currBlock, currBlockLen[0]);

                currBlock            = nextBlock;
                currBlockLen[0]    = blockLen * lastMergeBlocks;
                currBlockOrigin[0] = false;
            } else currBlockLen[0] += blockLen * lastMergeBlocks;

            grailMergeForwards(array, cmp, currBlock, currBlockLen[0], lastLen, blockLen);
        }
        else {
            grailBlockSwap(array, buffer, currBlock, currBlockLen[0]);
        }
    }

    private static <D> void grailLazyMergeBlocks(D[] array, Comparator<D> cmp, int medianKey, int start,
                                      int blockCount, int blockLen, int lastMergeBlocks,
                                      int lastLen) {
        int currBlock;
        int nextBlock = start + blockLen;

        int[] currBlockLen    = {blockLen};
        boolean[] currBlockOrigin = {grailGetSubarray(array, cmp, 0, medianKey)};

        for(int keyIndex = 1; keyIndex < blockCount; keyIndex++, nextBlock += blockLen) {
            boolean nextBlockOrigin;

            currBlock       = nextBlock - currBlockLen[0];
            nextBlockOrigin = grailGetSubarray(array, cmp, keyIndex, medianKey);

            if(nextBlockOrigin == currBlockOrigin[0]) {
                currBlockLen[0] = blockLen;
            }
            else {
                if(blockLen != 0 && currBlockLen[0] != 0) {
                    grailSmartLazyMerge(array, cmp, currBlock, currBlockLen[0], currBlockOrigin[0],
                            blockLen, currBlockLen, currBlockOrigin);
                }
            }
        }

        currBlock = nextBlock - currBlockLen[0];

        if(lastLen != 0) {
            if(currBlockOrigin[0]) {
                currBlock            = nextBlock;
                currBlockLen[0]    = blockLen * lastMergeBlocks;
                currBlockOrigin[0] = false;
            } else currBlockLen[0] += blockLen * lastMergeBlocks;

            grailLazyMerge(array, cmp, currBlock, currBlockLen[0], lastLen);
        }
    }

    private static <D> void grailCombineInPlace(D[] array, Comparator<D> cmp, int start, int length,
                                     int subarrayLen, int blockLen,
                                     int mergeCount, int lastSubarrays,
                                     boolean buffer) {
        int fullMerge  = 2 * subarrayLen;
        final int blockCount = fullMerge / blockLen;

        for(int mergeIndex = 0; mergeIndex < mergeCount; mergeIndex++) {
            int offset = start + (mergeIndex * fullMerge);

            grailInsertSort(array, cmp, blockCount);
            int medianKey = subarrayLen / blockLen;
            medianKey = grailBlockSelectSort(array, cmp, offset, medianKey, blockCount, blockLen);

            if(buffer) {
                grailMergeBlocks(array, cmp, medianKey, offset, blockCount, blockLen, 0, 0);
            }
            else {
                grailLazyMergeBlocks(array, cmp, medianKey, offset, blockCount, blockLen, 0, 0);
            }
        }

        if(lastSubarrays != 0) {
            int offsetFull = start + (mergeCount * fullMerge);
            var blockCountFull = lastSubarrays / blockLen;

            grailInsertSort(array, cmp, blockCountFull + 1);

            int medianKey = subarrayLen / blockLen;
            medianKey = grailBlockSelectSort(array, cmp, offsetFull, medianKey, blockCountFull, blockLen);
            int lastFragment = lastSubarrays - (blockCountFull * blockLen);
            int lastMergeBlocks;
            if(lastFragment != 0) {
                lastMergeBlocks = grailCountLastMergeBlocks(array, cmp, offsetFull, blockCountFull, blockLen);
            }
            else {
                lastMergeBlocks = 0;
            }

            int smartMerges = blockCountFull - lastMergeBlocks;

            if(smartMerges == 0) {
                int leftLen = lastMergeBlocks * blockLen;
                if(buffer) {
                    grailMergeForwards(array, cmp, offsetFull, leftLen, lastFragment, blockLen);
                }
                else {
                    grailLazyMerge(array, cmp, offsetFull, leftLen, lastFragment);
                }
            }
            else {
                if(buffer) {
                    grailMergeBlocks(array, cmp, medianKey, offsetFull,
                            smartMerges, blockLen, lastMergeBlocks, lastFragment);
                }
                else {
                    grailLazyMergeBlocks(array, cmp, medianKey, offsetFull,
                            smartMerges, blockLen, lastMergeBlocks, lastFragment);
                }
            }
        }

        if(buffer) {
            grailInPlaceBufferReset(array, start, length, blockLen);
        }
    }

    private static <D> void grailCombineBlocks(D[] array, Comparator<D> cmp, int start, int length,
                                    int subarrayLen, int blockLen, boolean buffer) {
        int     fullMerge = 2 * subarrayLen;
        int    mergeCount = length /  fullMerge;
        int lastSubarrays = length - (fullMerge * mergeCount);

        if(lastSubarrays <= subarrayLen) {
            length -= lastSubarrays;
            lastSubarrays = 0;
        }

        grailCombineInPlace(array, cmp, start, length, subarrayLen, blockLen,
                mergeCount, lastSubarrays, buffer);
    }

    private static <D> void grailLazyMerge(D[] array, Comparator<D> cmp, int start, int leftLen, int rightLen) {
        if(leftLen < rightLen) {
            int middle = start + leftLen;

            while(leftLen != 0) {
                int mergeLen = grailBinarySearchLeft(array, cmp, middle, rightLen, array[start]);

                if(mergeLen != 0) {
                    grailRotate(array, start, leftLen, mergeLen);

                    start    += mergeLen;
                    middle   += mergeLen;
                    rightLen -= mergeLen;
                }

                if(rightLen == 0) {
                    break;
                }
                else {
                    do {
                        start++;
                        leftLen--;
                    } while(leftLen != 0 && cmp.compare(array[start ],
                            array[middle]) <= 0);
                }
            }
        }
        else {
            int end = start + leftLen + rightLen - 1;

            while(rightLen != 0) {
                int mergeLen = grailBinarySearchRight(array, cmp, start, leftLen, array[end]);

                if(mergeLen != leftLen) {
                    grailRotate(array, start + mergeLen, leftLen - mergeLen, rightLen);

                    end     -=  leftLen - mergeLen;
                    leftLen  = mergeLen;
                }

                if(leftLen == 0) {
                    break;
                }
                else {
                    int middle = start + leftLen;
                    do {
                        rightLen--;
                        end--;
                    } while(rightLen != 0 && cmp.compare(array[middle - 1],
                            array[end       ]) <= 0);
                }
            }
        }
    }

    private static <D> void grailLazyStableSort(D[] array, Comparator<D> cmp, int length) {
        for(int index = 1; index < length; index += 2) {
            int  left = index - 1;

            if(cmp.compare(array[left], array[index]) > 0) {
                swap(array, left, index);
            }
        }
        for(int mergeLen = 2; mergeLen < length; mergeLen *= 2) {
            int fullMerge = 2 * mergeLen;

            int mergeIndex;
            int mergeEnd = length - fullMerge;

            for(mergeIndex = 0; mergeIndex <= mergeEnd; mergeIndex += fullMerge) {
                grailLazyMerge(array, cmp, mergeIndex, mergeLen, mergeLen);
            }

            int leftOver = length - mergeIndex;
            if(leftOver > mergeLen) {
                grailLazyMerge(array, cmp, mergeIndex, mergeLen, leftOver - mergeLen);
            }
        }
    }

    public static <D> void grailCommonSort(D[] array, Comparator<D> cmp) {
        if(array.length < 16) {
            Sort.binaryInsertion(array, cmp);

            return;
        }

        int blockLen = 4;

        while((blockLen * blockLen) < array.length) {
            blockLen *= 2;
        }

        int keyLen = (array.length - 1) / blockLen + 1;

        int idealKeys = keyLen + blockLen;

        int keysFound = grailCollectKeys(array, cmp, array.length, idealKeys);

        boolean idealBuffer;
        if(keysFound < idealKeys) {
            if(keysFound < 4) {
                if(keysFound == 1) return;

                grailLazyStableSort(array, cmp, array.length);
                return;
            }
            else {
                keyLen = blockLen;
                blockLen = 0;
                idealBuffer = false;

                while(keyLen > keysFound) {
                    keyLen /= 2;
                }
            }
        }
        else {
            idealBuffer = true;
        }

        int bufferEnd = blockLen + keyLen;
        int subarrayLen;
        if(idealBuffer) {
            subarrayLen = blockLen;
        }
        else {
            subarrayLen = keyLen;
        }

        grailBuildBlocks(array, cmp, bufferEnd, array.length - bufferEnd, subarrayLen);

        while((array.length - bufferEnd) > (2 * subarrayLen)) {
            subarrayLen *= 2;

            int currentBlockLen = blockLen;
            boolean scrollingBuffer = idealBuffer;

            if(!idealBuffer) {
                int keyBuffer = keyLen / 2;
                if(keyBuffer >= ((2 * subarrayLen) / keyBuffer)) {
                    currentBlockLen = keyBuffer;
                    scrollingBuffer = true;
                }
                else {
                    currentBlockLen = (2 * subarrayLen) / keyLen;
                }
            }

            grailCombineBlocks(array, cmp, bufferEnd, array.length - bufferEnd,
                    subarrayLen, currentBlockLen, scrollingBuffer);
        }

        grailInsertSort(array, cmp, bufferEnd);
        grailLazyMerge(array, cmp, 0, bufferEnd, array.length - bufferEnd);
    }
}