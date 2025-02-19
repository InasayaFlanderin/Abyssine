package org.inasayaflanderin.abyssine.miscellaneous.sort;

import lombok.extern.slf4j.Slf4j;
import org.inasayaflanderin.abyssine.primitives.Quin;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.*;

class Range {
    public int start;
    public int end;

    public Range(int start1, int end1) {
        start = start1;
        end = end1;
    }

    public Range() {
        start = 0;
        end = 0;
    }

    void set(int start1, int end1) {
        start = start1;
        end = end1;
    }
}

@Slf4j
class WikiSorter<D> {
    public static <D> void sort(D[] data, Comparator<D> comparator) {
        new WikiSorter<D>().Sort(Arrays.asList(data), comparator);
    }

    int BinaryFirst(List<D> data, Comparator<D> comparator, D value, Range range) {
        int start = range.start, end = range.end - 1;
        while (start < end) {
            int mid = start + (end - start)/2;
            if (comparator.compare(data.get(mid), value) < 0)
                start = mid + 1;
            else
                end = mid;
        }
        if (start == range.end - 1 && comparator.compare(data.get(start), value) < 0) start++;
        return start;
    }

    int BinaryLast(List<D> data,  Comparator<D> comparator, D value, Range range) {
        int start = range.start, end = range.end - 1;
        while (start < end) {
            int mid = start + (end - start)/2;
            if (comparator.compare(value, data.get(mid)) >= 0)
                start = mid + 1;
            else
                end = mid;
        }
        if (start == range.end - 1 && comparator.compare(value, data.get(start)) >= 0) start++;
        return start;
    }

    int FindFirstForward(List<D> data, Comparator<D> comparator, D value, Range range, int unique) {
        if (range.end - range.start == 0) return range.start;
        int index, skip = Math.max((range.end - range.start)/unique, 1);

        for (index = range.start + skip; comparator.compare(data.get(index - 1), value) < 0; index += skip)
            if (index >= range.end - skip)
                return BinaryFirst(data, comparator, value, new Range(index, range.end));

        return BinaryFirst(data, comparator, value, new Range(index - skip, index));
    }

    int FindLastForward(List<D> data, D value, Range range, Comparator<D> comparator, int unique) {
        if (range.end - range.start== 0) return range.start;
        int index, skip = Math.max((range.end - range.start) / unique, 1);

        for (index = range.start + skip; comparator.compare(value, data.get(index - 1)) >= 0; index += skip)
            if (index >= range.end - skip)
                return BinaryLast(data, comparator, value, new Range(index, range.end));

        return BinaryLast(data, comparator, value, new Range(index - skip, index));
    }

    int FindFirstBackward(List<D> data, Comparator<D> comparator, D value, Range range, int unique) {
        if (range.end - range.start == 0) return range.start;
        int index, skip = Math.max((range.end - range.start) / unique, 1);

        for (index = range.end - skip; index > range.start && comparator.compare(data.get(index - 1), value) >= 0; index -= skip)
            if (index < range.start + skip)
                return BinaryFirst(data, comparator, value, new Range(range.start, index));

        return BinaryFirst(data, comparator, value, new Range(index, index + skip));
    }

    int FindLastBackward(List<D> data, Comparator<D> comparator, D value, Range range, int unique) {
        if (range.end - range.start == 0) return range.start;
        int index, skip = Math.max((range.end - range.start) / unique, 1);

        for (index = range.end - skip; index > range.start && comparator.compare(value, data.get(index - 1)) < 0; index -= skip)
            if (index < range.start + skip)
                return BinaryLast(data, comparator, value, new Range(range.start, index));

        return BinaryLast(data, comparator, value, new Range(index, index + skip));
    }

    void BlockSwap(List<D> data, int start1, int start2, int block_size) {
        for (int index = 0; index < block_size; index++) swap(data, start1 + index, start2 + index);
    }

