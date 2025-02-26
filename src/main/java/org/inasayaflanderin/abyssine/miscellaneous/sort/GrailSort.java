package org.inasayaflanderin.abyssine.miscellaneous.sort;

import java.util.Comparator;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.copy;
import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.swap;

public class GrailSort {
    private static <D> void grailBlockSwap(D[] array, int a, int b, int blockLen) {
        for(int i = 0; i < blockLen; i++) swap(array, a + i, b + i);
    }

    private static <D> void grailRotate(D[] array, int start, int leftLen, int rightLen) {
        while(leftLen > 0 && rightLen > 0) {
            if(leftLen <= rightLen) {
                grailBlockSwap(array, start, start + leftLen, leftLen);
                start += leftLen;
                rightLen -= leftLen;
            } else {
                grailBlockSwap(array, start + leftLen - rightLen, start + leftLen, rightLen);
                leftLen -= rightLen;
            }
        }
    }

    private static <D> void grailInsertSort(D[] array, Comparator<D> cmp, int length) {
        for(int item = 1; item < length; item++) {
            var left = item - 1;
            var right = item;

            while(left >= 0 && cmp.compare(array[left], array[right]) > 0) swap(array, left--, right--);
        }
    }


    private static <D> int grailBinarySearchLeft(D[] array, Comparator<D> cmp, int start, int length, D target) {
        var left = 0;
        var right = length;

        while(left < right) {
            var middle = (left + right) >>> 1;

            if(cmp.compare(array[start + middle], target) < 0) left = middle + 1;
            else right = middle;
        }

        return left;
    }

    private static <D> int grailBinarySearchRight(D[] array, Comparator<D> cmp, int start, int length, D target) {
        var left = 0;
        var right = length;

        while(left < right) {
            int middle = (left + right) >>> 1;

            if(cmp.compare(array[start + middle], target) > 0) right = middle;
            else left = middle + 1;
        }

        return right;
    }

    private static <T> int binarySearchExclusive(T[] array, Comparator<T> cmp, int start, int length, T target) {
        var left = 0;
        var right = length;

        while(left < right) {
            var middle = (left + right) >>> 1;
            var comp = cmp.compare(array[start + middle], target);

            if(comp == 0) return -1;
            else if(comp < 0) left = middle + 1;
            else right = middle;
        }

        return left;
    }

    private static <T> void insertBackwards(T[] array, int start, int length) {
        var item = array[start + length];
        copy(array, start, array, start + 1, length);
        array[start] = item;
    }

    private static <T> int grailCollectKeys(T[] array, Comparator<T> cmp, int length, int idealKeys) {
        var keysFound = 1;
        var firstKey = 0;
        var currKey = 1;

        while(currKey < length && keysFound < idealKeys) {
            var insertPos = binarySearchExclusive(array, cmp, firstKey, keysFound, array[currKey]);

            if(insertPos != -1) {
                grailRotate(array, firstKey, keysFound, currKey - (firstKey + keysFound));
                firstKey = currKey - keysFound;

                if(keysFound != insertPos) insertBackwards(array, firstKey + insertPos, keysFound - insertPos);

                keysFound++;
            }

            currKey++;
        }

        grailRotate(array, 0, firstKey, keysFound);

        return keysFound;
    }

    private static <T> void grailPairwiseSwaps(T[] array, Comparator<T> cmp, int start, int length) {
        var firstKey = array[start - 1];
        var secondKey = array[start - 2];
        sortPairs(array, cmp, start, length);
        array[start + length - 2] =  firstKey;
        array[start + length - 1] = secondKey;
    }

    private static <T> void sortPairs(T[] array, Comparator<T> cmp, int start, int length) {
        int index;

        for(index = 1; index < length; index += 2) {
            var left = start + index - 1;
            var right = start + index;

            if(cmp.compare(array[left], array[right]) > 0) {
                array[ left - 2] = array[right];
                array[right - 2] = array[ left];
            } else {
                array[ left - 2] = array[ left];
                array[right - 2] = array[right];
            }
        }

        var left = start + index - 1;

        if(left < start + length) array[left - 2] = array[left];
    }

    private static <D> void grailMergeForwards(D[] array, Comparator<D> cmp, int start, int leftLen, int rightLen, int bufferOffset) {
        var buffer = start - bufferOffset;
        var left = start;
        var middle = start  +  leftLen;
        var right = middle;
        var end = middle + rightLen;

        while(right < end) {
            if(left == middle || cmp.compare(array[left], array[right]) > 0) swap(array, buffer, right++);
            else swap(array, buffer,  left++);

            buffer++;
        }

        if(buffer != left) grailBlockSwap(array, buffer, left, middle - left);
    }

