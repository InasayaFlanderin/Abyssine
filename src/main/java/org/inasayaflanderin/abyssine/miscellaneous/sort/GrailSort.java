package org.inasayaflanderin.abyssine.miscellaneous.sort;

import java.util.Comparator;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.copy;
import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.swap;

public class GrailSort {
    private static <D> void grailBlockSwap(D[] data, int a, int b, int blockLen) {
        for(int i = 0; i < blockLen; i++) swap(data, a + i, b + i);
    }

    private static <D> void grailRotate(D[] data, int start, int leftLen, int rightLen) {
        while(leftLen > 0 && rightLen > 0) {
            if(leftLen <= rightLen) {
                grailBlockSwap(data, start, start + leftLen, leftLen);
                start += leftLen;
                rightLen -= leftLen;
            } else {
                grailBlockSwap(data, start + leftLen - rightLen, start + leftLen, rightLen);
                leftLen -= rightLen;
            }
        }
    }

    private static <D> void grailInsertSort(D[] data, Comparator<D> comparator, int length) {
        for(int item = 1; item < length; item++) {
            var left = item - 1;
            var right = item;

            while(left >= 0 && comparator.compare(data[left], data[right]) > 0) swap(data, left--, right--);
        }
    }


    private static <D> int grailBinarySearchLeft(D[] data, Comparator<D> comparator, int start, int length, D target) {
        var left = 0;
        var right = length;

        while(left < right) {
            var middle = (left + right) >>> 1;

            if(comparator.compare(data[start + middle], target) < 0) left = middle + 1;
            else right = middle;
        }

        return left;
    }

    private static <D> int grailBinarySearchRight(D[] data, Comparator<D> comparator, int start, int length, D target) {
        var left = 0;
        var right = length;

        while(left < right) {
            int middle = (left + right) >>> 1;

            if(comparator.compare(data[start + middle], target) > 0) right = middle;
            else left = middle + 1;
        }

        return right;
    }

    private static <D> int binarySearchExclusive(D[] data, Comparator<D> comparator, int start, int length, D target) {
        var left = 0;
        var right = length;

        while(left < right) {
            var middle = (left + right) >>> 1;
            var comp = comparator.compare(data[start + middle], target);

            if(comp == 0) return -1;
            else if(comp < 0) left = middle + 1;
            else right = middle;
        }

        return left;
    }

    private static <D> void insertBackwards(D[] data, int start, int length) {
        var item = data[start + length];
        copy(data, start, data, start + 1, length);
        data[start] = item;
    }

    private static <D> int grailCollectKeys(D[] data, Comparator<D> comparator, int length, int idealKeys) {
        var keysFound = 1;
        var firstKey = 0;
        var currKey = 1;

        while(currKey < length && keysFound < idealKeys) {
            var insertPos = binarySearchExclusive(data, comparator, firstKey, keysFound, data[currKey]);

            if(insertPos != -1) {
                grailRotate(data, firstKey, keysFound, currKey - (firstKey + keysFound));
                firstKey = currKey - keysFound;

                if(keysFound != insertPos) insertBackwards(data, firstKey + insertPos, keysFound - insertPos);

                keysFound++;
            }

            currKey++;
        }

        grailRotate(data, 0, firstKey, keysFound);

        return keysFound;
    }

    private static <D> void grailPairwiseSwaps(D[] data, Comparator<D> comparator, int start, int length) {
        var firstKey = data[start - 1];
        var secondKey = data[start - 2];
        sortPairs(data, comparator, start, length);
        data[start + length - 2] =  firstKey;
        data[start + length - 1] = secondKey;
    }

    private static <D> void sortPairs(D[] data, Comparator<D> comparator, int start, int length) {
        int index;

        for(index = 1; index < length; index += 2) {
            var left = start + index - 1;
            var right = start + index;

            if(comparator.compare(data[left], data[right]) > 0) {
                data[left - 2] = data[right];
                data[right - 2] = data[left];
            } else {
                data[left - 2] = data[left];
                data[right - 2] = data[right];
            }
        }

        var left = start + index - 1;

        if(left < start + length) data[left - 2] = data[left];
    }

