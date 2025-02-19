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

    int BinaryFirst(List<D> data, Comparator<D> comparator, D value, int rangeStart, int rangeEnd) {
        int start = rangeStart, end = rangeEnd - 1;
        while (start < end) {
            int mid = start + (end - start)/2;
            if (comparator.compare(data.get(mid), value) < 0)
                start = mid + 1;
            else
                end = mid;
        }
        if (start == rangeEnd - 1 && comparator.compare(data.get(start), value) < 0) start++;
        return start;
    }

    int BinaryLast(List<D> data,  Comparator<D> comparator, D value, int rangeStart, int rangeEnd) {
        int start = rangeStart, end = rangeEnd - 1;
        while (start < end) {
            int mid = start + (end - start)/2;
            if (comparator.compare(value, data.get(mid)) >= 0)
                start = mid + 1;
            else
                end = mid;
        }
        if (start == rangeEnd - 1 && comparator.compare(value, data.get(start)) >= 0) start++;
        return start;
    }

    int FindFirstForward(List<D> data, Comparator<D> comparator, D value, int rangeStart, int rangeEnd, int unique) {
        if (rangeEnd - rangeStart == 0) return rangeStart;
        int index, skip = Math.max((rangeEnd - rangeStart)/unique, 1);

        for (index = rangeStart + skip; comparator.compare(data.get(index - 1), value) < 0; index += skip)
            if (index >= rangeEnd - skip)
                return BinaryFirst(data, comparator, value, index, rangeEnd);

        return BinaryFirst(data, comparator, value, index - skip, index);
    }

    int FindLastForward(List<D> data, Comparator<D> comparator, D value, int rangeStart, int rangeEnd, int unique) {
        if (rangeEnd - rangeStart== 0) return rangeStart;
        int index, skip = Math.max((rangeEnd - rangeStart) / unique, 1);

        for (index = rangeStart + skip; comparator.compare(value, data.get(index - 1)) >= 0; index += skip)
            if (index >= rangeEnd - skip)
                return BinaryLast(data, comparator, value, index, rangeEnd);

        return BinaryLast(data, comparator, value, index - skip, index);
    }

    int FindFirstBackward(List<D> data, Comparator<D> comparator, D value, int rangeStart, int rangeEnd, int unique) {
        if (rangeEnd - rangeStart == 0) return rangeStart;
        int index, skip = Math.max((rangeEnd - rangeStart) / unique, 1);

        for (index = rangeEnd - skip; index > rangeStart && comparator.compare(data.get(index - 1), value) >= 0; index -= skip)
            if (index < rangeStart + skip)
                return BinaryFirst(data, comparator, value, rangeStart, index);

        return BinaryFirst(data, comparator, value, index, index + skip);
    }

    int FindLastBackward(List<D> data, Comparator<D> comparator, D value, int rangeStart, int rangeEnd, int unique) {
        if (rangeEnd - rangeStart == 0) return rangeStart;
        int index, skip = Math.max((rangeEnd - rangeStart) / unique, 1);

        for (index = rangeEnd - skip; index > rangeStart && comparator.compare(value, data.get(index - 1)) < 0; index -= skip)
            if (index < rangeStart + skip)
                return BinaryLast(data, comparator, value, rangeStart, index);

        return BinaryLast(data, comparator, value, index, index + skip);
    }

    void BlockSwap(List<D> data, int start1, int start2, int block_size) {
        for (int index = 0; index < block_size; index++) swap(data, start1 + index, start2 + index);
    }

    void Rotate(List<D> data, int amount, int rangeStart, int rangeEnd, D[] cache) {
        if (rangeEnd - rangeStart == 0) return;

        int split;
        if (amount >= 0)
            split = rangeStart + amount;
        else
            split = rangeEnd + amount;

        var range1Start = rangeStart;
        var range1End = split;
        var range2Start = split;
        var range2End = rangeEnd;

        if (cache != null) {
            if (range1End - range1Start <= range2End - range2Start) {
                if (range1End - range1Start <= 512) {
                    copy(data, range1Start, cache, 0, range1End - range1Start);
                    copy(data, range2Start, data, range1Start, range2End - range2Start);
                    copy(cache, 0, data, range1Start + range2End - range2Start, range1End - range1Start);
                    return;
                }
            } else {
                if (range2End - range2Start <= 512) {
                    copy(data, range2Start, cache, 0, range2End - range2Start);
                    copy(data, range1Start, data, range2End - range1End + range1Start, range1End - range1Start);
                    copy(cache, 0, data, range1Start, range2End - range2Start);
                    return;
                }
            }
        }

        flip(data, range1Start, range1End);
        flip(data, range2Start, range2End);
        flip(data, rangeStart, rangeEnd);
    }

    void MergeInto(List<D> from, Comparator<D> comparator, int AStart, int AEnd, int BStart, int BEnd, D[] into, int at_index) {
        int A_index = AStart;
        int B_index = BStart;
        int insert_index = at_index;
        int A_last = AEnd;
        int B_last = BEnd;

        while (true) {
            if (comparator.compare(from.get(B_index), from.get(A_index)) >= 0) {
                into[insert_index] = from.get(A_index);
                A_index++;
                insert_index++;
                if (A_index == A_last) {
                    copy(from, B_index, into, insert_index, B_last - B_index);
                    break;
                }
            } else {
                into[insert_index] = from.get(B_index);
                B_index++;
                insert_index++;
                if (B_index == B_last) {
                    copy(from, A_index, into, insert_index, A_last - A_index);
                    break;
                }
            }
        }
    }

    void MergeInto(D[] from, Comparator<D> comparator, int AStart, int AEnd, int BStart, int BEnd, List<D> into, int at_index) {
        int A_index = AStart;
        int B_index = BStart;
        int insert_index = at_index;
        int A_last = AEnd;
        int B_last = BEnd;

        while (true) {
            if (comparator.compare(from[B_index], from[A_index]) >= 0) {
                into.set(insert_index, from[A_index]);
                A_index++;
                insert_index++;
                if (A_index == A_last) {
                    copy(from, B_index, into, insert_index, B_last - B_index);
                    break;
                }
            } else {
                into.set(insert_index, from[B_index]);
                B_index++;
                insert_index++;
                if (B_index == B_last) {
                    copy(from, A_index, into, insert_index, A_last - A_index);
                    break;
                }
            }
        }
    }

    void MergeExternal(List<D> data, Comparator<D> comparator, int AStart, int AEnd, int BStart, int BEnd, D[] cache) {
        int A_index = 0;
        int B_index = BStart;
        int insert_index = AStart;
        int A_last = AEnd - AStart;
        int B_last = BEnd;

        if (BEnd - BStart > 0 && AEnd - AStart > 0) {
            while (true) {
                if (comparator.compare(data.get(B_index), cache[A_index]) >= 0) {
                    data.set(insert_index, cache[A_index]);
                    A_index++;
                    insert_index++;
                    if (A_index == A_last) break;
                } else {
                    data.set(insert_index, data.get(B_index));
                    B_index++;
                    insert_index++;
                    if (B_index == B_last) break;
                }
            }
        }

        copy(cache, A_index, data, insert_index, A_last - A_index);
    }

    void MergeInternal(List<D> data, Comparator<D> comparator, int AStart, int AEnd, int BStart, int BEnd, int bufferStart, int bufferEnd) {
        int A_count = 0, B_count = 0, insert = 0;

        if (BEnd - BStart > 0 && AEnd - AStart > 0) {
            while (true) {
                if (comparator.compare(data.get(BStart + B_count), data.get(bufferStart + A_count)) >= 0) {
                    swap(data, AStart + insert, bufferStart + A_count);
                    A_count++;
                    insert++;
                    if (A_count >= AEnd - AStart) break;
                } else {
                    swap(data, AStart + insert, BStart + B_count);
                    B_count++;
                    insert++;
                    if (B_count >= BEnd - BStart) break;
                }
            }
        }

        BlockSwap(data, bufferStart + A_count, AStart + insert, AEnd - AStart - A_count);
    }

    void MergeInPlace(List<D> data, Comparator<D> comparator, int AStart, int AEnd, int BStart, int BEnd, D[] cache) {
        if (AEnd - AStart == 0 || BEnd - BStart == 0) return;

        AStart = AStart;
        AEnd = AEnd;
        BStart = BStart;
        BEnd = BEnd;

        while (true) {
            int mid = BinaryFirst(data, comparator, data.get(AStart), BStart, BEnd);

            int amount = mid - AEnd;
            Rotate(data, -amount, AStart, mid, cache);

            if (BEnd == mid) break;

            BStart = mid;
            AStart = AStart + amount;
            AEnd = BStart;
            AStart = BinaryLast(data, comparator, data.get(AStart), AStart, AEnd);

            if (AEnd - AStart == 0) break;
        }
    }

    void NetSwap(List<D> data, Comparator<D> comparator, int[] order, int rangeStart, int rangeEnd, int x, int y) {
        int compare = comparator.compare(data.get(rangeStart + x), data.get(rangeStart + y));
        if (compare > 0 || (order[x] > order[y] && compare == 0)) {
            swap(data, rangeStart + x, rangeStart + y);
            int swap2 = order[x];
            order[x] = order[y];
            order[y] = swap2;
        }
    }

    @SuppressWarnings("unchecked")
    void Sort(List<D> data, Comparator<D> comparator) {
        D[] cache = (D[]) new Object[512];
        int size = data.size();

        if (size < 4) {
            Sort.binaryInsertion(data, comparator);

            return;
        }

        var powerOfTwo = size;
        powerOfTwo |= powerOfTwo >> 1;
        powerOfTwo |= powerOfTwo >> 2;
        powerOfTwo |= powerOfTwo >> 4;
        powerOfTwo |= powerOfTwo >> 8;
        powerOfTwo |= powerOfTwo >> 16;
        powerOfTwo -= powerOfTwo >> 1;
        var denominator = powerOfTwo / 4;
        var numeratorStep = size % denominator;
        var decimalStep = size / denominator;
        var numerator = 0;
        var decimal = 0;

        while (decimal < size) {
            int[] order = { 0, 1, 2, 3, 4, 5, 6, 7 };
            var rangeStart = decimal;
            decimal += decimalStep;
            numerator += numeratorStep;

            if (numerator >= denominator) {
                numerator -= denominator;
                decimal++;
            }

            var rangeEnd = decimal;

            if (rangeEnd - rangeStart == 8) {
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 0, 1);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 2, 3);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 4, 5);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 6, 7);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 0, 2);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 1, 3);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 4, 6);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 5, 7);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 1, 2);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 5, 6);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 0, 4);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 3, 7);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 1, 5);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 2, 6);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 1, 4);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 3, 6);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 2, 4);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 3, 5);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 3, 4);

            } else if (rangeEnd - rangeStart == 7) {
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 1, 2);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 3, 4);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 5, 6);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 0, 2);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 3, 5);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 4, 6);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 0, 1);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 4, 5);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 2, 6);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 0, 4);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 1, 5);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 0, 3);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 2, 5);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 1, 3);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 2, 4);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 2, 3);

            } else if (rangeEnd - rangeStart == 6) {
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 1, 2);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 4, 5);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 0, 2);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 3, 5);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 0, 1);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 3, 4);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 2, 5);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 0, 3);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 1, 4);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 2, 4);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 1, 3);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 2, 3);

            } else if (rangeEnd - rangeStart == 5) {
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 0, 1);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 3, 4);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 2, 4);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 2, 3);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 1, 4);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 0, 3);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 0, 2);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 1, 3);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 1, 2);

            } else if (rangeEnd - rangeStart == 4) {
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 0, 1);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 2, 3);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 0, 2);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 1, 3);
                NetSwap(data, comparator, order, rangeStart, rangeEnd, 1, 2);
            }
        }
        if (size < 8) return;

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
                if ((decimalStep + 1) * 4 <= 512 && decimalStep * 4 <= size) {
                    numerator = decimal = 0;
                    while (decimal < size) {
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
                        } else if (comparator.compare(data.get(B1Start), data.get(A1End - 1)) < 0) {
                            MergeInto(data, comparator, A1Start, A1End, B1Start, B1End, cache, 0);
                        } else {
                            if (comparator.compare(data.get(B2Start), data.get(A2End - 1)) >= 0 && comparator.compare(data.get(A2Start), data.get(B1End - 1)) >= 0)
                                continue;

                            copy(data, A1Start, cache, 0, A1End - A1Start);
                            copy(data, B1Start, cache, A1End - A1Start, B1End - B1Start);
                        }

                        A1End = B1End;

                        if (comparator.compare(data.get(B2End - 1), data.get(A2Start)) < 0) {
                            copy(data, A2Start, cache, A1End - A1Start + B2End - B2Start, A2End - A2Start);
                            copy(data, B2Start, cache, A1End - A1Start, B2End - B2Start);
                        } else if (comparator.compare(data.get(B2Start), data.get(A2End - 1)) < 0) {
                            MergeInto(data, comparator, A2Start, A2End, B2Start, B2End, cache, A1End - A1Start);
                        } else {
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
                            MergeInto(cache, comparator, A3Start, A3End, B3Start, B3End, data, A1Start);
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
                    numerator = decimal = 0;
                    while (decimal < size) {
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

                        if (comparator.compare(data.get(BEnd - 1), data.get(AStart)) < 0) {
                            Rotate(data, AEnd - AStart, AStart, BEnd, cache);
                        } else if (comparator.compare(data.get(BStart), data.get(AEnd - 1)) < 0) {
                            copy(data, AStart, cache, 0, AEnd - AStart);
                            MergeExternal(data, comparator, AStart, AEnd, BStart, BEnd, cache);
                        }
                    }
                }
            } else {

                int block_size = (int) Math.sqrt(decimalStep);
                int buffer_size = decimalStep / block_size + 1;

                int index, last, count, pull_index = 0;
                buffer1Start = 0;
                buffer1End = 0;
                buffer2Start = 0;
                buffer2End = 0;

                pull[0] = pull[0].withFirst(0).withSecond(0).withThird(0).withFourth(0).withFifth(0);
                pull[1] = pull[1].withFirst(0).withSecond(0).withThird(0).withFourth(0).withFifth(0);

                int find = buffer_size + buffer_size;
                boolean find_separately = false;

                if (block_size <= 512) {
                    find = buffer_size;
                } else if (find > decimalStep) {
                    find = buffer_size;
                    find_separately = true;
                }

                numerator = decimal = 0;
                while (decimal < size) {
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
                        index = FindLastForward(data, comparator, data.get(last), last + 1, AEnd, find - count);
                        if (index == AEnd) break;
                    }
                    index = last;

                    if (count >= buffer_size) {
                        pull[pull_index] = pull[pull_index].withFirst(AStart).withSecond(BEnd).withThird(count).withFourth(index).withFifth(AStart);
                        pull_index = 1;

                        if (count == buffer_size + buffer_size) {
                            buffer1Start = AStart;
                            buffer1End = AStart + buffer_size;
                            buffer2Start = AStart + buffer_size;
                            buffer2End = AStart + count;
                            break;
                        } else if (find == buffer_size + buffer_size) {
                            buffer1Start = AStart;
                            buffer1End = AStart + count;
                            find = buffer_size;
                        } else if (block_size <= 512) {
                            buffer1Start = AStart;
                            buffer1End = AStart + count;
                            break;
                        } else if (find_separately) {
                            buffer1Start = AStart;
                            buffer1End = AStart + count;
                            find_separately = false;
                        } else {
                            buffer2Start = AStart;
                            buffer2End = AStart + count;
                            break;
                        }
                    } else if (pull_index == 0 && count > buffer1End - buffer1Start) {
                        buffer1Start = AStart;
                        buffer1End = AStart + count;
                        pull[pull_index] = pull[pull_index].withFirst(AStart).withSecond(BEnd).withThird(count).withFourth(index).withFifth(AStart);
                    }

                    for (last = BEnd - 1, count = 1; count < find; last = index - 1, count++) {
                        index = FindFirstBackward(data, comparator, data.get(last), BStart, last, find - count);
                        if (index == BStart) break;
                    }
                    index = last;

                    if (count >= buffer_size) {
                        pull[pull_index] = pull[pull_index].withFirst(AStart).withSecond(BEnd).withThird(count).withFourth(index).withFifth(BEnd);
                        pull_index = 1;

                        if (count == buffer_size + buffer_size) {
                            buffer1Start = BEnd - count;
                            buffer1End = BEnd - buffer_size;
                            buffer2Start = BEnd - buffer_size;
                            buffer2End = BEnd;
                            break;
                        } else if (find == buffer_size + buffer_size) {
                            buffer1Start = BEnd - count;
                            buffer1End = BEnd;
                            find = buffer_size;
                        } else if (block_size <= 512) {
                            buffer1Start = BEnd - count;
                            buffer1End = BEnd;
                            break;
                        } else if (find_separately) {
                            buffer1Start = BEnd - count;
                            buffer1End = BEnd;
                            find_separately = false;
                        } else {
                            if (pull[0].first() == AStart) pull[0] = pull[0].withSecond((pull[0].second() - pull[1].third()));

                            buffer2Start = BEnd - count;
                            buffer2End = BEnd;
                            break;
                        }
                    } else if (pull_index == 0 && count > buffer1End - buffer1Start) {
                        buffer1Start = BEnd - count;
                        buffer1End = BEnd;
                        pull[pull_index] = pull[pull_index].withFirst(AStart).withSecond(BEnd).withThird(count).withFourth(index).withFifth(BEnd);
                    }
                }

                for (pull_index = 0; pull_index < 2; pull_index++) {
                    int length = pull[pull_index].third();

                    if (pull[pull_index].fifth() < pull[pull_index].fourth()) {
                        index = pull[pull_index].fourth();

                        for (count = 1; count < length; count++) {
                            index = FindFirstBackward(data, comparator, data.get(index - 1), pull[pull_index].fifth(), pull[pull_index].fourth() - (count - 1), length - count);
                            var rangeStart = index + 1;
                            var rangeEnd = pull[pull_index].fourth() + 1;
                            Rotate(data, rangeEnd - rangeStart - count, rangeStart, rangeEnd, cache);
                            pull[pull_index] = pull[pull_index].withFourth(index + count);
                        }
                    } else if (pull[pull_index].fifth() > pull[pull_index].fourth()) {
                        index = pull[pull_index].fourth() + 1;
                        for (count = 1; count < length; count++) {
                            index = FindLastForward(data, comparator, data.get(index), index, pull[pull_index].fifth(), length - count);
                            var rangeStart = pull[pull_index].fourth();
                            var rangeEnd = index - 1;
                            Rotate(data, count, rangeStart, rangeEnd, cache);
                            pull[pull_index] = pull[pull_index].withFourth(index - 1 - count);
                        }
                    }
                }

                buffer_size = buffer1End - buffer1Start;
                block_size = decimalStep / buffer_size + 1;

                numerator = decimal = 0;
                while (decimal < size) {
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

                    int start = AStart;
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

                    if (comparator.compare(data.get(BEnd - 1), data.get(AStart)) < 0) {
                        Rotate(data, AEnd - AStart, AStart, BEnd, cache);
                    } else if (comparator.compare(data.get(AEnd), data.get(AEnd - 1)) < 0) {
                        blockAStart = AStart;
                        blockAEnd = AEnd;
                        firstAStart = AStart;
                        firstAEnd = AStart + (blockAEnd - blockAStart) % block_size;
                        int indexA = buffer1Start;
                        for (index = firstAEnd; index < blockAEnd; index += block_size) {
                            swap(data, indexA, index);
                            indexA++;
                        }

                        lastAStart = firstAStart;
                        lastAEnd = firstAEnd;
                        lastBStart = 0;
                        lastBEnd = 0;
                        blockBStart = BStart;
                        blockBEnd = BStart + Math.min(block_size, BEnd - BStart);
                        blockAStart += firstAEnd - firstAStart;
                        indexA = buffer1Start;

                        if (lastAEnd - lastAStart <= 512)
                            copy(data, lastAStart, cache, 0, lastAEnd - lastAStart);
                        else if (buffer2End - buffer2Start > 0)
                            BlockSwap(data, lastAStart, buffer2Start, lastAEnd - lastAStart);

                        if (blockAEnd - blockAStart > 0) {
                            while (true) {
                                if ((lastBEnd - lastBStart > 0 && comparator.compare(data.get(lastBEnd - 1), data.get(indexA)) >= 0) || blockBEnd - blockBStart == 0) {
                                    int B_split = BinaryFirst(data, comparator, data.get(indexA), lastBStart, lastBEnd);
                                    int B_remaining = lastBEnd - B_split;

                                    int minA = blockAStart;
                                    for (int findA = minA + block_size; findA < blockAEnd; findA += block_size)
                                        if (comparator.compare(data.get(findA), data.get(minA)) < 0)
                                            minA = findA;
                                    BlockSwap(data, blockAStart, minA, block_size);

                                    swap(data, blockAStart, indexA);
                                    indexA++;

                                    if (lastAEnd - lastAStart <= 512)
                                        MergeExternal(data, comparator, lastAStart, lastAEnd, lastAEnd, B_split, cache);
                                    else if (buffer2End - buffer2Start > 0)
                                        MergeInternal(data, comparator, lastAStart, lastAEnd, lastAEnd, B_split, buffer2Start, buffer2End);
                                    else
                                        MergeInPlace(data, comparator, lastAStart, lastAEnd, lastAEnd, B_split, cache);

                                    if (buffer2End - buffer2Start > 0 || block_size <= 512) {
                                        if (block_size <= 512)
                                            copy(data, blockAStart, cache, 0, block_size);
                                        else
                                            BlockSwap(data, blockAStart, buffer2Start, block_size);

                                        BlockSwap(data, B_split, blockAStart + block_size - B_remaining, B_remaining);
                                    } else {
                                        Rotate(data, blockAStart - B_split, B_split, blockAStart + block_size, cache);
                                    }

                                    lastAStart = blockAStart - B_remaining;
                                    lastAEnd = blockAStart - B_remaining + block_size;
                                    lastBStart = lastAEnd;
                                    lastBEnd = lastAEnd + B_remaining;

                                    blockAStart += block_size;
                                    if (blockAEnd - blockAStart == 0)
                                        break;

                                } else if (blockBEnd - blockBStart < block_size) {
                                    Rotate(data, -blockBEnd + blockBStart, blockAStart, blockBEnd, null);

                                    lastBStart = blockAStart;
                                    lastBEnd = blockAStart + blockBEnd - blockBStart;
                                    blockAStart += blockBEnd - blockBStart;
                                    blockAEnd += blockBEnd - blockBStart;
                                    blockBEnd = blockBStart;
                                } else {
                                    BlockSwap(data, blockAStart, blockBStart, block_size);
                                    lastBStart = blockAStart;
                                    lastBEnd = blockAStart + block_size;

                                    blockAStart += block_size;
                                    blockAEnd += block_size;
                                    blockBStart += block_size;
                                    blockBEnd += block_size;

                                    if (blockBEnd > BEnd)
                                        blockBEnd = BEnd;
                                }
                            }
                        }

                        if (lastAEnd - lastAStart <= 512)
                            MergeExternal(data, comparator, lastAStart, lastAEnd, lastAEnd, BEnd, cache);
                        else if (buffer2End - buffer2Start > 0)
                            MergeInternal(data, comparator, lastAStart, lastAEnd, lastAEnd, BEnd, buffer2Start, buffer2End);
                        else
                            MergeInPlace(data, comparator, lastAStart, lastAEnd, lastAEnd, BEnd, cache);
                    }
                }

                Sort.insertion(data, comparator, buffer2Start, buffer2End);

                for (pull_index = 0; pull_index < 2; pull_index++) {
                    int unique = pull[pull_index].third() * 2;
                    if (pull[pull_index].fourth() > pull[pull_index].fifth()) {
                        var bufferStart = pull[pull_index].first();
                        var bufferEnd = pull[pull_index].first() + pull[pull_index].third();
                        while (bufferEnd - bufferStart > 0) {
                            index = FindFirstForward(data, comparator, data.get(bufferStart), bufferEnd, pull[pull_index].second(), unique);
                            int amount = index - bufferEnd;
                            Rotate(data, bufferEnd - bufferStart, bufferStart, index, cache);
                            bufferStart += (amount + 1);
                            bufferEnd += amount;
                            unique -= 2;
                        }
                    } else if (pull[pull_index].fourth() < pull[pull_index].fifth()) {
                        var bufferStart = pull[pull_index].second() - pull[pull_index].third();
                        var bufferEnd = pull[pull_index].second();
                        while (bufferEnd - bufferStart > 0) {
                            index = FindLastBackward(data, comparator, data.get(bufferEnd - 1), pull[pull_index].first(), bufferStart, unique);
                            int amount = bufferStart - index;
                            Rotate(data, amount, index, bufferEnd, cache);
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
        } while (decimalStep < size);
    }
}