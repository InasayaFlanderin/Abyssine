package org.inasayaflanderin.abyssine.miscellaneous.sort;

import java.util.Comparator;

public class GrailSort<D> {
    private int currBlockLen;
    private boolean currBlockOrigin;

    private static <D> void grailSwap(D[] array, int a, int b) {
        D temp   = array[a];
        array[a] = array[b];
        array[b] = temp;
    }

    private static <D> void grailBlockSwap(D[] array, int a, int b, int blockLen) {
        for(int i = 0; i < blockLen; i++) {
            grailSwap(array, a + i, b + i);
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

    private static <D> void grailInsertSort(D[] array, int length, Comparator<D> cmp) {
        for(int item = 1; item < length; item++) {
            int  left = item - 1;
            int right = item;

            while(left >= 0 && cmp.compare(array[ left],
                    array[right]) > 0) {
                grailSwap(array, left, right);
                left--;
                right--;
            }
        }
    }


    private static <D> int grailBinarySearchLeft(D[] array, int start, int length, D target, Comparator<D> cmp) {
        int  left = 0;
        int right = length;

        while(left < right) {
            int middle = left + ((right - left) / 2);

            if(cmp.compare(array[start + middle], target) < 0) {
                left = middle + 1;
            }
            else {
                right = middle;
            }
        }
        return left;
    }

    private static <D> int grailBinarySearchRight(D[] array, int start, int length, D target, Comparator<D> cmp) {
        int  left = 0;
        int right = length;

        while(left < right) {
            int middle = left + ((right - left) / 2);

            if(cmp.compare(array[start + middle], target) > 0) {
                right = middle;
            }
            else {
                left = middle + 1;
            }
        }

        return right;
    }

    private static <D> int grailCollectKeys(D[] array, int length, int idealKeys, Comparator<D> cmp) {
        int keysFound = 1;
        int  firstKey = 0;
        int   currKey = 1;

        while(currKey < length && keysFound < idealKeys) {
            int insertPos = grailBinarySearchLeft(array, firstKey, keysFound, array[currKey], cmp);
            if(insertPos == keysFound || cmp.compare(array[currKey], array[firstKey + insertPos]) != 0) {
                grailRotate(array, firstKey, keysFound, currKey - (firstKey + keysFound));
                firstKey = currKey - keysFound;
                grailRotate(array, firstKey + insertPos, keysFound - insertPos, 1);
                keysFound++;
            }
            currKey++;
        }

        grailRotate(array, 0, firstKey, keysFound);
        return keysFound;
    }


    private static <D> void grailPairwiseSwaps(D[] array, int start, int length, Comparator<D> cmp) {
        int index;
        for(index = 1; index < length; index += 2) {
            int  left = start + index - 1;
            int right = start + index;

            if(cmp.compare(array[left], array[right]) > 0) {
                grailSwap(array,  left - 2, right);
                grailSwap(array, right - 2,  left);
            }
            else {
                grailSwap(array,  left - 2,  left);
                grailSwap(array, right - 2, right);
            }
        }

        int left = start + index - 1;
        if(left < start + length) {
            grailSwap(array, left - 2, left);
        }
    }

    private static <D> void grailMergeForwards(D[] array, int start, int leftLen, int rightLen,
                                               int bufferOffset, Comparator<D> cmp) {
        int buffer = start  - bufferOffset;
        int   left = start;
        int middle = start  +  leftLen;
        int  right = middle;
        int    end = middle + rightLen;

        while(right < end) {
            if(left == middle || cmp.compare(array[ left],
                    array[right]) > 0) {
                grailSwap(array, buffer, right);
                right++;
            }
            else {
                grailSwap(array, buffer,  left);
                left++;
            }
            buffer++;
        }

        if(buffer != left) {
            grailBlockSwap(array, buffer, left, middle - left);
        }
    }

    private static <D> void grailMergeBackwards(D[] array, int start, int leftLen, int rightLen, int bufferOffset, Comparator<D> cmp) {
        int end = start  -  1;
        int left = end    +  leftLen;
        int middle = left;
        int  right = middle + rightLen;
        int buffer = right  + bufferOffset;

        while(left > end) {
            if(right == middle || cmp.compare(array[ left],
                    array[right]) > 0) {
                grailSwap(array, buffer,  left);
                left--;
            }
            else {
                grailSwap(array, buffer, right);
                right--;
            }
            buffer--;
        }

        if(right != buffer) {
            while(right > middle) {
                grailSwap(array, buffer, right);
                buffer--;
                right--;
            }
        }
    }

    private static <D> void grailMergeOutOfPlace(D[] array, int start, int leftLen, int rightLen,
                                                 int bufferOffset, Comparator<D> cmp) {
        int buffer = start  - bufferOffset;
        int   left = start;
        int middle = start  +  leftLen;
        int  right = middle;
        int    end = middle + rightLen;

        while(right < end) {
            if(left == middle || cmp.compare(array[ left],
                    array[right]) > 0) {
                array[buffer] = array[right];
                right++;
            }
            else {
                array[buffer] = array[ left];
                left++;
            }
            buffer++;
        }

        if(buffer != left) {
            while(left < middle) {
                array[buffer] = array[left];
                buffer++;
                left++;
            }
        }
    }

    private static <D> void grailBuildInPlace(D[] array, int start, int length, int currentLen, int bufferLen, Comparator<D> cmp) {
        for(int mergeLen = currentLen; mergeLen < bufferLen; mergeLen *= 2) {
            int fullMerge = 2 * mergeLen;

            int mergeIndex;
            int mergeEnd = start + length - fullMerge;

            for(mergeIndex = start; mergeIndex <= mergeEnd; mergeIndex += fullMerge) {
                grailMergeForwards(array, mergeIndex, mergeLen, mergeLen, mergeLen, cmp);
            }

            int leftOver = length - (mergeIndex - start);

            if(leftOver > mergeLen) {
                grailMergeForwards(array, mergeIndex, mergeLen, leftOver - mergeLen, mergeLen, cmp);
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
            grailMergeBackwards(array, lastOffset, bufferLen, lastBlock - bufferLen, bufferLen, cmp);
        }

        for(int mergeIndex = lastOffset - fullMerge; mergeIndex >= start; mergeIndex -= fullMerge) {
            grailMergeBackwards(array, mergeIndex, bufferLen, bufferLen, bufferLen, cmp);
        }
    }

    private void grailBuildBlocks(D[] array, int start, int length, int bufferLen, Comparator<D> cmp) {
        grailPairwiseSwaps(array, start, length, cmp);
        grailBuildInPlace(array, start - 2, length, 2, bufferLen, cmp);
    }

    private static <D> int grailBlockSelectSort(D[] array, int start, int medianKey,
                                                int blockCount, int blockLen, Comparator<D> cmp) {
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
                grailSwap(array, firstBlock, selectBlock);

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
            grailSwap(array, index, buffer);
            buffer--;
            index--;
        }
    }

    private static <D> void grailInPlaceBufferRewind(D[] array, int start, int leftBlock, int buffer) {
        while(leftBlock >= start) {
            grailSwap(array, buffer, leftBlock);
            leftBlock--;
            buffer--;
        }
    }

    private static <D> void grailOutOfPlaceBufferRewind(D[] array, int start, int leftBlock, int buffer) {
        while(leftBlock >= start) {
            array[buffer] = array[leftBlock];
            leftBlock--;
            buffer--;
        }
    }


    private static <D> boolean grailGetSubarray(D[] array, int currentKey, int medianKey, Comparator<D> cmp) {
        return cmp.compare(array[currentKey], array[medianKey]) >= 0;
    }

    private static <D> int grailCountLastMergeBlocks(D[] array, int offset, int blockCount, int blockLen, Comparator<D> cmp) {
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

    private void grailSmartMerge(D[] array, int start, int leftLen, boolean leftOrigin,
                                 int rightLen, int bufferOffset,
                                 Comparator<D> cmp) {
        int buffer = start  - bufferOffset;
        int   left = start;
        int middle = start  +  leftLen;
        int  right = middle;
        int    end = middle + rightLen;

        if (leftOrigin) {
            while(left < middle && right < end) {
                if(cmp.compare(array[left], array[right]) <  0) {
                    grailSwap(array, buffer, left);
                    left++;
                }
                else {
                    grailSwap(array, buffer, right);
                    right++;
                }
                buffer++;
            }
        } else {
            while(left < middle && right < end) {
                if(cmp.compare(array[left], array[right]) <= 0) {
                    grailSwap(array, buffer, left);
                    left++;
                }
                else {
                    grailSwap(array, buffer, right);
                    right++;
                }
                buffer++;
            }
        }

        if(left < middle) {
            this.currBlockLen = middle - left;
            grailInPlaceBufferRewind(array, left, middle - 1, end - 1);
        }
        else {
            this.currBlockLen = end - right;
            this.currBlockOrigin = !leftOrigin;
        }
    }

    private void grailSmartLazyMerge(D[] array, int start, int leftLen, boolean leftOrigin, int rightLen, Comparator<D> cmp) {
        int middle = start + leftLen;

        if(!leftOrigin) {
            if(cmp.compare(array[middle - 1], array[middle]) >  0) {
                while(leftLen != 0) {
                    int mergeLen = grailBinarySearchLeft(array, middle, rightLen, array[start], cmp);

                    if(mergeLen != 0) {
                        grailRotate(array, start, leftLen, mergeLen);

                        start    += mergeLen;
                        middle   += mergeLen;
                        rightLen -= mergeLen;
                    }

                    if(rightLen == 0) {
                        this.currBlockLen = leftLen;
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
                    int mergeLen = grailBinarySearchRight(array, middle, rightLen, array[start], cmp);

                    if(mergeLen != 0) {
                        grailRotate(array, start, leftLen, mergeLen);

                        start    += mergeLen;
                        middle   += mergeLen;
                        rightLen -= mergeLen;
                    }

                    if(rightLen == 0) {
                        this.currBlockLen = leftLen;
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

        this.currBlockLen = rightLen;
        this.currBlockOrigin = !leftOrigin;
    }

    private void grailMergeBlocks(D[] array, int medianKey, int start,
                                  int blockCount, int blockLen, int lastMergeBlocks,
                                  int lastLen, Comparator<D> cmp) {
        int buffer;

        int currBlock;
        int nextBlock = start + blockLen;

        this.currBlockLen    = blockLen;
        this.currBlockOrigin = grailGetSubarray(array, 0, medianKey, cmp);

        for(int keyIndex = 1; keyIndex < blockCount; keyIndex++, nextBlock += blockLen) {
            boolean nextBlockOrigin;

            currBlock       = nextBlock - this.currBlockLen;
            nextBlockOrigin = grailGetSubarray(array, keyIndex, medianKey, cmp);

            if(nextBlockOrigin == this.currBlockOrigin) {
                buffer = currBlock - blockLen;

                grailBlockSwap(array, buffer, currBlock, this.currBlockLen);
                this.currBlockLen = blockLen;
            }
            else {
                this.grailSmartMerge(array, currBlock, this.currBlockLen, this.currBlockOrigin,
                        blockLen, blockLen, cmp);
            }
        }

        currBlock = nextBlock - this.currBlockLen;
        buffer    = currBlock - blockLen;

        if(lastLen != 0) {
            if(this.currBlockOrigin) {
                grailBlockSwap(array, buffer, currBlock, this.currBlockLen);

                currBlock            = nextBlock;
                this.currBlockLen    = blockLen * lastMergeBlocks;
                this.currBlockOrigin = false;
            } else this.currBlockLen += blockLen * lastMergeBlocks;

            grailMergeForwards(array, currBlock, this.currBlockLen, lastLen, blockLen, cmp);
        }
        else {
            grailBlockSwap(array, buffer, currBlock, this.currBlockLen);
        }
    }

    private void grailLazyMergeBlocks(D[] array, int medianKey, int start,
                                      int blockCount, int blockLen, int lastMergeBlocks,
                                      int lastLen, Comparator<D> cmp) {
        int currBlock;
        int nextBlock = start + blockLen;

        this.currBlockLen    = blockLen;
        this.currBlockOrigin = grailGetSubarray(array, 0, medianKey, cmp);

        for(int keyIndex = 1; keyIndex < blockCount; keyIndex++, nextBlock += blockLen) {
            boolean nextBlockOrigin;

            currBlock       = nextBlock - this.currBlockLen;
            nextBlockOrigin = grailGetSubarray(array, keyIndex, medianKey, cmp);

            if(nextBlockOrigin == this.currBlockOrigin) {
                this.currBlockLen = blockLen;
            }
            else {
                if(blockLen != 0 && this.currBlockLen != 0) {
                    this.grailSmartLazyMerge(array, currBlock, this.currBlockLen, this.currBlockOrigin,
                            blockLen, cmp);
                }
            }
        }

        currBlock = nextBlock - this.currBlockLen;

        if(lastLen != 0) {
            if(this.currBlockOrigin) {
                currBlock            = nextBlock;
                this.currBlockLen    = blockLen * lastMergeBlocks;
                this.currBlockOrigin = false;
            } else this.currBlockLen += blockLen * lastMergeBlocks;

            grailLazyMerge(array, currBlock, this.currBlockLen, lastLen, cmp);
        }
    }


    //TODO: Double-check "Merge Blocks" arguments
    private void grailCombineInPlace(D[] array, int start, int length,
                                     int subarrayLen, int blockLen,
                                     int mergeCount, int lastSubarrays,
                                     boolean buffer, Comparator<D> cmp) { //TODO: Do collisions with hanging indents like these affect readability?
        int fullMerge  = 2 * subarrayLen;
        int blockCount = fullMerge / blockLen;

        for(int mergeIndex = 0; mergeIndex < mergeCount; mergeIndex++) {
            int offset = start + (mergeIndex * fullMerge);

            grailInsertSort(array, blockCount, cmp);
            int medianKey = subarrayLen / blockLen;
            medianKey = grailBlockSelectSort(array, offset, medianKey, blockCount, blockLen, cmp);

            if(buffer) {
                this.grailMergeBlocks(array, medianKey, offset, blockCount, blockLen, 0, 0, cmp);
            }
            else {
                this.grailLazyMergeBlocks(array, medianKey, offset, blockCount, blockLen, 0, 0, cmp);
            }
        }

        if(lastSubarrays != 0) {
            int offset = start + (mergeCount * fullMerge);
            blockCount = lastSubarrays / blockLen;

            grailInsertSort(array, blockCount + 1, cmp);

            int medianKey = subarrayLen / blockLen;
            medianKey = grailBlockSelectSort(array, offset, medianKey, blockCount, blockLen, cmp);
            int lastFragment = lastSubarrays - (blockCount * blockLen);
            int lastMergeBlocks;
            if(lastFragment != 0) {
                lastMergeBlocks = grailCountLastMergeBlocks(array, offset, blockCount, blockLen, cmp);
            }
            else {
                lastMergeBlocks = 0;
            }

            int smartMerges = blockCount - lastMergeBlocks;

            //TODO: Double-check if this micro-optimization works correctly like the original
            if(smartMerges == 0) {
                int leftLen = lastMergeBlocks * blockLen;
                if(buffer) {
                    grailMergeForwards(array, offset, leftLen, lastFragment, blockLen, cmp);
                }
                else {
                    grailLazyMerge(array, offset, leftLen, lastFragment, cmp);
                }
            }
            else {
                if(buffer) {
                    this.grailMergeBlocks(array, medianKey, offset,
                            smartMerges, blockLen, lastMergeBlocks, lastFragment, cmp);
                }
                else {
                    this.grailLazyMergeBlocks(array, medianKey, offset,
                            smartMerges, blockLen, lastMergeBlocks, lastFragment, cmp);
                }
            }
        }

        if(buffer) {
            grailInPlaceBufferReset(array, start, length, blockLen);
        }
    }

    private void grailCombineBlocks(D[] array, int start, int length,
                                    int subarrayLen, int blockLen, boolean buffer, Comparator<D> cmp) {
        int     fullMerge = 2 * subarrayLen;
        int    mergeCount = length /  fullMerge;
        int lastSubarrays = length - (fullMerge * mergeCount);

        if(lastSubarrays <= subarrayLen) {
            length -= lastSubarrays;
            lastSubarrays = 0;
        }

        this.grailCombineInPlace(array, start, length, subarrayLen, blockLen,
                    mergeCount, lastSubarrays, buffer, cmp);
    }

    private static <D> void grailLazyMerge(D[] array, int start, int leftLen, int rightLen, Comparator<D> cmp) {
        if(leftLen < rightLen) {
            int middle = start + leftLen;

            while(leftLen != 0) {
                int mergeLen = grailBinarySearchLeft(array, middle, rightLen, array[start], cmp);

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
                int mergeLen = grailBinarySearchRight(array, start, leftLen, array[end], cmp);

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

    private static <D> void grailLazyStableSort(D[] array, int length, Comparator<D> cmp) {
        for(int index = 1; index < length; index += 2) {
            int  left = index - 1;

            if(cmp.compare(array[left], array[index]) > 0) {
                grailSwap(array, left, index);
            }
        }
        for(int mergeLen = 2; mergeLen < length; mergeLen *= 2) {
            int fullMerge = 2 * mergeLen;

            int mergeIndex;
            int mergeEnd = length - fullMerge;

            for(mergeIndex = 0; mergeIndex <= mergeEnd; mergeIndex += fullMerge) {
                grailLazyMerge(array, mergeIndex, mergeLen, mergeLen, cmp);
            }

            int leftOver = length - mergeIndex;
            if(leftOver > mergeLen) {
                grailLazyMerge(array, mergeIndex, mergeLen, leftOver - mergeLen, cmp);
            }
        }
    }

    void grailCommonSort(D[] array, int length, Comparator<D> cmp) {
        if(length < 16) {
            grailInsertSort(array, length, cmp);
            return;
        }

        int blockLen = 1;

        while((blockLen * blockLen) < length) {
            blockLen *= 2;
        }

        int keyLen = ((length - 1) / blockLen) + 1;

        int idealKeys = keyLen + blockLen;

        //TODO: Clean up `start +` offsets
        int keysFound = grailCollectKeys(array, length, idealKeys, cmp);

        boolean idealBuffer;
        if(keysFound < idealKeys) {
            if(keysFound < 4) {
                grailLazyStableSort(array, length, cmp);
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

        this.grailBuildBlocks(array, bufferEnd, length - bufferEnd, subarrayLen, cmp);

        while((length - bufferEnd) > (2 * subarrayLen)) {
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

            this.grailCombineBlocks(array, bufferEnd, length - bufferEnd,
                    subarrayLen, currentBlockLen, scrollingBuffer, cmp);
        }

        grailInsertSort(array, bufferEnd, cmp);
        grailLazyMerge(array, 0, bufferEnd, length - bufferEnd, cmp);
    }

    public void grailSortInPlace(D[] array, Comparator<D> cmp) {
        this.grailCommonSort(array, array.length, cmp);
    }
}