    private static <D> void grailMergeForwards(D[] data, Comparator<D> comparator, int start, int leftLen, int rightLen, int bufferOffset) {
        var buffer = start - bufferOffset;
        var left = start;
        var middle = start + leftLen;
        var right = middle;
        var end = middle + rightLen;

        while(right < end) {
            if(left == middle || comparator.compare(data[left], data[right]) > 0) swap(data, buffer, right++);
            else swap(data, buffer,  left++);

            buffer++;
        }

        if(buffer != left) grailBlockSwap(data, buffer, left, middle - left);
    }

    private static <D> void grailMergeBackwards(D[] data, Comparator<D> comparator, int start, int leftLen, int rightLen, int bufferOffset) {
        var end = start - 1;
        var left = end + leftLen;
        var middle = left;
        var right = middle + rightLen;
        var buffer = right + bufferOffset;

        while(left > end) {
            if(right == middle || comparator.compare(data[ left], data[right]) > 0) swap(data, buffer,  left--);
            else swap(data, buffer, right--);

            buffer--;
        }

        if(right != buffer) while(right > middle) swap(data, buffer--, right--);
    }

    private static <D> void grailBuildInPlace(D[] data, Comparator<D> comparator, int start, int length, int bufferLen) {
        for(int mergeLen = 2; mergeLen < bufferLen; mergeLen *= 2) {
            var fullMerge = 2 * mergeLen;
            int mergeIndex;
            var mergeEnd = start + length - fullMerge;

            for(mergeIndex = start; mergeIndex <= mergeEnd; mergeIndex += fullMerge) grailMergeForwards(data, comparator, mergeIndex, mergeLen, mergeLen, mergeLen);

            var leftOver = length - (mergeIndex - start);

            if(leftOver > mergeLen) grailMergeForwards(data, comparator, mergeIndex, mergeLen, leftOver - mergeLen, mergeLen);
            else grailRotate(data, mergeIndex - mergeLen, mergeLen, leftOver);

            start -= mergeLen;
        }

        var fullMerge  = 2 * bufferLen;
        var lastBlock  = length % fullMerge;
        var lastOffset = start + length - lastBlock;

        if(lastBlock <= bufferLen) grailRotate(data, lastOffset, lastBlock, bufferLen);
        else grailMergeBackwards(data, comparator, lastOffset, bufferLen, lastBlock - bufferLen, bufferLen);

        for(int mergeIndex = lastOffset - fullMerge; mergeIndex >= start; mergeIndex -= fullMerge) grailMergeBackwards(data, comparator, mergeIndex, bufferLen, bufferLen, bufferLen);
    }

    private static <D> void grailBuildBlocks(D[] data, Comparator<D> comparator, int start, int length, int bufferLen) {
        grailPairwiseSwaps(data, comparator, start, length);
        grailBuildInPlace(data, comparator, start - 2, length, bufferLen);
    }

    private static <D> int grailBlockSelectSort(D[] data, Comparator<D> comparator, int start, int medianKey, int blockCount, int blockLen) {
        for(int firstBlock = 0; firstBlock < blockCount; firstBlock++) {
            var selectBlock = firstBlock;

            for(int currBlock = firstBlock + 1; currBlock < blockCount; currBlock++) {
                var compare = comparator.compare(data[start + (currBlock   * blockLen)], data[start + (selectBlock * blockLen)]);

                if(compare < 0 || (compare == 0 && comparator.compare(data[currBlock], data[selectBlock]) < 0)) selectBlock = currBlock;
            }

            if(selectBlock != firstBlock) {
                grailBlockSwap(data, start + (firstBlock * blockLen), start + (selectBlock * blockLen), blockLen);
                swap(data, firstBlock, selectBlock);

                if(medianKey == firstBlock) medianKey = selectBlock;
                else if(medianKey == selectBlock) medianKey = firstBlock;
            }
        }

        return medianKey;
    }

    private static <D> void grailInPlaceBufferReset(D[] data, int start, int length, int bufferOffset) {
        var buffer = start + length - 1;
        var index = buffer - bufferOffset;

        while(buffer >= start) swap(data, index--, buffer--);
    }

    private static <D> void grailInPlaceBufferRewind(D[] data, int start, int leftBlock, int buffer) {
        while(leftBlock >= start) swap(data, buffer--, leftBlock--);
    }