    void Rotate(List<D> data, int amount, Range range, D[] cache) {
        if (range.end - range.start == 0) return;

        int split;
        if (amount >= 0)
            split = range.start + amount;
        else
            split = range.end + amount;

        Range range1 = new Range(range.start, split);
        Range range2 = new Range(split, range.end);

        if (cache != null) {
            if (range1.end - range1.start <= range2.end - range2.start) {
                if (range1.end - range1.start <= 512) {
                    copy(data, range1.start, cache, 0, range1.end - range1.start);
                    copy(data, range2.start, data, range1.start, range2.end - range2.start);
                    copy(cache, 0, data, range1.start + range2.end - range2.start, range1.end - range1.start);
                    return;
                }
            } else {
                if (range2.end - range2.start <= 512) {
                    copy(data, range2.start, cache, 0, range2.end - range2.start);
                    copy(data, range1.start, data, range2.end - range1.end + range1.start, range1.end - range1.start);
                    copy(cache, 0, data, range1.start, range2.end - range2.start);
                    return;
                }
            }
        }

        flip(data, range1.start, range1.end);
        flip(data, range2.start, range2.end);
        flip(data, range.start, range.end);
    }

    void MergeInto(List<D> from, Comparator<D> comparator, Range A, Range B, D[] into, int at_index) {
        int A_index = A.start;
        int B_index = B.start;
        int insert_index = at_index;
        int A_last = A.end;
        int B_last = B.end;

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

    void MergeInto(D[] from, Comparator<D> comparator, Range A, Range B, List<D> into, int at_index) {
        int A_index = A.start;
        int B_index = B.start;
        int insert_index = at_index;
        int A_last = A.end;
        int B_last = B.end;

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

    void MergeExternal(List<D> data, Comparator<D> comparator, Range A, Range B, D[] cache) {
        int A_index = 0;
        int B_index = B.start;
        int insert_index = A.start;
        int A_last = A.end - A.start;
        int B_last = B.end;

        if (B.end - B.start > 0 && A.end - A.start > 0) {
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

    void MergeInternal(List<D> data, Comparator<D> comparator, Range A, Range B, Range buffer) {
        int A_count = 0, B_count = 0, insert = 0;

        if (B.end - B.start > 0 && A.end - A.start > 0) {
            while (true) {
                if (comparator.compare(data.get(B.start + B_count), data.get(buffer.start + A_count)) >= 0) {
                    swap(data, A.start + insert, buffer.start + A_count);
                    A_count++;
                    insert++;
                    if (A_count >= A.end - A.start) break;
                } else {
                    swap(data, A.start + insert, B.start + B_count);
                    B_count++;
                    insert++;
                    if (B_count >= B.end - B.start) break;
                }
            }
        }

        BlockSwap(data, buffer.start + A_count, A.start + insert, A.end - A.start - A_count);
    }

    void MergeInPlace(List<D> data, Comparator<D> comparator, Range A, Range B, D[] cache) {
        if (A.end - A.start == 0 || B.end - B.start == 0) return;


        A = new Range(A.start, A.end);
        B = new Range(B.start, B.end);

        while (true) {
            int mid = BinaryFirst(data, comparator, data.get(A.start), B);

            int amount = mid - A.end;
            Rotate(data, -amount, new Range(A.start, mid), cache);

            if (B.end == mid) break;

            B.start = mid;
            A.set(A.start + amount, B.start);
            A.start = BinaryLast(data, comparator, data.get(A.start), A);

            if (A.end - A.start == 0) break;
        }
    }

    void NetSwap(List<D> data, Comparator<D> comparator, int[] order, Range range, int x, int y) {
        int compare = comparator.compare(data.get(range.start + x), data.get(range.start + y));
        if (compare > 0 || (order[x] > order[y] && compare == 0)) {
            swap(data, range.start + x, range.start + y);
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
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 0, 1);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 2, 3);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 4, 5);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 6, 7);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 0, 2);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 1, 3);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 4, 6);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 5, 7);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 1, 2);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 5, 6);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 0, 4);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 3, 7);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 1, 5);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 2, 6);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 1, 4);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 3, 6);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 2, 4);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 3, 5);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 3, 4);

            } else if (rangeEnd - rangeStart == 7) {
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 1, 2);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 3, 4);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 5, 6);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 0, 2);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 3, 5);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 4, 6);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 0, 1);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 4, 5);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 2, 6);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 0, 4);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 1, 5);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 0, 3);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 2, 5);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 1, 3);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 2, 4);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 2, 3);

            } else if (rangeEnd - rangeStart == 6) {
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 1, 2);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 4, 5);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 0, 2);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 3, 5);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 0, 1);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 3, 4);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 2, 5);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 0, 3);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 1, 4);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 2, 4);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 1, 3);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 2, 3);

            } else if (rangeEnd - rangeStart == 5) {
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 0, 1);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 3, 4);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 2, 4);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 2, 3);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 1, 4);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 0, 3);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 0, 2);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 1, 3);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 1, 2);

            } else if (rangeEnd - rangeStart == 4) {
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 0, 1);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 2, 3);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 0, 2);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 1, 3);
                NetSwap(data, comparator, order, new Range(rangeStart, rangeEnd), 1, 2);
            }
        }
        if (size < 8) return;

        Range buffer1 = new Range(), buffer2 = new Range();
        Range blockA = new Range(), blockB = new Range();
        Range lastA = new Range(), lastB = new Range();
        Range firstA = new Range();
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
                            MergeInto(data, comparator, new Range(A1Start, A1End), new Range(B1Start, B1End), cache, 0);
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
                            MergeInto(data, comparator, new Range(A2Start, A2End), new Range(B2Start, B2End), cache, A1End - A1Start);
                        } else {
                            copy(data, A2Start, cache, A1End - A1Start, A2End - A2Start);
                            copy(data, B2Start, cache, A1End - A1Start + A2End - A2Start, B2End - B2Start);
                        }

                        A2End = B2End;

                        Range A3 = new Range(0, A1End - A1Start);
                        Range B3 = new Range(A1End - A1Start, A1End - A1Start + A2End - A2Start);

                        if (comparator.compare(cache[B3.end - 1], cache[A3.start]) < 0) {
                            copy(cache, A3.start, data, A1Start + A2End - A2Start, A3.end - A3.start);
                            copy(cache, B3.start, data, A1Start, B3.end - B3.start);
                        } else if (comparator.compare(cache[B3.start], cache[A3.end - 1]) < 0) {
                            MergeInto(cache, comparator, A3, B3, data, A1Start);
                        } else {
                            copy(cache, A3.start, data, A1Start, A3.end - A3.start);
                            copy(cache, B3.start, data, A1End, B3.end - B3.start);
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
                            Rotate(data, AEnd - AStart, new Range(AStart, BEnd), cache);
                        } else if (comparator.compare(data.get(BStart), data.get(AEnd - 1)) < 0) {
                            copy(data, AStart, cache, 0, AEnd - AStart);
                            MergeExternal(data, comparator, new Range(AStart, AEnd), new Range(BStart, BEnd), cache);
                        }
                    }
                }
            } else {

                int block_size = (int) Math.sqrt(decimalStep);
                int buffer_size = decimalStep / block_size + 1;

                int index, last, count, pull_index = 0;
                buffer1.set(0, 0);
                buffer2.set(0, 0);

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
                        index = FindLastForward(data, data.get(last), new Range(last + 1, AEnd), comparator, find - count);
                        if (index == AEnd) break;
                    }
                    index = last;

                    if (count >= buffer_size) {
                        pull[pull_index] = pull[pull_index].withFirst(AStart).withSecond(BEnd).withThird(count).withFourth(index).withFifth(AStart);
                        pull_index = 1;

                        if (count == buffer_size + buffer_size) {
                            buffer1.set(AStart, AStart + buffer_size);
                            buffer2.set(AStart + buffer_size, AStart + count);
                            break;
                        } else if (find == buffer_size + buffer_size) {
                            buffer1.set(AStart, AStart + count);
                            find = buffer_size;
                        } else if (block_size <= 512) {
                            buffer1.set(AStart, AStart + count);
                            break;
                        } else if (find_separately) {
                            buffer1 = new Range(AStart, AStart + count);
                            find_separately = false;
                        } else {
                            buffer2.set(AStart, AStart + count);
                            break;
                        }
                    } else if (pull_index == 0 && count > buffer1.end - buffer1.start) {
                        buffer1.set(AStart, AStart + count);
                        pull[pull_index] = pull[pull_index].withFirst(AStart).withSecond(BEnd).withThird(count).withFourth(index).withFifth(AStart);
                    }

                    for (last = BEnd - 1, count = 1; count < find; last = index - 1, count++) {
                        index = FindFirstBackward(data, comparator, data.get(last), new Range(BStart, last), find - count);
                        if (index == BStart) break;
                    }
                    index = last;

                    if (count >= buffer_size) {
                        pull[pull_index] = pull[pull_index].withFirst(AStart).withSecond(BEnd).withThird(count).withFourth(index).withFifth(BEnd);
                        pull_index = 1;

                        if (count == buffer_size + buffer_size) {
                            buffer1.set(BEnd - count, BEnd - buffer_size);
                            buffer2.set(BEnd - buffer_size, BEnd);
                            break;
                        } else if (find == buffer_size + buffer_size) {
                            buffer1.set(BEnd - count, BEnd);
                            find = buffer_size;
                        } else if (block_size <= 512) {
                            buffer1.set(BEnd - count, BEnd);
                            break;
                        } else if (find_separately) {
                            buffer1 = new Range(BEnd - count, BEnd);
                            find_separately = false;
                        } else {
                            if (pull[0].first() == AStart) pull[0] = pull[0].withSecond((pull[0].second() - pull[1].third()));

                            buffer2.set(BEnd - count, BEnd);
                            break;
                        }
                    } else if (pull_index == 0 && count > buffer1.end - buffer1.start) {
                        buffer1.set(BEnd - count, BEnd);
                        pull[pull_index] = pull[pull_index].withFirst(AStart).withSecond(BEnd).withThird(count).withFourth(index).withFifth(BEnd);
                    }
                }

                for (pull_index = 0; pull_index < 2; pull_index++) {
                    int length = pull[pull_index].third();

                    if (pull[pull_index].fifth() < pull[pull_index].fourth()) {
                        index = pull[pull_index].fourth();

                        for (count = 1; count < length; count++) {
                            index = FindFirstBackward(data, comparator, data.get(index - 1), new Range(pull[pull_index].fifth(), pull[pull_index].fourth() - (count - 1)), length - count);
                            Range range = new Range(index + 1, pull[pull_index].fourth() + 1);
                            Rotate(data, range.end - range.start - count, range, cache);
                            pull[pull_index] = pull[pull_index].withFourth(index + count);
                        }
                    } else if (pull[pull_index].fifth() > pull[pull_index].fourth()) {
                        index = pull[pull_index].fourth() + 1;
                        for (count = 1; count < length; count++) {
                            index = FindLastForward(data, data.get(index), new Range(index, pull[pull_index].fifth()), comparator, length - count);
                            Range range = new Range(pull[pull_index].fourth(), index - 1);
                            Rotate(data, count, range, cache);
                            pull[pull_index] = pull[pull_index].withFourth(index - 1 - count);
                        }
                    }
                }

                buffer_size = buffer1.end - buffer1.start;
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
                        Rotate(data, AEnd - AStart, new Range(AStart, BEnd), cache);
                    } else if (comparator.compare(data.get(AEnd), data.get(AEnd - 1)) < 0) {
                        blockA.set(AStart, AEnd);
                        firstA.set(AStart, AStart + (blockA.end - blockA.start) % block_size);
                        int indexA = buffer1.start;
                        for (index = firstA.end; index < blockA.end; index += block_size) {
                            swap(data, indexA, index);
                            indexA++;
                        }
                        lastA.set(firstA.start, firstA.end);
                        lastB.set(0, 0);
                        blockB.set(BStart, BStart + Math.min(block_size, BEnd - BStart));
                        blockA.start += firstA.end - firstA.start;
                        indexA = buffer1.start;

                        if (lastA.end - lastA.start <= 512)
                            copy(data, lastA.start, cache, 0, lastA.end - lastA.start);
                        else if (buffer2.end - buffer2.start > 0)
                            BlockSwap(data, lastA.start, buffer2.start, lastA.end - lastA.start);

                        if (blockA.end - blockA.start > 0) {
                            while (true) {
                                if ((lastB.end - lastB.start > 0 && comparator.compare(data.get(lastB.end - 1), data.get(indexA)) >= 0) || blockB.end - blockB.start == 0) {
                                    int B_split = BinaryFirst(data, comparator, data.get(indexA), lastB);
                                    int B_remaining = lastB.end - B_split;

                                    int minA = blockA.start;
                                    for (int findA = minA + block_size; findA < blockA.end; findA += block_size)
                                        if (comparator.compare(data.get(findA), data.get(minA)) < 0)
                                            minA = findA;
                                    BlockSwap(data, blockA.start, minA, block_size);

                                    swap(data, blockA.start, indexA);
                                    indexA++;

                                    if (lastA.end - lastA.start <= 512)
                                        MergeExternal(data, comparator, lastA, new Range(lastA.end, B_split), cache);
                                    else if (buffer2.end - buffer2.start > 0)
                                        MergeInternal(data, comparator, lastA, new Range(lastA.end, B_split), buffer2);
                                    else
                                        MergeInPlace(data, comparator, lastA, new Range(lastA.end, B_split), cache);

                                    if (buffer2.end - buffer2.start > 0 || block_size <= 512) {
                                        if (block_size <= 512)
                                            copy(data, blockA.start, cache, 0, block_size);
                                        else
                                            BlockSwap(data, blockA.start, buffer2.start, block_size);

                                        BlockSwap(data, B_split, blockA.start + block_size - B_remaining, B_remaining);
                                    } else {
                                        Rotate(data, blockA.start - B_split, new Range(B_split, blockA.start + block_size), cache);
                                    }

                                    lastA.set(blockA.start - B_remaining, blockA.start - B_remaining + block_size);
                                    lastB.set(lastA.end, lastA.end + B_remaining);

                                    blockA.start += block_size;
                                    if (blockA.end - blockA.start == 0)
                                        break;

                                } else if (blockB.end - blockB.start < block_size) {
                                    Rotate(data, -blockB.end + blockB.start, new Range(blockA.start, blockB.end), null);

                                    lastB.set(blockA.start, blockA.start + blockB.end - blockB.start);
                                    blockA.start += blockB.end - blockB.start;
                                    blockA.end += blockB.end - blockB.start;
                                    blockB.end = blockB.start;
                                } else {
                                    BlockSwap(data, blockA.start, blockB.start, block_size);
                                    lastB.set(blockA.start, blockA.start + block_size);

                                    blockA.start += block_size;
                                    blockA.end += block_size;
                                    blockB.start += block_size;
                                    blockB.end += block_size;

                                    if (blockB.end > BEnd)
                                        blockB.end = BEnd;
                                }
                            }
                        }

                        if (lastA.end - lastA.start <= 512)
                            MergeExternal(data, comparator, lastA, new Range(lastA.end, BEnd), cache);
                        else if (buffer2.end - buffer2.start > 0)
                            MergeInternal(data, comparator, lastA, new Range(lastA.end, BEnd), buffer2);
                        else
                            MergeInPlace(data, comparator, lastA, new Range(lastA.end, BEnd), cache);
                    }
                }

                Sort.insertion(data, comparator, buffer2.start, buffer2.end);

                for (pull_index = 0; pull_index < 2; pull_index++) {
                    int unique = pull[pull_index].third() * 2;
                    if (pull[pull_index].fourth() > pull[pull_index].fifth()) {
                        Range buffer = new Range(pull[pull_index].first(), pull[pull_index].first() + pull[pull_index].third());
                        while (buffer.end - buffer.start > 0) {
                            index = FindFirstForward(data, comparator, data.get(buffer.start), new Range(buffer.end, pull[pull_index].second()), unique);
                            int amount = index - buffer.end;
                            Rotate(data, buffer.end - buffer.start, new Range(buffer.start, index), cache);
                            buffer.start += (amount + 1);
                            buffer.end += amount;
                            unique -= 2;
                        }
                    } else if (pull[pull_index].fourth() < pull[pull_index].fifth()) {
                        Range buffer = new Range(pull[pull_index].second() - pull[pull_index].third(), pull[pull_index].second());
                        while (buffer.end - buffer.start > 0) {
                            index = FindLastBackward(data, comparator, data.get(buffer.end - 1), new Range(pull[pull_index].first(), buffer.start), unique);
                            int amount = buffer.start - index;
                            Rotate(data, amount, new Range(index, buffer.end), cache);
                            buffer.start -= amount;
                            buffer.end -= (amount + 1);
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