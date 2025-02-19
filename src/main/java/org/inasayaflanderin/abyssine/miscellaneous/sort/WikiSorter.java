package org.inasayaflanderin.abyssine.miscellaneous.sort;

import lombok.extern.slf4j.Slf4j;
import org.inasayaflanderin.abyssine.primitives.Quin;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.*;

@Slf4j
class WikiSorter<D> {
    public static <D> void sort(D[] data, Comparator<D> comparator) {
        new WikiSorter<D>().Sort(Arrays.asList(data), comparator);
    }

    int binaryFirst(List<D> data, Comparator<D> comparator, D value, int rangeStart, int rangeEnd) {
        var start = rangeStart;
        var end = rangeEnd - 1;

        while (start < end) {
            var mid = start + (end - start)/2;
            if (comparator.compare(data.get(mid), value) < 0) start = mid + 1;
            else end = mid;
        }

        if (start == rangeEnd - 1 && comparator.compare(data.get(start), value) < 0) start++;

        return start;
    }

    int binaryLast(List<D> data,  Comparator<D> comparator, D value, int rangeStart, int rangeEnd) {
        var start = rangeStart;
        var end = rangeEnd - 1;

        while (start < end) {
            int mid = start + (end - start)/2;
            if (comparator.compare(value, data.get(mid)) >= 0) start = mid + 1;
            else end = mid;
        }

        if (start == rangeEnd - 1 && comparator.compare(value, data.get(start)) >= 0) start++;

        return start;
    }

    int findFirstForward(List<D> data, Comparator<D> comparator, D value, int rangeStart, int rangeEnd, int unique) {
        if (rangeEnd - rangeStart == 0) return rangeStart;

        int index;
        var skip = Math.max((rangeEnd - rangeStart)/unique, 1);

        for (index = rangeStart + skip; comparator.compare(data.get(index - 1), value) < 0; index += skip) if (index >= rangeEnd - skip) return binaryFirst(data, comparator, value, index, rangeEnd);

        return binaryFirst(data, comparator, value, index - skip, index);
    }

    int findLastForward(List<D> data, Comparator<D> comparator, D value, int rangeStart, int rangeEnd, int unique) {
        if (rangeEnd - rangeStart== 0) return rangeStart;
        int index;
        var skip = Math.max((rangeEnd - rangeStart) / unique, 1);

        for (index = rangeStart + skip; comparator.compare(value, data.get(index - 1)) >= 0; index += skip) if (index >= rangeEnd - skip) return binaryLast(data, comparator, value, index, rangeEnd);

        return binaryLast(data, comparator, value, index - skip, index);
    }

    int findFirstBackward(List<D> data, Comparator<D> comparator, D value, int rangeStart, int rangeEnd, int unique) {
        if (rangeEnd - rangeStart == 0) return rangeStart;

        int index;
        var skip = Math.max((rangeEnd - rangeStart) / unique, 1);

        for (index = rangeEnd - skip; index > rangeStart && comparator.compare(data.get(index - 1), value) >= 0; index -= skip) if (index < rangeStart + skip) return binaryFirst(data, comparator, value, rangeStart, index);

        return binaryFirst(data, comparator, value, index, index + skip);
    }

    int findLastBackward(List<D> data, Comparator<D> comparator, D value, int rangeStart, int rangeEnd, int unique) {
        if (rangeEnd - rangeStart == 0) return rangeStart;

        int index;
        var skip = Math.max((rangeEnd - rangeStart) / unique, 1);

        for (index = rangeEnd - skip; index > rangeStart && comparator.compare(value, data.get(index - 1)) < 0; index -= skip) if (index < rangeStart + skip) return binaryLast(data, comparator, value, rangeStart, index);

        return binaryLast(data, comparator, value, index, index + skip);
    }

    void blockSwap(List<D> data, int start1, int start2, int blockSize) {
        for (int index = 0; index < blockSize; index++) swap(data, start1 + index, start2 + index);
    }