    private static <D> boolean grailGetSubarray(D[] data, Comparator<D> comparator, int currentKey, int medianKey) {
        return comparator.compare(data[currentKey], data[medianKey]) >= 0;
    }

    private static <D> int grailCountLastMergeBlocks(D[] data, Comparator<D> comparator, int offset, int blockCount, int blockLen) {
        var blocksToMerge = 0;
        var lastRightFrag = offset + (blockCount * blockLen);
        var prevLeftBlock = lastRightFrag - blockLen;

        while(blocksToMerge < blockCount && comparator.compare(data[lastRightFrag], data[prevLeftBlock]) < 0) {
            blocksToMerge++;
            prevLeftBlock -= blockLen;
        }

        return blocksToMerge;
    }

    private static <D> void grailSmartMerge(D[] data, Comparator<D> comparator, int start, int leftLen, boolean leftOrigin, int rightLen, int bufferOffset, int[] currBlockLen, boolean[] currBlockOrigin) {
        var buffer = start - bufferOffset;
        var left = start;
        var middle = start + leftLen;
        var right = middle;
        var end = middle + rightLen;

        if (leftOrigin) {
            while(left < middle && right < end) {
                if(comparator.compare(data[left], data[right]) <  0) swap(data, buffer, left++);
                else swap(data, buffer, right++);

                buffer++;
            }
        } else {
            while(left < middle && right < end) {
                if(comparator.compare(data[left], data[right]) <= 0) swap(data, buffer, left++);
                else swap(data, buffer, right++);

                buffer++;
            }
        }

        if(left < middle) {
            currBlockLen[0] = middle - left;
            grailInPlaceBufferRewind(data, left, middle - 1, end - 1);
        } else {
            currBlockLen[0] = end - right;
            currBlockOrigin[0] = !leftOrigin;
        }
    }