    private static <D> void grailMergeBackwards(D[] array, Comparator<D> cmp, int start, int leftLen, int rightLen, int bufferOffset) {
        var end = start - 1;
        var left = end + leftLen;
        var middle = left;
        var right = middle + rightLen;
        var buffer = right + bufferOffset;

        while(left > end) {
            if(right == middle || cmp.compare(array[ left], array[right]) > 0) swap(array, buffer,  left--);
            else swap(array, buffer, right--);

            buffer--;
        }

        if(right != buffer) while(right > middle) swap(array, buffer--, right--);
    }

    private static <D> void grailBuildInPlace(D[] array, Comparator<D> cmp, int start, int length, int bufferLen) {
        for(int mergeLen = 2; mergeLen < bufferLen; mergeLen *= 2) {
            var fullMerge = 2 * mergeLen;
            int mergeIndex;
            var mergeEnd = start + length - fullMerge;

            for(mergeIndex = start; mergeIndex <= mergeEnd; mergeIndex += fullMerge) grailMergeForwards(array, cmp, mergeIndex, mergeLen, mergeLen, mergeLen);

            var leftOver = length - (mergeIndex - start);

            if(leftOver > mergeLen) grailMergeForwards(array, cmp, mergeIndex, mergeLen, leftOver - mergeLen, mergeLen);
            else grailRotate(array, mergeIndex - mergeLen, mergeLen, leftOver);

            start -= mergeLen;
        }

        var fullMerge  = 2 * bufferLen;
        var lastBlock  = length % fullMerge;
        var lastOffset = start + length - lastBlock;

        if(lastBlock <= bufferLen) grailRotate(array, lastOffset, lastBlock, bufferLen);
        else grailMergeBackwards(array, cmp, lastOffset, bufferLen, lastBlock - bufferLen, bufferLen);

        for(int mergeIndex = lastOffset - fullMerge; mergeIndex >= start; mergeIndex -= fullMerge) grailMergeBackwards(array, cmp, mergeIndex, bufferLen, bufferLen, bufferLen);
    }

    private static <D> void grailBuildBlocks(D[] array, Comparator<D> cmp, int start, int length, int bufferLen) {
        grailPairwiseSwaps(array, cmp, start, length);
        grailBuildInPlace(array, cmp, start - 2, length, bufferLen);
    }

    private static <D> int grailBlockSelectSort(D[] array, Comparator<D> cmp, int start, int medianKey, int blockCount, int blockLen) {
        for(int firstBlock = 0; firstBlock < blockCount; firstBlock++) {
            var selectBlock = firstBlock;

            for(int currBlock = firstBlock + 1; currBlock < blockCount; currBlock++) {
                var compare = cmp.compare(array[start + (currBlock   * blockLen)], array[start + (selectBlock * blockLen)]);

                if(compare < 0 || (compare == 0 && cmp.compare(array[currBlock], array[selectBlock]) < 0)) selectBlock = currBlock;
            }

            if(selectBlock != firstBlock) {
                grailBlockSwap(array, start + (firstBlock * blockLen), start + (selectBlock * blockLen), blockLen);
                swap(array, firstBlock, selectBlock);

                if(medianKey == firstBlock) medianKey = selectBlock;
                else if(medianKey == selectBlock) medianKey = firstBlock;
            }
        }

        return medianKey;
    }

    private static <D> void grailInPlaceBufferReset(D[] array, int start, int length, int bufferOffset) {
        var buffer = start + length - 1;
        var index = buffer - bufferOffset;

        while(buffer >= start) swap(array, index--, buffer--);
    }

    private static <D> void grailInPlaceBufferRewind(D[] array, int start, int leftBlock, int buffer) {
        while(leftBlock >= start) swap(array, buffer--, leftBlock--);
    }

    private static <D> boolean grailGetSubarray(D[] array, Comparator<D> cmp, int currentKey, int medianKey) {
        return cmp.compare(array[currentKey], array[medianKey]) >= 0;
    }

    private static <D> int grailCountLastMergeBlocks(D[] array, Comparator<D> cmp, int offset, int blockCount, int blockLen) {
        var blocksToMerge = 0;
        var lastRightFrag = offset + (blockCount * blockLen);
        var prevLeftBlock = lastRightFrag - blockLen;

        while(blocksToMerge < blockCount && cmp.compare(array[lastRightFrag], array[prevLeftBlock]) < 0) {
            blocksToMerge++;
            prevLeftBlock -= blockLen;
        }

        return blocksToMerge;
    }

