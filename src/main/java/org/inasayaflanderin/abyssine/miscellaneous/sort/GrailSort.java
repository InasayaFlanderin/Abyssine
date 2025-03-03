package org.inasayaflanderin.abyssine.miscellaneous.sort;

import java.util.Arrays;
import java.util.Comparator;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.copy;
import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.swap;

public class GrailSort {
    private static <D> void blockSwap(D[] data, int start, int end, int blockLen) {
        for(int i = 0; i < blockLen; i++) swap(data, start + i, end + i);
    }

    private static <D> void rotate(D[] data, int start, int leftLen, int rightLen) {
        while(leftLen > 0 && rightLen > 0) {
            if(leftLen <= rightLen) {
                blockSwap(data, start, start + leftLen, leftLen);
                start += leftLen;
                rightLen -= leftLen;
            } else {
                blockSwap(data, start + leftLen - rightLen, start + leftLen, rightLen);
                leftLen -= rightLen;
            }
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

        if(buffer != left) blockSwap(data, buffer, left, middle - left);
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

    private static <D> int grailBlockSelectSort(D[] data, Comparator<D> comparator, int start, int medianKey, int blockCount, int blockLen) {
        for(int firstBlock = 0; firstBlock < blockCount; firstBlock++) {
            var selectBlock = firstBlock;

            for(int currBlock = firstBlock + 1; currBlock < blockCount; currBlock++) {
                var compare = comparator.compare(data[start + (currBlock   * blockLen)], data[start + (selectBlock * blockLen)]);

                if(compare < 0 || (compare == 0 && comparator.compare(data[currBlock], data[selectBlock]) < 0)) selectBlock = currBlock;
            }

            if(selectBlock != firstBlock) {
                blockSwap(data, start + (firstBlock * blockLen), start + (selectBlock * blockLen), blockLen);
                swap(data, firstBlock, selectBlock);

                if(medianKey == firstBlock) medianKey = selectBlock;
                else if(medianKey == selectBlock) medianKey = firstBlock;
            }
        }

        return medianKey;
    }

    private static <D> void grailMergeBlocks(D[] data, Comparator<D> comparator, int medianKey, int start, int blockCount, int blockLen, int lastMergeBlocks, int lastLen) {
        int buffer, currBlock;
        var nextBlock = start + blockLen;
        int currBlockLen = blockLen;
        boolean currBlockOrigin = comparator.compare(data[0], data[medianKey]) >= 0;

        for(int keyIndex = 1; keyIndex < blockCount; keyIndex++, nextBlock += blockLen) {
            boolean nextBlockOrigin;
            currBlock = nextBlock - currBlockLen;
            nextBlockOrigin = comparator.compare(data[keyIndex], data[medianKey]) >= 0;

            if(nextBlockOrigin == currBlockOrigin) {
                buffer = currBlock - blockLen;
                blockSwap(data, buffer, currBlock, currBlockLen);
                currBlockLen = blockLen;
            } else {
                var bufferSM = currBlock - blockLen;
                var leftSM = currBlock;
                var middleSM = currBlock + currBlockLen;
                var rightSM = middleSM;
                var endSM = middleSM + blockLen;

                if (currBlockOrigin) {
                    while (leftSM < middleSM && rightSM < endSM) {
                        if (comparator.compare(data[leftSM], data[rightSM]) < 0) swap(data, bufferSM, leftSM++);
                        else swap(data, bufferSM, rightSM++);

                        bufferSM++;
                    }
                } else {
                    while (leftSM < middleSM && rightSM < endSM) {
                        if (comparator.compare(data[leftSM], data[rightSM]) <= 0) swap(data, bufferSM, leftSM++);
                        else swap(data, bufferSM, rightSM++);

                        bufferSM++;
                    }
                }

                if (leftSM < middleSM) {
                    currBlockLen = middleSM - leftSM;
                    var leftBlockSM = middleSM - 1;
                    var bufferRSM = endSM - 1;

                    while (leftBlockSM >= leftSM) swap(data, bufferRSM--, leftBlockSM--);
                } else {
                    currBlockLen = endSM - rightSM;
                    currBlockOrigin = !currBlockOrigin;
                }
            }
        }

        currBlock = nextBlock - currBlockLen;
        buffer = currBlock - blockLen;

        if(lastLen != 0) {
            if(currBlockOrigin) {
                blockSwap(data, buffer, currBlock, currBlockLen);
                currBlock = nextBlock;
                currBlockLen = blockLen * lastMergeBlocks;
            } else currBlockLen += blockLen * lastMergeBlocks;

            grailMergeForwards(data, comparator, currBlock, currBlockLen, lastLen, blockLen);
        } else blockSwap(data, buffer, currBlock, currBlockLen);
    }

    private static <D> void grailLazyMergeBlocks(D[] data, Comparator<D> comparator, int medianKey, int start, int blockCount, int blockLen, int lastMergeBlocks, int lastLen) {
        int currBlock;
        var nextBlock = start + blockLen;
        int currBlockLen = blockLen;
        boolean currBlockOrigin = comparator.compare(data[0], data[medianKey]) >= 0;

        for(int keyIndex = 1; keyIndex < blockCount; keyIndex++, nextBlock += blockLen) {
            boolean nextBlockOrigin;
            currBlock = nextBlock - currBlockLen;
            nextBlockOrigin = comparator.compare(data[keyIndex], data[medianKey]) >= 0;

            if(blockLen != 0) {
                var middle = currBlock + currBlockLen;

                if(currBlockOrigin) {
                    if(comparator.compare(data[middle - 1], data[middle]) > 0) {
                        while(true) {
                            var mergeLen = grailBinarySearchRight(data, comparator, middle, blockLen, data[currBlock]);

                            if(mergeLen != 0) {
                                rotate(data, currBlock, currBlockLen, mergeLen);
                                currBlock += mergeLen;
                                middle    += mergeLen;
                                blockLen  -= mergeLen;
                            }
                            if(blockLen == 0) {
                                return;
                            } else {
                                do {
                                    currBlock++;
                                    currBlockLen--;
                                } while(currBlockLen != 0 && comparator.compare(data[currBlock], data[middle]) <= 0);
                            }
                        }
                    }
                } else {
                    if(comparator.compare(data[middle - 1], data[middle]) > 0) {
                        while(currBlockLen != 0) {
                            var mergeLen = grailBinarySearchLeft(data, comparator, middle, blockLen, data[currBlock]);

                            if(mergeLen != 0) {
                                rotate(data, currBlock, currBlockLen, mergeLen);
                                currBlock += mergeLen;
                                middle    += mergeLen;
                                blockLen  -= mergeLen;
                            }
                            if(blockLen == 0) {
                                return;
                            } else {
                                do {
                                    currBlock++;
                                    currBlockLen--;
                                } while(currBlockLen != 0 && comparator.compare(data[currBlock], data[middle]) <= 0);
                            }
                        }
                    }
                }

                currBlockLen = blockLen;
                currBlockOrigin = nextBlockOrigin;
            }
        }

        currBlock = nextBlock - currBlockLen;

        if(lastLen != 0) {
            if(currBlockOrigin) {
                currBlock = nextBlock;
                currBlockLen = blockLen * lastMergeBlocks;
            } else currBlockLen += blockLen * lastMergeBlocks;

            grailLazyMerge(data, comparator, currBlock, currBlockLen, lastLen);
        }
    }

    private static <D> void grailLazyMerge(D[] data, Comparator<D> comparator, int start, int leftLen, int rightLen) {
        if(leftLen < rightLen) {
            var middle = start + leftLen;

            while(leftLen != 0) {
                var mergeLen = grailBinarySearchLeft(data, comparator, middle, rightLen, data[start]);

                if(mergeLen != 0) {
                    rotate(data, start, leftLen, mergeLen);
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
                    rotate(data, start + mergeLen, leftLen - mergeLen, rightLen);
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

    public static <D> void grailCommonSort(D[] data, Comparator<D> comparator) {
        if(data.length < 16) {
            Sort.binaryInsertion(data, comparator);

            return;
        }

        var blockLen = 4;

        while((blockLen * blockLen) < data.length) blockLen *= 2;

        var keyLen = (data.length - 1) / blockLen + 1;
        var idealKeys = keyLen + blockLen;
        var keysFound = 1;
        var firstKey = 0;
        var currKey = 1;

        while(currKey < data.length && keysFound < idealKeys) {
            var insertPos = 0;
            var right = keysFound;

            while(insertPos < right) {
                var mid = (insertPos + right) >>> 1;
                var comp = comparator.compare(data[firstKey + mid], data[currKey]);

                if(comp == 0) {
                    insertPos = -1;
                    break;
                } else if(comp < 0) insertPos = mid + 1;
                else right = mid;
            }

            if(insertPos != -1) {
                rotate(data, firstKey, keysFound, currKey - (firstKey + keysFound));
                firstKey = currKey - keysFound;

                if(keysFound != insertPos) {
                    var item = data[firstKey + keysFound];
                    copy(data, firstKey + insertPos, data, firstKey + insertPos + 1, keysFound - insertPos);
                    data[firstKey + insertPos] = item;
                }

                keysFound++;
            }

            currKey++;
        }

        rotate(data, 0, firstKey, keysFound);

        boolean idealBuffer;
        if(keysFound < idealKeys) {
            /*if(keysFound < 4) {
                if(keysFound == 1) return;

                Sort.tim(data, comparator);

                return;
            } else {
                keyLen = blockLen;
                blockLen = 0;
                idealBuffer = false;

                while(keyLen > keysFound) keyLen /= 2;
            }*/

            Sort.tim(data, comparator);

            return;
        } else idealBuffer = true;

        var bufferEnd = blockLen + keyLen;
        var subarrayLen = idealBuffer ? blockLen : keyLen;
        var firstSwapKey = data[bufferEnd - 1];
        var secondSwapKey = data[bufferEnd - 2];
        int i;

        for(i = 1; i < data.length - bufferEnd; i += 2) {
            var left = bufferEnd + i - 1;
            var right = bufferEnd + i;

            if(comparator.compare(data[left], data[right]) > 0) {
                data[left - 2] = data[right];
                data[right - 2] = data[left];
            } else {
                data[left - 2] = data[left];
                data[right - 2] = data[right];
            }
        }

        var left = bufferEnd + i - 1;

        if(left < data.length - bufferEnd) data[left - 2] = data[left];

        data[data.length - 2] = firstSwapKey;
        data[data.length - 1] = secondSwapKey;
        var start = bufferEnd - 2;

        for(int mergeLen = 2; mergeLen < subarrayLen; mergeLen *= 2) {
            var fullMerge = 2 * mergeLen;
            int mergeIndex;
            var mergeEnd = start + data.length - bufferEnd - fullMerge;

            for(mergeIndex = start; mergeIndex <= mergeEnd; mergeIndex += fullMerge) grailMergeForwards(data, comparator, mergeIndex, mergeLen, mergeLen, mergeLen);

            var leftOver = data.length - bufferEnd - (mergeIndex - start);

            if(leftOver > mergeLen) grailMergeForwards(data, comparator, mergeIndex, mergeLen, leftOver - mergeLen, mergeLen);
            else rotate(data, mergeIndex - mergeLen, mergeLen, leftOver);

            start -= mergeLen;
        }

        var fullMerge = 2 * subarrayLen;
        var lastBlock = (data.length - bufferEnd) % fullMerge;
        var lastOffset = start + data.length - bufferEnd - lastBlock;

        if(lastBlock <= subarrayLen) rotate(data, lastOffset, lastBlock, subarrayLen);
        else grailMergeBackwards(data, comparator, lastOffset, subarrayLen, lastBlock - subarrayLen, subarrayLen);

        for(int mergeIndex = lastOffset - fullMerge; mergeIndex >= start; mergeIndex -= fullMerge) grailMergeBackwards(data, comparator, mergeIndex, subarrayLen, subarrayLen, subarrayLen);

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

            var length = data.length - bufferEnd;
            var fullMergeCalc = 2 * subarrayLen;
            var mergeCount = length / fullMergeCalc;
            var lastSubData = length - (fullMergeCalc * mergeCount);

            if(lastSubData <= subarrayLen) {
                length -= lastSubData;
                lastSubData = 0;
            }

            var fullMergeInPlace = 2 * subarrayLen;
            var blockCount = fullMergeInPlace / currentBlockLen;

            for(int mergeIndex = 0; mergeIndex < mergeCount; mergeIndex++) {
                var offset = bufferEnd + (mergeIndex * fullMergeInPlace);
                Sort.insertion(Arrays.asList(data), comparator, 0, blockCount);
                var medianKey = subarrayLen / currentBlockLen;
                medianKey = grailBlockSelectSort(data, comparator, offset, medianKey, blockCount, currentBlockLen);

                if(scrollingBuffer) grailMergeBlocks(data, comparator, medianKey, offset, blockCount, currentBlockLen, 0, 0);
                else grailLazyMergeBlocks(data, comparator, medianKey, offset, blockCount, currentBlockLen, 0, 0);
            }

            if(lastSubData != 0) {
                var offsetFull = bufferEnd + (mergeCount * fullMergeInPlace);
                var blockCountFull = lastSubData / currentBlockLen;
                Sort.insertion(Arrays.asList(data), comparator, 0, blockCountFull + 1);
                var medianKey = subarrayLen / currentBlockLen;
                medianKey = grailBlockSelectSort(data, comparator, offsetFull, medianKey, blockCountFull, currentBlockLen);
                var lastFragment = lastSubData - (blockCountFull * currentBlockLen);
                var lastMergeBlocks = 0;

                if(lastFragment != 0) {
                    var lastRightFrag = offsetFull + (blockCountFull * currentBlockLen);
                    var prevLeftBlock = lastRightFrag - currentBlockLen;

                    while(lastMergeBlocks < blockCountFull && comparator.compare(data[lastRightFrag], data[prevLeftBlock]) < 0) {
                        lastMergeBlocks++;
                        prevLeftBlock -= currentBlockLen;
                    }
                }

                var smartMerges = blockCountFull - lastMergeBlocks;

                if(smartMerges == 0) {
                    var leftLen = lastMergeBlocks * currentBlockLen;

                    if(scrollingBuffer) grailMergeForwards(data, comparator, offsetFull, leftLen, lastFragment, currentBlockLen);
                    else grailLazyMerge(data, comparator, offsetFull, leftLen, lastFragment);
                } else {
                    if(scrollingBuffer) grailMergeBlocks(data, comparator, medianKey, offsetFull, smartMerges, currentBlockLen, lastMergeBlocks, lastFragment);
                    else grailLazyMergeBlocks(data, comparator, medianKey, offsetFull, smartMerges, currentBlockLen, lastMergeBlocks, lastFragment);
                }
            }

            if(scrollingBuffer) {
                var bufferR = bufferEnd + length - 1;
                var index = bufferR - currentBlockLen;

                while(bufferR >= bufferEnd) swap(data, index--, bufferR--);
            }
        }

        Sort.insertion(Arrays.asList(data), comparator, 0, bufferEnd);
        grailLazyMerge(data, comparator, 0, bufferEnd, data.length - bufferEnd);
    }
}