    private static <D> void grailSmartLazyMerge(D[] data, Comparator<D> comparator, int start, int leftLen, boolean leftOrigin, int rightLen, int[] currBlockLen, boolean[] currBlockOrigin) {
        var middle = start + leftLen;

        if(!leftOrigin) {
            if(comparator.compare(data[middle - 1], data[middle]) >  0) {
                while(leftLen != 0) {
                    var mergeLen = grailBinarySearchLeft(data, comparator, middle, rightLen, data[start]);

                    if(mergeLen != 0) {
                        grailRotate(data, start, leftLen, mergeLen);
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
                        } while(leftLen != 0 && comparator.compare(data[start ], data[middle]) <= 0);
                    }
                }
            }
        } else {
            if(comparator.compare(data[middle - 1], data[middle]) >= 0) {
                while(leftLen != 0) {
                    var mergeLen = grailBinarySearchRight(data, comparator, middle, rightLen, data[start]);

                    if(mergeLen != 0) {
                        grailRotate(data, start, leftLen, mergeLen);
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
                        } while(leftLen != 0 && comparator.compare(data[start ], data[middle]) < 0);
                    }
                }
            }
        }

        currBlockLen[0] = rightLen;
        currBlockOrigin[0] = !leftOrigin;
    }

    private static <D> void grailMergeBlocks(D[] data, Comparator<D> comparator, int medianKey, int start, int blockCount, int blockLen, int lastMergeBlocks, int lastLen) {
        int buffer, currBlock;
        var nextBlock = start + blockLen;
        int[] currBlockLen    = {blockLen};
        boolean[] currBlockOrigin = {grailGetSubarray(data, comparator, 0, medianKey)};

        for(int keyIndex = 1; keyIndex < blockCount; keyIndex++, nextBlock += blockLen) {
            boolean nextBlockOrigin;
            currBlock = nextBlock - currBlockLen[0];
            nextBlockOrigin = grailGetSubarray(data, comparator, keyIndex, medianKey);

            if(nextBlockOrigin == currBlockOrigin[0]) {
                buffer = currBlock - blockLen;
                grailBlockSwap(data, buffer, currBlock, currBlockLen[0]);
                currBlockLen[0] = blockLen;
            } else grailSmartMerge(data, comparator, currBlock, currBlockLen[0], currBlockOrigin[0], blockLen, blockLen, currBlockLen, currBlockOrigin);
        }

        currBlock = nextBlock - currBlockLen[0];
        buffer = currBlock - blockLen;

        if(lastLen != 0) {
            if(currBlockOrigin[0]) {
                grailBlockSwap(data, buffer, currBlock, currBlockLen[0]);
                currBlock = nextBlock;
                currBlockLen[0] = blockLen * lastMergeBlocks;
                currBlockOrigin[0] = false;
            } else currBlockLen[0] += blockLen * lastMergeBlocks;

            grailMergeForwards(data, comparator, currBlock, currBlockLen[0], lastLen, blockLen);
        } else grailBlockSwap(data, buffer, currBlock, currBlockLen[0]);
    }

    private static <D> void grailLazyMergeBlocks(D[] data, Comparator<D> comparator, int medianKey, int start, int blockCount, int blockLen, int lastMergeBlocks, int lastLen) {
        int currBlock;
        var nextBlock = start + blockLen;
        int[] currBlockLen    = {blockLen};
        boolean[] currBlockOrigin = {grailGetSubarray(data, comparator, 0, medianKey)};

        for(int keyIndex = 1; keyIndex < blockCount; keyIndex++, nextBlock += blockLen) {
            boolean nextBlockOrigin;
            currBlock = nextBlock - currBlockLen[0];
            nextBlockOrigin = grailGetSubarray(data, comparator, keyIndex, medianKey);

            if(nextBlockOrigin == currBlockOrigin[0]) currBlockLen[0] = blockLen;
            else if(blockLen != 0 && currBlockLen[0] != 0) grailSmartLazyMerge(data, comparator, currBlock, currBlockLen[0], currBlockOrigin[0], blockLen, currBlockLen, currBlockOrigin);
        }

        currBlock = nextBlock - currBlockLen[0];

        if(lastLen != 0) {
            if(currBlockOrigin[0]) {
                currBlock = nextBlock;
                currBlockLen[0] = blockLen * lastMergeBlocks;
                currBlockOrigin[0] = false;
            } else currBlockLen[0] += blockLen * lastMergeBlocks;

            grailLazyMerge(data, comparator, currBlock, currBlockLen[0], lastLen);
        }
    }

    private static <D> void grailCombineInPlace(D[] data, Comparator<D> comparator, int start, int length, int subarrayLen, int blockLen, int mergeCount, int lastSubarrays, boolean buffer) {
        var fullMerge  = 2 * subarrayLen;
        final var blockCount = fullMerge / blockLen;

        for(int mergeIndex = 0; mergeIndex < mergeCount; mergeIndex++) {
            var offset = start + (mergeIndex * fullMerge);
            grailInsertSort(data, comparator, blockCount);
            var medianKey = subarrayLen / blockLen;
            medianKey = grailBlockSelectSort(data, comparator, offset, medianKey, blockCount, blockLen);

            if(buffer) grailMergeBlocks(data, comparator, medianKey, offset, blockCount, blockLen, 0, 0);
            else grailLazyMergeBlocks(data, comparator, medianKey, offset, blockCount, blockLen, 0, 0);
        }

        if(lastSubarrays != 0) {
            var offsetFull = start + (mergeCount * fullMerge);
            var blockCountFull = lastSubarrays / blockLen;
            grailInsertSort(data, comparator, blockCountFull + 1);
            var medianKey = subarrayLen / blockLen;
            medianKey = grailBlockSelectSort(data, comparator, offsetFull, medianKey, blockCountFull, blockLen);
            var lastFragment = lastSubarrays - (blockCountFull * blockLen);
            var lastMergeBlocks = lastFragment != 0 ? grailCountLastMergeBlocks(data, comparator, offsetFull, blockCountFull, blockLen) : 0;
            var smartMerges = blockCountFull - lastMergeBlocks;

            if(smartMerges == 0) {
                var leftLen = lastMergeBlocks * blockLen;

                if(buffer) grailMergeForwards(data, comparator, offsetFull, leftLen, lastFragment, blockLen);
                else grailLazyMerge(data, comparator, offsetFull, leftLen, lastFragment);
            } else {
                if(buffer) grailMergeBlocks(data, comparator, medianKey, offsetFull, smartMerges, blockLen, lastMergeBlocks, lastFragment);
                else grailLazyMergeBlocks(data, comparator, medianKey, offsetFull, smartMerges, blockLen, lastMergeBlocks, lastFragment);
            }
        }

        if(buffer) grailInPlaceBufferReset(data, start, length, blockLen);
    }

    private static <D> void grailCombineBlocks(D[] data, Comparator<D> comparator, int start, int length, int subarrayLen, int blockLen, boolean buffer) {
        var fullMerge = 2 * subarrayLen;
        var mergeCount = length /  fullMerge;
        var lastSubarrays = length - (fullMerge * mergeCount);

        if(lastSubarrays <= subarrayLen) {
            length -= lastSubarrays;
            lastSubarrays = 0;
        }

        grailCombineInPlace(data, comparator, start, length, subarrayLen, blockLen, mergeCount, lastSubarrays, buffer);
    }

    private static <D> void grailLazyMerge(D[] data, Comparator<D> comparator, int start, int leftLen, int rightLen) {
        if(leftLen < rightLen) {
            var middle = start + leftLen;

            while(leftLen != 0) {
                var mergeLen = grailBinarySearchLeft(data, comparator, middle, rightLen, data[start]);

                if(mergeLen != 0) {
                    grailRotate(data, start, leftLen, mergeLen);
                    start    += mergeLen;
                    middle   += mergeLen;
                    rightLen -= mergeLen;
                }

                if(rightLen == 0) break;
                else {
                    do {
                        start++;
                        leftLen--;
                    } while(leftLen != 0 && comparator.compare(data[start ], data[middle]) <= 0);
                }
            }
        } else {
            var end = start + leftLen + rightLen - 1;

            while(rightLen != 0) {
                var mergeLen = grailBinarySearchRight(data, comparator, start, leftLen, data[end]);

                if(mergeLen != leftLen) {
                    grailRotate(data, start + mergeLen, leftLen - mergeLen, rightLen);
                    end -=  leftLen - mergeLen;
                    leftLen = mergeLen;
                }

                if(leftLen == 0) break;
                else {
                    var middle = start + leftLen;

                    do {
                        rightLen--;
                        end--;
                    } while(rightLen != 0 && comparator.compare(data[middle - 1], data[end]) <= 0);
                }
            }
        }
    }

    private static <D> void grailLazyStableSort(D[] data, Comparator<D> comparator, int length) {
        for(int index = 1; index < length; index += 2) {
            var left = index - 1;

            if(comparator.compare(data[left], data[index]) > 0) swap(data, left, index);
        }
        for(int mergeLen = 2; mergeLen < length; mergeLen *= 2) {
            var fullMerge = 2 * mergeLen;
            int mergeIndex;
            var mergeEnd = length - fullMerge;

            for(mergeIndex = 0; mergeIndex <= mergeEnd; mergeIndex += fullMerge) grailLazyMerge(data, comparator, mergeIndex, mergeLen, mergeLen);

            var leftOver = length - mergeIndex;

            if(leftOver > mergeLen) grailLazyMerge(data, comparator, mergeIndex, mergeLen, leftOver - mergeLen);
        }
    }

    public static <D> void grailCommonSort(D[] data, Comparator<D> comparator) {
        if(data.length < 16) {
            Sort.binaryInsertion(data, comparator);

            return;
        }

        var blockLen = 4;

        while((blockLen * blockLen) < data.length) blockLen *= 2;

        var keyLen = (data.length - 1) / blockLen + 1;
        var idealKeys = keyLen + blockLen;
        var keysFound = grailCollectKeys(data, comparator, data.length, idealKeys);

        boolean idealBuffer;
        if(keysFound < idealKeys) {
            if(keysFound < 4) {
                if(keysFound == 1) return;

                grailLazyStableSort(data, comparator, data.length);

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
        grailBuildBlocks(data, comparator, bufferEnd, data.length - bufferEnd, subarrayLen);

        while((data.length - bufferEnd) > (2 * subarrayLen)) {
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

            grailCombineBlocks(data, comparator, bufferEnd, data.length - bufferEnd, subarrayLen, currentBlockLen, scrollingBuffer);
        }

        grailInsertSort(data, comparator, bufferEnd);
        grailLazyMerge(data, comparator, 0, bufferEnd, data.length - bufferEnd);
    }
}