    private static <D> void grailSmartMerge(D[] array, Comparator<D> cmp, int start, int leftLen, boolean leftOrigin, int rightLen, int bufferOffset, int[] currBlockLen, boolean[] currBlockOrigin) {
        var buffer = start - bufferOffset;
        var left = start;
        var middle = start + leftLen;
        var right = middle;
        var end = middle + rightLen;

        if (leftOrigin) {
            while(left < middle && right < end) {
                if(cmp.compare(array[left], array[right]) <  0) swap(array, buffer, left++);
                else swap(array, buffer, right++);

                buffer++;
            }
        } else {
            while(left < middle && right < end) {
                if(cmp.compare(array[left], array[right]) <= 0) swap(array, buffer, left++);
                else swap(array, buffer, right++);

                buffer++;
            }
        }

        if(left < middle) {
            currBlockLen[0] = middle - left;
            grailInPlaceBufferRewind(array, left, middle - 1, end - 1);
        } else {
            currBlockLen[0] = end - right;
            currBlockOrigin[0] = !leftOrigin;
        }
    }

    private static <D> void grailSmartLazyMerge(D[] array, Comparator<D> cmp, int start, int leftLen, boolean leftOrigin, int rightLen, int[] currBlockLen, boolean[] currBlockOrigin) {
        var middle = start + leftLen;

        if(!leftOrigin) {
            if(cmp.compare(array[middle - 1], array[middle]) >  0) {
                while(leftLen != 0) {
                    var mergeLen = grailBinarySearchLeft(array, cmp, middle, rightLen, array[start]);

                    if(mergeLen != 0) {
                        grailRotate(array, start, leftLen, mergeLen);
                        start    += mergeLen;
                        middle   += mergeLen;
                        rightLen -= mergeLen;
                    }
                    if(rightLen == 0) {
                        currBlockLen[0] = leftLen;

                        return;
                    } else {
                        do {
                            start++;
                            leftLen--;
                        } while(leftLen != 0 && cmp.compare(array[start ], array[middle]) <= 0);
                    }
                }
            }
        } else {
            if(cmp.compare(array[middle - 1], array[middle]) >= 0) {
                while(leftLen != 0) {
                    var mergeLen = grailBinarySearchRight(array, cmp, middle, rightLen, array[start]);

                    if(mergeLen != 0) {
                        grailRotate(array, start, leftLen, mergeLen);
                        start    += mergeLen;
                        middle   += mergeLen;
                        rightLen -= mergeLen;
                    }
                    if(rightLen == 0) {
                        currBlockLen[0] = leftLen;

                        return;
                    } else {
                        do {
                            start++;
                            leftLen--;
                        } while(leftLen != 0 && cmp.compare(array[start ], array[middle]) < 0);
                    }
                }
            }
        }

        currBlockLen[0] = rightLen;
        currBlockOrigin[0] = !leftOrigin;
    }

    private static <D> void grailMergeBlocks(D[] array, Comparator<D> cmp, int medianKey, int start, int blockCount, int blockLen, int lastMergeBlocks, int lastLen) {
        int buffer, currBlock;
        var nextBlock = start + blockLen;
        int[] currBlockLen    = {blockLen};
        boolean[] currBlockOrigin = {grailGetSubarray(array, cmp, 0, medianKey)};

        for(int keyIndex = 1; keyIndex < blockCount; keyIndex++, nextBlock += blockLen) {
            boolean nextBlockOrigin;
            currBlock = nextBlock - currBlockLen[0];
            nextBlockOrigin = grailGetSubarray(array, cmp, keyIndex, medianKey);

            if(nextBlockOrigin == currBlockOrigin[0]) {
                buffer = currBlock - blockLen;
                grailBlockSwap(array, buffer, currBlock, currBlockLen[0]);
                currBlockLen[0] = blockLen;
            } else grailSmartMerge(array, cmp, currBlock, currBlockLen[0], currBlockOrigin[0], blockLen, blockLen, currBlockLen, currBlockOrigin);
        }

        currBlock = nextBlock - currBlockLen[0];
        buffer = currBlock - blockLen;

        if(lastLen != 0) {
            if(currBlockOrigin[0]) {
                grailBlockSwap(array, buffer, currBlock, currBlockLen[0]);
                currBlock = nextBlock;
                currBlockLen[0] = blockLen * lastMergeBlocks;
                currBlockOrigin[0] = false;
            } else currBlockLen[0] += blockLen * lastMergeBlocks;

            grailMergeForwards(array, cmp, currBlock, currBlockLen[0], lastLen, blockLen);
        } else grailBlockSwap(array, buffer, currBlock, currBlockLen[0]);
    }