    void rotate(List<D> data, int amount, int rangeStart, int rangeEnd, D[] cache) {
        if (rangeEnd - rangeStart == 0) return;

        int split;

        if (amount >= 0) split = rangeStart + amount;
        else split = rangeEnd + amount;

        var range1End = split;
        var range2Start = split;

        if (cache != null) {
            if (range1End - rangeStart <= rangeEnd - range2Start) {
                if (range1End - rangeStart <= 512) {
                    copy(data, rangeStart, cache, 0, range1End - rangeStart);
                    copy(data, range2Start, data, rangeStart, rangeEnd - range2Start);
                    copy(cache, 0, data, rangeStart + rangeEnd - range2Start, range1End - rangeStart);

                    return;
                }
            } else {
                if (rangeEnd - range2Start <= 512) {
                    copy(data, range2Start, cache, 0, rangeEnd - range2Start);
                    copy(data, rangeStart, data, rangeEnd - range1End + rangeStart, range1End - rangeStart);
                    copy(cache, 0, data, rangeStart, rangeEnd - range2Start);

                    return;
                }
            }
        }

        flip(data, rangeStart, range1End);
        flip(data, range2Start, rangeEnd);
        flip(data, rangeStart, rangeEnd);
    }

    void mergeInto(List<D> from, Comparator<D> comparator, int AStart, int AEnd, int BStart, int BEnd, D[] into, int atIndex) {
        var AIndex = AStart;
        var BIndex = BStart;
        var insertIndex = atIndex;

        while (true) {
            if (comparator.compare(from.get(BIndex), from.get(AIndex)) >= 0) {
                into[insertIndex++] = from.get(AIndex++);

                if (AIndex == AEnd) {
                    copy(from, BIndex, into, insertIndex, BEnd - BIndex);

                    break;
                }
            } else {
                into[insertIndex++] = from.get(BIndex++);

                if (BIndex == BEnd) {
                    copy(from, AIndex, into, insertIndex, AEnd - AIndex);

                    break;
                }
            }
        }
    }

    void mergeExternal(List<D> data, Comparator<D> comparator, int AStart, int AEnd, int BStart, int BEnd, D[] cache) {
        var AIndex = 0;
        var BIndex = BStart;
        var insertIndex = AStart;
        var ALast = AEnd - AStart;

        if (BEnd - BStart > 0 && AEnd - AStart > 0) {
            while (true) {
                if (comparator.compare(data.get(BIndex), cache[AIndex]) >= 0) {
                    data.set(insertIndex++, cache[AIndex++]);

                    if (AIndex == ALast) break;
                } else {
                    data.set(insertIndex++, data.get(BIndex++));

                    if (BIndex == BEnd) break;
                }
            }
        }

        copy(cache, AIndex, data, insertIndex, ALast - AIndex);
    }

    void mergeInternal(List<D> data, Comparator<D> comparator, int AStart, int AEnd, int BStart, int BEnd, int bufferStart) {
        var ACount = 0;
        var BCount = 0;
        var insert = 0;

        if (BEnd - BStart > 0 && AEnd - AStart > 0) {
            while (true) {
                if (comparator.compare(data.get(BStart + BCount), data.get(bufferStart + ACount)) >= 0) {
                    swap(data, AStart + insert++, bufferStart + ACount++);

                    if (ACount >= AEnd - AStart) break;
                } else {
                    swap(data, AStart + insert++, BStart + BCount++);

                    if (BCount >= BEnd - BStart) break;
                }
            }
        }

        blockSwap(data, bufferStart + ACount, AStart + insert, AEnd - AStart - ACount);
    }

    void mergeInPlace(List<D> data, Comparator<D> comparator, int AStart, int AEnd, int BStart, int BEnd, D[] cache) {
        if (AEnd - AStart == 0 || BEnd - BStart == 0) return;

        while (true) {
            var mid = binaryFirst(data, comparator, data.get(AStart), BStart, BEnd);
            var amount = mid - AEnd;
            rotate(data, -amount, AStart, mid, cache);

            if (BEnd == mid) break;

            BStart = mid;
            AStart = AStart + amount;
            AEnd = BStart;
            AStart = binaryLast(data, comparator, data.get(AStart), AStart, AEnd);

            if (AEnd - AStart == 0) break;
        }
    }

    void netSwap(List<D> data, Comparator<D> comparator, int[] order, int rangeStart, int x, int y) {
        var compare = comparator.compare(data.get(rangeStart + x), data.get(rangeStart + y));
        if (compare > 0 || (order[x] > order[y] && compare == 0)) {
            swap(data, rangeStart + x, rangeStart + y);
            var swap2 = order[x];
            order[x] = order[y];
            order[y] = swap2;
        }
    }