    private static <D> void grailLazyMergeBlocks(D[] array, Comparator<D> cmp, int medianKey, int start, int blockCount, int blockLen, int lastMergeBlocks, int lastLen) {
        int currBlock;
        var nextBlock = start + blockLen;
        int[] currBlockLen    = {blockLen};
        boolean[] currBlockOrigin = {grailGetSubarray(array, cmp, 0, medianKey)};

        for(int keyIndex = 1; keyIndex < blockCount; keyIndex++, nextBlock += blockLen) {
            boolean nextBlockOrigin;
            currBlock = nextBlock - currBlockLen[0];
            nextBlockOrigin = grailGetSubarray(array, cmp, keyIndex, medianKey);

            if(nextBlockOrigin == currBlockOrigin[0]) currBlockLen[0] = blockLen;
            else if(blockLen != 0 && currBlockLen[0] != 0) grailSmartLazyMerge(array, cmp, currBlock, currBlockLen[0], currBlockOrigin[0], blockLen, currBlockLen, currBlockOrigin);
        }

        currBlock = nextBlock - currBlockLen[0];

        if(lastLen != 0) {
            if(currBlockOrigin[0]) {
                currBlock = nextBlock;
                currBlockLen[0] = blockLen * lastMergeBlocks;
                currBlockOrigin[0] = false;
            } else currBlockLen[0] += blockLen * lastMergeBlocks;

            grailLazyMerge(array, cmp, currBlock, currBlockLen[0], lastLen);
        }
    }

    private static <D> void grailCombineInPlace(D[] array, Comparator<D> cmp, int start, int length, int subarrayLen, int blockLen, int mergeCount, int lastSubarrays, boolean buffer) {
        var fullMerge  = 2 * subarrayLen;
        final var blockCount = fullMerge / blockLen;

        for(int mergeIndex = 0; mergeIndex < mergeCount; mergeIndex++) {
            var offset = start + (mergeIndex * fullMerge);
            grailInsertSort(array, cmp, blockCount);
            var medianKey = subarrayLen / blockLen;
            medianKey = grailBlockSelectSort(array, cmp, offset, medianKey, blockCount, blockLen);

            if(buffer) grailMergeBlocks(array, cmp, medianKey, offset, blockCount, blockLen, 0, 0);
            else grailLazyMergeBlocks(array, cmp, medianKey, offset, blockCount, blockLen, 0, 0);
        }

        if(lastSubarrays != 0) {
            var offsetFull = start + (mergeCount * fullMerge);
            var blockCountFull = lastSubarrays / blockLen;
            grailInsertSort(array, cmp, blockCountFull + 1);
            var medianKey = subarrayLen / blockLen;
            medianKey = grailBlockSelectSort(array, cmp, offsetFull, medianKey, blockCountFull, blockLen);
            var lastFragment = lastSubarrays - (blockCountFull * blockLen);
            var lastMergeBlocks = lastFragment != 0 ? grailCountLastMergeBlocks(array, cmp, offsetFull, blockCountFull, blockLen) : 0;
            var smartMerges = blockCountFull - lastMergeBlocks;

            if(smartMerges == 0) {
                var leftLen = lastMergeBlocks * blockLen;

                if(buffer) grailMergeForwards(array, cmp, offsetFull, leftLen, lastFragment, blockLen);
                else grailLazyMerge(array, cmp, offsetFull, leftLen, lastFragment);
            } else {
                if(buffer) grailMergeBlocks(array, cmp, medianKey, offsetFull, smartMerges, blockLen, lastMergeBlocks, lastFragment);
                else grailLazyMergeBlocks(array, cmp, medianKey, offsetFull, smartMerges, blockLen, lastMergeBlocks, lastFragment);
            }
        }

        if(buffer) grailInPlaceBufferReset(array, start, length, blockLen);
    }

    private static <D> void grailCombineBlocks(D[] array, Comparator<D> cmp, int start, int length, int subarrayLen, int blockLen, boolean buffer) {
        var fullMerge = 2 * subarrayLen;
        var mergeCount = length /  fullMerge;
        var lastSubarrays = length - (fullMerge * mergeCount);

        if(lastSubarrays <= subarrayLen) {
            length -= lastSubarrays;
            lastSubarrays = 0;
        }

        grailCombineInPlace(array, cmp, start, length, subarrayLen, blockLen, mergeCount, lastSubarrays, buffer);
    }

    private static <D> void grailLazyMerge(D[] array, Comparator<D> cmp, int start, int leftLen, int rightLen) {
        if(leftLen < rightLen) {
            var middle = start + leftLen;

            while(leftLen != 0) {
                var mergeLen = grailBinarySearchLeft(array, cmp, middle, rightLen, array[start]);

                if(mergeLen != 0) {
                    grailRotate(array, start, leftLen, mergeLen);
                    start    += mergeLen;
                    middle   += mergeLen;
                    rightLen -= mergeLen;
                }

                if(rightLen == 0) break;
                else {
                    do {
                        start++;
                        leftLen--;
                    } while(leftLen != 0 && cmp.compare(array[start ], array[middle]) <= 0);
                }
            }
        } else {
            var end = start + leftLen + rightLen - 1;

            while(rightLen != 0) {
                var mergeLen = grailBinarySearchRight(array, cmp, start, leftLen, array[end]);

                if(mergeLen != leftLen) {
                    grailRotate(array, start + mergeLen, leftLen - mergeLen, rightLen);
                    end -=  leftLen - mergeLen;
                    leftLen = mergeLen;
                }

                if(leftLen == 0) break;
                else {
                    var middle = start + leftLen;

                    do {
                        rightLen--;
                        end--;
                    } while(rightLen != 0 && cmp.compare(array[middle - 1], array[end]) <= 0);
                }
            }
        }
    }

    private static <D> void grailLazyStableSort(D[] array, Comparator<D> cmp, int length) {
        for(int index = 1; index < length; index += 2) {
            var left = index - 1;

            if(cmp.compare(array[left], array[index]) > 0) swap(array, left, index);
        }
        for(int mergeLen = 2; mergeLen < length; mergeLen *= 2) {
            var fullMerge = 2 * mergeLen;
            int mergeIndex;
            var mergeEnd = length - fullMerge;

            for(mergeIndex = 0; mergeIndex <= mergeEnd; mergeIndex += fullMerge) grailLazyMerge(array, cmp, mergeIndex, mergeLen, mergeLen);

            var leftOver = length - mergeIndex;

            if(leftOver > mergeLen) grailLazyMerge(array, cmp, mergeIndex, mergeLen, leftOver - mergeLen);
        }
    }

    public static <D> void grailCommonSort(D[] array, Comparator<D> cmp) {
        if(array.length < 16) {
            Sort.binaryInsertion(array, cmp);

            return;
        }

        var blockLen = 4;

        while((blockLen * blockLen) < array.length) blockLen *= 2;

        var keyLen = (array.length - 1) / blockLen + 1;
        var idealKeys = keyLen + blockLen;
        var keysFound = grailCollectKeys(array, cmp, array.length, idealKeys);

        boolean idealBuffer;
        if(keysFound < idealKeys) {
            if(keysFound < 4) {
                if(keysFound == 1) return;

                grailLazyStableSort(array, cmp, array.length);

                return;
            } else {
                keyLen = blockLen;
                blockLen = 0;
                idealBuffer = false;

                while(keyLen > keysFound) keyLen /= 2;
            }
        } else idealBuffer = true;

        var bufferEnd = blockLen + keyLen;
        var subarrayLen = idealBuffer ? blockLen : keyLen;
        grailBuildBlocks(array, cmp, bufferEnd, array.length - bufferEnd, subarrayLen);

        while((array.length - bufferEnd) > (2 * subarrayLen)) {
            subarrayLen *= 2;
            var currentBlockLen = blockLen;
            var scrollingBuffer = idealBuffer;

            if(!idealBuffer) {
                var keyBuffer = keyLen / 2;
                if(keyBuffer >= ((2 * subarrayLen) / keyBuffer)) {
                    currentBlockLen = keyBuffer;
                    scrollingBuffer = true;
                } else currentBlockLen = (2 * subarrayLen) / keyLen;
            }

            grailCombineBlocks(array, cmp, bufferEnd, array.length - bufferEnd, subarrayLen, currentBlockLen, scrollingBuffer);
        }

        grailInsertSort(array, cmp, bufferEnd);
        grailLazyMerge(array, cmp, 0, bufferEnd, array.length - bufferEnd);
    }
}