    @SuppressWarnings("unchecked")
    void Sort(List<D> data, Comparator<D> comparator) {
        D[] cache = (D[]) new Object[512];

        if (data.size() < 4) {
            Sort.binaryInsertion(data, comparator);

            return;
        }

        var powerOfTwo = data.size();
        powerOfTwo |= powerOfTwo >> 1;
        powerOfTwo |= powerOfTwo >> 2;
        powerOfTwo |= powerOfTwo >> 4;
        powerOfTwo |= powerOfTwo >> 8;
        powerOfTwo |= powerOfTwo >> 16;
        powerOfTwo -= powerOfTwo >> 1;
        var denominator = powerOfTwo / 4;
        var numeratorStep = data.size() % denominator;
        var decimalStep = data.size() / denominator;
        var numerator = 0;
        var decimal = 0;

        while (decimal < data.size()) {
            int[] order = { 0, 1, 2, 3, 4, 5, 6, 7 };
            var rangeStart = decimal;
            decimal += decimalStep;
            numerator += numeratorStep;

            if (numerator >= denominator) {
                numerator -= denominator;
                decimal++;
            }

            var rangeEnd = decimal;

            switch(rangeEnd - rangeStart) {
                case 8 -> {
                    netSwap(data, comparator, order, rangeStart, 0, 1);
                    netSwap(data, comparator, order, rangeStart, 2, 3);
                    netSwap(data, comparator, order, rangeStart, 4, 5);
                    netSwap(data, comparator, order, rangeStart, 6, 7);
                    netSwap(data, comparator, order, rangeStart, 0, 2);
                    netSwap(data, comparator, order, rangeStart, 1, 3);
                    netSwap(data, comparator, order, rangeStart, 4, 6);
                    netSwap(data, comparator, order, rangeStart, 5, 7);
                    netSwap(data, comparator, order, rangeStart, 1, 2);
                    netSwap(data, comparator, order, rangeStart, 5, 6);
                    netSwap(data, comparator, order, rangeStart, 0, 4);
                    netSwap(data, comparator, order, rangeStart, 3, 7);
                    netSwap(data, comparator, order, rangeStart, 1, 5);
                    netSwap(data, comparator, order, rangeStart, 2, 6);
                    netSwap(data, comparator, order, rangeStart, 1, 4);
                    netSwap(data, comparator, order, rangeStart, 3, 6);
                    netSwap(data, comparator, order, rangeStart, 2, 4);
                    netSwap(data, comparator, order, rangeStart, 3, 5);
                    netSwap(data, comparator, order, rangeStart, 3, 4);
                }

                case 7 -> {
                    netSwap(data, comparator, order, rangeStart, 1, 2);
                    netSwap(data, comparator, order, rangeStart, 3, 4);
                    netSwap(data, comparator, order, rangeStart, 5, 6);
                    netSwap(data, comparator, order, rangeStart, 0, 2);
                    netSwap(data, comparator, order, rangeStart, 3, 5);
                    netSwap(data, comparator, order, rangeStart, 4, 6);
                    netSwap(data, comparator, order, rangeStart, 0, 1);
                    netSwap(data, comparator, order, rangeStart, 4, 5);
                    netSwap(data, comparator, order, rangeStart, 2, 6);
                    netSwap(data, comparator, order, rangeStart, 0, 4);
                    netSwap(data, comparator, order, rangeStart, 1, 5);
                    netSwap(data, comparator, order, rangeStart, 0, 3);
                    netSwap(data, comparator, order, rangeStart, 2, 5);
                    netSwap(data, comparator, order, rangeStart, 1, 3);
                    netSwap(data, comparator, order, rangeStart, 2, 4);
                    netSwap(data, comparator, order, rangeStart, 2, 3);
                }

                case 6 -> {
                    netSwap(data, comparator, order, rangeStart, 1, 2);
                    netSwap(data, comparator, order, rangeStart, 4, 5);
                    netSwap(data, comparator, order, rangeStart, 0, 2);
                    netSwap(data, comparator, order, rangeStart, 3, 5);
                    netSwap(data, comparator, order, rangeStart, 0, 1);
                    netSwap(data, comparator, order, rangeStart, 3, 4);
                    netSwap(data, comparator, order, rangeStart, 2, 5);
                    netSwap(data, comparator, order, rangeStart, 0, 3);
                    netSwap(data, comparator, order, rangeStart, 1, 4);
                    netSwap(data, comparator, order, rangeStart, 2, 4);
                    netSwap(data, comparator, order, rangeStart, 1, 3);
                    netSwap(data, comparator, order, rangeStart, 2, 3);
                }

                case 5 -> {
                    netSwap(data, comparator, order, rangeStart, 0, 1);
                    netSwap(data, comparator, order, rangeStart, 3, 4);
                    netSwap(data, comparator, order, rangeStart, 2, 4);
                    netSwap(data, comparator, order, rangeStart, 2, 3);
                    netSwap(data, comparator, order, rangeStart, 1, 4);
                    netSwap(data, comparator, order, rangeStart, 0, 3);
                    netSwap(data, comparator, order, rangeStart, 0, 2);
                    netSwap(data, comparator, order, rangeStart, 1, 3);
                    netSwap(data, comparator, order, rangeStart, 1, 2);
                }

                case 4 -> {
                    netSwap(data, comparator, order, rangeStart, 0, 1);
                    netSwap(data, comparator, order, rangeStart, 2, 3);
                    netSwap(data, comparator, order, rangeStart, 0, 2);
                    netSwap(data, comparator, order, rangeStart, 1, 3);
                    netSwap(data, comparator, order, rangeStart, 1, 2);
                }
            }
        }

        if (data.size() < 8) return;

        var buffer1Start = 0;
        var buffer1End = 0;
        var buffer2Start = 0;
        var buffer2End = 0;
        var blockAStart = 0;
        var blockAEnd = 0;
        var blockBStart = 0;
        var blockBEnd = 0;
        var lastAStart = 0;
        var lastAEnd = 0;
        var lastBStart = 0;
        var lastBEnd = 0;
        var firstAStart = 0;
        var firstAEnd = 0;
        int AStart, AEnd, BStart, BEnd;
        Quin<Integer, Integer, Integer, Integer, Integer>[] pull = (Quin<Integer, Integer, Integer, Integer, Integer>[]) new Quin[2];
        pull[0] = new Quin<>(0, 0, 0, 0, 0);
        pull[1] = new Quin<>(0, 0, 0, 0, 0);

        do {
            if (decimalStep < 512) {
                numerator = decimal = 0;

                if ((decimalStep + 1) * 4 <= 512 && decimalStep * 4 <= data.size()) {
                    while (decimal < data.size()) {
                        var A1Start = decimal;
                        decimal += decimalStep;
                        numerator += numeratorStep;

                        if (numerator >= denominator) {
                            numerator -= denominator;
                            decimal++;
                        }

                        var A1End = decimal;
                        var B1Start = decimal;
                        decimal += decimalStep;
                        numerator += numeratorStep;

                        if (numerator >= denominator) {
                            numerator -= denominator;
                            decimal++;
                        }

                        var B1End = decimal;
                        var A2Start = decimal;
                        decimal += decimalStep;
                        numerator += numeratorStep;

                        if (numerator >= denominator) {
                            numerator -= denominator;
                            decimal++;
                        }

                        var A2End = decimal;
                        var B2Start = decimal;
                        decimal += decimalStep;
                        numerator += numeratorStep;

                        if (numerator >= denominator) {
                            numerator -= denominator;
                            decimal++;
                        }

                        var B2End = decimal;

                        if (comparator.compare(data.get(B1End - 1), data.get(A1Start)) < 0) {
                            copy(data, A1Start, cache, B1End - B1Start, A1End - A1Start);
                            copy(data, B1Start, cache, 0, B1End - B1Start);
                        } else if (comparator.compare(data.get(B1Start), data.get(A1End - 1)) < 0) mergeInto(data, comparator, A1Start, A1End, B1Start, B1End, cache, 0);
                        else {
                            if (comparator.compare(data.get(B2Start), data.get(A2End - 1)) >= 0 && comparator.compare(data.get(A2Start), data.get(B1End - 1)) >= 0) continue;

                            copy(data, A1Start, cache, 0, A1End - A1Start);
                            copy(data, B1Start, cache, A1End - A1Start, B1End - B1Start);
                        }

                        A1End = B1End;

                        if (comparator.compare(data.get(B2End - 1), data.get(A2Start)) < 0) {
                            copy(data, A2Start, cache, A1End - A1Start + B2End - B2Start, A2End - A2Start);
                            copy(data, B2Start, cache, A1End - A1Start, B2End - B2Start);
                        } else if (comparator.compare(data.get(B2Start), data.get(A2End - 1)) < 0) mergeInto(data, comparator, A2Start, A2End, B2Start, B2End, cache, A1End - A1Start);
                        else {
                            copy(data, A2Start, cache, A1End - A1Start, A2End - A2Start);
                            copy(data, B2Start, cache, A1End - A1Start + A2End - A2Start, B2End - B2Start);
                        }

                        A2End = B2End;
                        var A3Start = 0;
                        var A3End = A1End - A1Start;
                        var B3Start = A1End - A1Start;
                        var B3End = A1End - A1Start + A2End - A2Start;

                        if (comparator.compare(cache[B3End - 1], cache[A3Start]) < 0) {
                            copy(cache, A3Start, data, A1Start + A2End - A2Start, A3End - A3Start);
                            copy(cache, B3Start, data, A1Start, B3End - B3Start);
                        } else if (comparator.compare(cache[B3Start], cache[A3End - 1]) < 0) {
                            var AIndex = A3Start;
                            var BIndex = B3Start;
                            var insertIndex = A1Start;

                            while(true) {
                                if(comparator.compare(cache[BIndex], cache[AIndex]) >= 0) {
                                    data.set(insertIndex, cache[AIndex]);
                                    AIndex++;
                                    insertIndex++;

                                    if(AIndex == A3End) {
                                        copy(cache, BIndex, data, insertIndex, B3End - BIndex);
                                        break;
                                    }
                                } else {
                                    data.set(insertIndex, cache[BIndex]);
                                    BIndex++;
                                    insertIndex++;

                                    if(BIndex == B3End) {
                                        copy(cache, AIndex, data, insertIndex, A3End - AIndex);
                                        break;
                                    }
                                }
                            }
                        } else {
                            copy(cache, A3Start, data, A1Start, A3End - A3Start);
                            copy(cache, B3Start, data, A1End, B3End - B3Start);
                        }
                    }

                    decimalStep += decimalStep;
                    numeratorStep += numeratorStep;

                    if (numeratorStep >= denominator) {
                        numeratorStep -= denominator;
                        decimalStep++;
                    }
                } else {
                    while (decimal < data.size()) {
                        AStart = decimal;
                        decimal += decimalStep;
                        numerator += numeratorStep;

                        if (numerator >= denominator) {
                            numerator -= denominator;
                            decimal++;
                        }

                        AEnd = decimal;
                        BStart = decimal;
                        decimal += decimalStep;
                        numerator += numeratorStep;

                        if (numerator >= denominator) {
                            numerator -= denominator;
                            decimal++;
                        }

                        BEnd = decimal;

                        if (comparator.compare(data.get(BEnd - 1), data.get(AStart)) < 0) rotate(data, AEnd - AStart, AStart, BEnd, cache);
                        else if (comparator.compare(data.get(BStart), data.get(AEnd - 1)) < 0) {
                            copy(data, AStart, cache, 0, AEnd - AStart);
                            mergeExternal(data, comparator, AStart, AEnd, BStart, BEnd, cache);
                        }
                    }
                }
            } else {

                var blockSize = (int) Math.sqrt(decimalStep);
                var bufferSize = decimalStep / blockSize + 1;

                int index, last, count;
                var pullIndex = 0;
                buffer1Start = 0;
                buffer1End = 0;
                buffer2Start = 0;
                buffer2End = 0;
                pull[0] = pull[0].withFirst(0).withSecond(0).withThird(0).withFourth(0).withFifth(0);
                pull[1] = pull[1].withFirst(0).withSecond(0).withThird(0).withFourth(0).withFifth(0);
                var find = bufferSize + bufferSize;
                var findSeparate = false;

                if (blockSize <= 512) find = bufferSize;
                else if (find > decimalStep) {
                    find = bufferSize;
                    findSeparate = true;
                }

                numerator = decimal = 0;
                while (decimal < data.size()) {
                    AStart = decimal;
                    decimal += decimalStep;
                    numerator += numeratorStep;

                    if (numerator >= denominator) {
                        numerator -= denominator;
                        decimal++;
                    }

                    AEnd = decimal;
                    BStart = decimal;
                    decimal += decimalStep;
                    numerator += numeratorStep;

                    if (numerator >= denominator) {
                        numerator -= denominator;
                        decimal++;
                    }

                    BEnd = decimal;

                    for (last = AStart, count = 1; count < find; last = index, count++) {
                        index = findLastForward(data, comparator, data.get(last), last + 1, AEnd, find - count);

                        if (index == AEnd) break;
                    }

                    index = last;

                    if (count >= bufferSize) {
                        pull[pullIndex] = pull[pullIndex].withFirst(AStart).withSecond(BEnd).withThird(count).withFourth(index).withFifth(AStart);
                        pullIndex = 1;

                        if (count == bufferSize + bufferSize) {
                            buffer1Start = AStart;
                            buffer1End = AStart + bufferSize;
                            buffer2Start = AStart + bufferSize;
                            buffer2End = AStart + count;

                            break;
                        } else if (find == bufferSize + bufferSize) {
                            buffer1Start = AStart;
                            buffer1End = AStart + count;
                            find = bufferSize;
                        } else if (blockSize <= 512) {
                            buffer1Start = AStart;
                            buffer1End = AStart + count;

                            break;
                        } else if (findSeparate) {
                            buffer1Start = AStart;
                            buffer1End = AStart + count;
                            findSeparate = false;
                        } else {
                            buffer2Start = AStart;
                            buffer2End = AStart + count;

                            break;
                        }
                    } else if (pullIndex == 0 && count > buffer1End - buffer1Start) {
                        buffer1Start = AStart;
                        buffer1End = AStart + count;
                        pull[pullIndex] = pull[pullIndex].withFirst(AStart).withSecond(BEnd).withThird(count).withFourth(index).withFifth(AStart);
                    }

                    for (last = BEnd - 1, count = 1; count < find; last = index - 1, count++) {
                        index = findFirstBackward(data, comparator, data.get(last), BStart, last, find - count);

                        if (index == BStart) break;
                    }

                    index = last;

                    if (count >= bufferSize) {
                        pull[pullIndex] = pull[pullIndex].withFirst(AStart).withSecond(BEnd).withThird(count).withFourth(index).withFifth(BEnd);
                        pullIndex = 1;

                        if (count == bufferSize + bufferSize) {
                            buffer1Start = BEnd - count;
                            buffer1End = BEnd - bufferSize;
                            buffer2Start = BEnd - bufferSize;
                            buffer2End = BEnd;

                            break;
                        } else if (find == bufferSize + bufferSize) {
                            buffer1Start = BEnd - count;
                            buffer1End = BEnd;
                            find = bufferSize;
                        } else if (blockSize <= 512) {
                            buffer1Start = BEnd - count;
                            buffer1End = BEnd;

                            break;
                        } else if (findSeparate) {
                            buffer1Start = BEnd - count;
                            buffer1End = BEnd;
                            findSeparate = false;
                        } else {
                            if (pull[0].first() == AStart) pull[0] = pull[0].withSecond((pull[0].second() - pull[1].third()));

                            buffer2Start = BEnd - count;
                            buffer2End = BEnd;

                            break;
                        }
                    } else if (pullIndex == 0 && count > buffer1End - buffer1Start) {
                        buffer1Start = BEnd - count;
                        buffer1End = BEnd;
                        pull[pullIndex] = pull[pullIndex].withFirst(AStart).withSecond(BEnd).withThird(count).withFourth(index).withFifth(BEnd);
                    }
                }

                for (pullIndex = 0; pullIndex < 2; pullIndex++) {
                    var length = pull[pullIndex].third();

                    if (pull[pullIndex].fifth() < pull[pullIndex].fourth()) {
                        index = pull[pullIndex].fourth();

                        for (count = 1; count < length; count++) {
                            index = findFirstBackward(data, comparator, data.get(index - 1), pull[pullIndex].fifth(), pull[pullIndex].fourth() - (count - 1), length - count);
                            var rangeStart = index + 1;
                            var rangeEnd = pull[pullIndex].fourth() + 1;
                            rotate(data, rangeEnd - rangeStart - count, rangeStart, rangeEnd, cache);
                            pull[pullIndex] = pull[pullIndex].withFourth(index + count);
                        }
                    } else if (pull[pullIndex].fifth() > pull[pullIndex].fourth()) {
                        index = pull[pullIndex].fourth() + 1;

                        for (count = 1; count < length; count++) {
                            index = findLastForward(data, comparator, data.get(index), index, pull[pullIndex].fifth(), length - count);
                            var rangeStart = pull[pullIndex].fourth();
                            var rangeEnd = index - 1;
                            rotate(data, count, rangeStart, rangeEnd, cache);
                            pull[pullIndex] = pull[pullIndex].withFourth(index - 1 - count);
                        }
                    }
                }

                bufferSize = buffer1End - buffer1Start;
                blockSize = decimalStep / bufferSize + 1;
                numerator = decimal = 0;
                while (decimal < data.size()) {

                    AStart = decimal;
                    decimal += decimalStep;
                    numerator += numeratorStep;

                    if (numerator >= denominator) {
                        numerator -= denominator;
                        decimal++;
                    }

                    AEnd = decimal;
                    BStart = decimal;
                    decimal += decimalStep;
                    numerator += numeratorStep;

                    if (numerator >= denominator) {
                        numerator -= denominator;
                        decimal++;
                    }

                    BEnd = decimal;
                    var start = AStart;

                    if (start == pull[0].first()) {
                        if (pull[0].fourth() > pull[0].fifth()) {
                            AStart += pull[0].third();

                            if (AEnd - AStart == 0) continue;
                        } else if (pull[0].fourth() < pull[0].fifth()) {
                            BEnd -= pull[0].third();

                            if (BEnd - BStart == 0) continue;
                        }
                    }
                    if (start == pull[1].first()) {
                        if (pull[1].fourth() > pull[1].fifth()) {
                            AStart += pull[1].third();

                            if (AEnd - AStart == 0) continue;
                        } else if (pull[1].fourth() < pull[1].fifth()) {
                            BEnd -= pull[1].third();

                            if (BEnd - BStart == 0) continue;
                        }
                    }

                    if (comparator.compare(data.get(BEnd - 1), data.get(AStart)) < 0) rotate(data, AEnd - AStart, AStart, BEnd, cache);
                    else if (comparator.compare(data.get(AEnd), data.get(AEnd - 1)) < 0) {
                        blockAStart = AStart;
                        blockAEnd = AEnd;
                        firstAStart = AStart;
                        firstAEnd = AStart + (blockAEnd - blockAStart) % blockSize;
                        var indexA = buffer1Start;

                        for (index = firstAEnd; index < blockAEnd; index += blockSize) swap(data, indexA++, index);

                        lastAStart = firstAStart;
                        lastAEnd = firstAEnd;
                        lastBStart = 0;
                        lastBEnd = 0;
                        blockBStart = BStart;
                        blockBEnd = BStart + Math.min(blockSize, BEnd - BStart);
                        blockAStart += firstAEnd - firstAStart;
                        indexA = buffer1Start;

                        if (lastAEnd - lastAStart <= 512) copy(data, lastAStart, cache, 0, lastAEnd - lastAStart);
                        else if (buffer2End - buffer2Start > 0) blockSwap(data, lastAStart, buffer2Start, lastAEnd - lastAStart);

                        if (blockAEnd - blockAStart > 0) {
                            while (true) {
                                if ((lastBEnd - lastBStart > 0 && comparator.compare(data.get(lastBEnd - 1), data.get(indexA)) >= 0) || blockBEnd - blockBStart == 0) {
                                    var BSplit = binaryFirst(data, comparator, data.get(indexA), lastBStart, lastBEnd);
                                    var BRemaining = lastBEnd - BSplit;
                                    var minA = blockAStart;

                                    for (int findA = minA + blockSize; findA < blockAEnd; findA += blockSize) if (comparator.compare(data.get(findA), data.get(minA)) < 0) minA = findA;

                                    blockSwap(data, blockAStart, minA, blockSize);

                                    swap(data, blockAStart, indexA++);

                                    if (lastAEnd - lastAStart <= 512) mergeExternal(data, comparator, lastAStart, lastAEnd, lastAEnd, BSplit, cache);
                                    else if (buffer2End - buffer2Start > 0) mergeInternal(data, comparator, lastAStart, lastAEnd, lastAEnd, BSplit, buffer2Start);
                                    else mergeInPlace(data, comparator, lastAStart, lastAEnd, lastAEnd, BSplit, cache);

                                    if (buffer2End - buffer2Start > 0 || blockSize <= 512) {
                                        if (blockSize <= 512) copy(data, blockAStart, cache, 0, blockSize);
                                        else blockSwap(data, blockAStart, buffer2Start, blockSize);

                                        blockSwap(data, BSplit, blockAStart + blockSize - BRemaining, BRemaining);
                                    } else rotate(data, blockAStart - BSplit, BSplit, blockAStart + blockSize, cache);

                                    lastAStart = blockAStart - BRemaining;
                                    lastAEnd = blockAStart - BRemaining + blockSize;
                                    lastBStart = lastAEnd;
                                    lastBEnd = lastAEnd + BRemaining;
                                    blockAStart += blockSize;

                                    if (blockAEnd - blockAStart == 0) break;
                                } else if (blockBEnd - blockBStart < blockSize) {
                                    rotate(data, -blockBEnd + blockBStart, blockAStart, blockBEnd, null);
                                    lastBStart = blockAStart;
                                    lastBEnd = blockAStart + blockBEnd - blockBStart;
                                    blockAStart += blockBEnd - blockBStart;
                                    blockAEnd += blockBEnd - blockBStart;
                                    blockBEnd = blockBStart;
                                } else {
                                    blockSwap(data, blockAStart, blockBStart, blockSize);
                                    lastBStart = blockAStart;
                                    lastBEnd = blockAStart + blockSize;
                                    blockAStart += blockSize;
                                    blockAEnd += blockSize;
                                    blockBStart += blockSize;
                                    blockBEnd += blockSize;

                                    if (blockBEnd > BEnd) blockBEnd = BEnd;
                                }
                            }
                        }

                        if (lastAEnd - lastAStart <= 512) mergeExternal(data, comparator, lastAStart, lastAEnd, lastAEnd, BEnd, cache);
                        else if (buffer2End - buffer2Start > 0) mergeInternal(data, comparator, lastAStart, lastAEnd, lastAEnd, BEnd, buffer2Start);
                        else mergeInPlace(data, comparator, lastAStart, lastAEnd, lastAEnd, BEnd, cache);
                    }
                }

                Sort.insertion(data, comparator, buffer2Start, buffer2End);

                for (pullIndex = 0; pullIndex < 2; pullIndex++) {
                    var unique = pull[pullIndex].third() * 2;

                    if (pull[pullIndex].fourth() > pull[pullIndex].fifth()) {
                        var bufferStart = pull[pullIndex].first();
                        var bufferEnd = pull[pullIndex].first() + pull[pullIndex].third();

                        while (bufferEnd - bufferStart > 0) {
                            index = findFirstForward(data, comparator, data.get(bufferStart), bufferEnd, pull[pullIndex].second(), unique);
                            var amount = index - bufferEnd;
                            rotate(data, bufferEnd - bufferStart, bufferStart, index, cache);
                            bufferStart += (amount + 1);
                            bufferEnd += amount;
                            unique -= 2;
                        }
                    } else if (pull[pullIndex].fourth() < pull[pullIndex].fifth()) {
                        var bufferStart = pull[pullIndex].second() - pull[pullIndex].third();
                        var bufferEnd = pull[pullIndex].second();

                        while (bufferEnd - bufferStart > 0) {
                            index = findLastBackward(data, comparator, data.get(bufferEnd - 1), pull[pullIndex].first(), bufferStart, unique);
                            var amount = bufferStart - index;
                            rotate(data, amount, index, bufferEnd, cache);
                            bufferStart -= amount;
                            bufferEnd -= (amount + 1);
                            unique -= 2;
                        }
                    }
                }
            }

            decimalStep += decimalStep;
            numeratorStep += numeratorStep;

            if (numeratorStep >= denominator) {
                numeratorStep -= denominator;
                decimalStep++;
            }
        } while (decimalStep < data.size());
    }
}