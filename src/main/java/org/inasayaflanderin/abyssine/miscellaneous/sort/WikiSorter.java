package org.inasayaflanderin.abyssine.miscellaneous.sort;

import org.inasayaflanderin.abyssine.primitives.Quin;

import java.util.Comparator;

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

    int length() {
        return end - start;
    }
}

//TODO range start = first, range end = second, count = third, from = fourth, to = fifth
class Pull {
    public int from, to, count;
    public Range range;
    public Pull() { range = new Range(0, 0); }
    void reset() {
        range.set(0, 0);
        from = 0;
        to = 0;
        count = 0;
    }
}
class Iterator {
    public int size, power_of_two;
    public int numerator, decimal;
    public int denominator, decimal_step, numerator_step;
    Iterator(int size2, int min_level) {
        size = size2;
        power_of_two = FloorPowerOfTwo(size);
        denominator = power_of_two/min_level;
        numerator_step = size % denominator;
        decimal_step = size/denominator;
        begin();
    }

    static int FloorPowerOfTwo(int value) {
        int x = value;
        x = x | (x >> 1);
        x = x | (x >> 2);
        x = x | (x >> 4);
        x = x | (x >> 8);
        x = x | (x >> 16);
        return x - (x >> 1);
    }

    void begin() {
        numerator = decimal = 0;
    }

    Range nextRange() {
        int start = decimal;

        decimal += decimal_step;
        numerator += numerator_step;
        if (numerator >= denominator) {
            numerator -= denominator;
            decimal++;
        }

        return new Range(start, decimal);
    }

    boolean finished() {
        return (decimal >= size);
    }

    boolean nextLevel() {
        decimal_step += decimal_step;
        numerator_step += numerator_step;
        if (numerator_step >= denominator) {
            numerator_step -= denominator;
            decimal_step++;
        }

        return (decimal_step < size);
    }

    int length() {
        return decimal_step;
    }
}

class WikiSorter<T> {
    private static final int cache_size = 512;
    private final T[] cache;

    public WikiSorter() {
        @SuppressWarnings("unchecked")
        T[] cache1 = (T[])new Object[cache_size];
        cache = cache1;
    }

    public static <T> void sort(T[] array, Comparator<T> comp) {
        new WikiSorter<T>().Sort(array, comp);
    }

    int BinaryFirst(T[] array, T value, Range range, Comparator<T> comp) {
        int start = range.start, end = range.end - 1;
        while (start < end) {
            int mid = start + (end - start)/2;
            if (comp.compare(array[mid], value) < 0)
                start = mid + 1;
            else
                end = mid;
        }
        if (start == range.end - 1 && comp.compare(array[start], value) < 0) start++;
        return start;
    }

    int BinaryLast(T[] array, T value, Range range, Comparator<T> comp) {
        int start = range.start, end = range.end - 1;
        while (start < end) {
            int mid = start + (end - start)/2;
            if (comp.compare(value, array[mid]) >= 0)
                start = mid + 1;
            else
                end = mid;
        }
        if (start == range.end - 1 && comp.compare(value, array[start]) >= 0) start++;
        return start;
    }

    int FindFirstForward(T[] array, T value, Range range, Comparator<T> comp, int unique) {
        if (range.length() == 0) return range.start;
        int index, skip = Math.max(range.length()/unique, 1);

        for (index = range.start + skip; comp.compare(array[index - 1], value) < 0; index += skip)
            if (index >= range.end - skip)
                return BinaryFirst(array, value, new Range(index, range.end), comp);

        return BinaryFirst(array, value, new Range(index - skip, index), comp);
    }

    int FindLastForward(T[] array, T value, Range range, Comparator<T> comp, int unique) {
        if (range.length() == 0) return range.start;
        int index, skip = Math.max(range.length()/unique, 1);

        for (index = range.start + skip; comp.compare(value, array[index - 1]) >= 0; index += skip)
            if (index >= range.end - skip)
                return BinaryLast(array, value, new Range(index, range.end), comp);

        return BinaryLast(array, value, new Range(index - skip, index), comp);
    }

    int FindFirstBackward(T[] array, T value, Range range, Comparator<T> comp, int unique) {
        if (range.length() == 0) return range.start;
        int index, skip = Math.max(range.length()/unique, 1);

        for (index = range.end - skip; index > range.start && comp.compare(array[index - 1], value) >= 0; index -= skip)
            if (index < range.start + skip)
                return BinaryFirst(array, value, new Range(range.start, index), comp);

        return BinaryFirst(array, value, new Range(index, index + skip), comp);
    }

    int FindLastBackward(T[] array, T value, Range range, Comparator<T> comp, int unique) {
        if (range.length() == 0) return range.start;
        int index, skip = Math.max(range.length()/unique, 1);

        for (index = range.end - skip; index > range.start && comp.compare(value, array[index - 1]) < 0; index -= skip)
            if (index < range.start + skip)
                return BinaryLast(array, value, new Range(range.start, index), comp);

        return BinaryLast(array, value, new Range(index, index + skip), comp);
    }

    void InsertionSort(T[] array, Range range, Comparator<T> comp) {
        for (int j, i = range.start + 1; i < range.end; i++) {
            T temp = array[i];
            for (j = i; j > range.start && comp.compare(temp, array[j - 1]) < 0; j--)
                array[j] = array[j - 1];
            array[j] = temp;
        }
    }

    void Reverse(T[] array, Range range) {
        for (int index = range.length()/2 - 1; index >= 0; index--) {
            T swap = array[range.start + index];
            array[range.start + index] = array[range.end - index - 1];
            array[range.end - index - 1] = swap;
        }
    }

    void BlockSwap(T[] array, int start1, int start2, int block_size) {
        for (int index = 0; index < block_size; index++) {
            T swap = array[start1 + index];
            array[start1 + index] = array[start2 + index];
            array[start2 + index] = swap;
        }
    }

    void Rotate(T[] array, int amount, Range range, boolean use_cache) {
        if (range.length() == 0) return;

        int split;
        if (amount >= 0)
            split = range.start + amount;
        else
            split = range.end + amount;

        Range range1 = new Range(range.start, split);
        Range range2 = new Range(split, range.end);

        if (use_cache) {
            if (range1.length() <= range2.length()) {
                if (range1.length() <= cache_size) {
                    System.arraycopy(array, range1.start, cache, 0, range1.length());
                    System.arraycopy(array, range2.start, array, range1.start, range2.length());
                    System.arraycopy(cache, 0, array, range1.start + range2.length(), range1.length());
                    return;
                }
            } else {
                if (range2.length() <= cache_size) {
                    System.arraycopy(array, range2.start, cache, 0, range2.length());
                    System.arraycopy(array, range1.start, array, range2.end - range1.length(), range1.length());
                    System.arraycopy(cache, 0, array, range1.start, range2.length());
                    return;
                }
            }
        }

        Reverse(array, range1);
        Reverse(array, range2);
        Reverse(array, range);
    }

    void MergeInto(T[] from, Range A, Range B, Comparator<T> comp, T[] into, int at_index) {
        int A_index = A.start;
        int B_index = B.start;
        int insert_index = at_index;
        int A_last = A.end;
        int B_last = B.end;

        while (true) {
            if (comp.compare(from[B_index], from[A_index]) >= 0) {
                into[insert_index] = from[A_index];
                A_index++;
                insert_index++;
                if (A_index == A_last) {
                    java.lang.System.arraycopy(from, B_index, into, insert_index, B_last - B_index);
                    break;
                }
            } else {
                into[insert_index] = from[B_index];
                B_index++;
                insert_index++;
                if (B_index == B_last) {
                    java.lang.System.arraycopy(from, A_index, into, insert_index, A_last - A_index);
                    break;
                }
            }
        }
    }

    void MergeExternal(T[] array, Range A, Range B, Comparator<T> comp) {
        int A_index = 0;
        int B_index = B.start;
        int insert_index = A.start;
        int A_last = A.length();
        int B_last = B.end;

        if (B.length() > 0 && A.length() > 0) {
            while (true) {
                if (comp.compare(array[B_index], cache[A_index]) >= 0) {
                    array[insert_index] = cache[A_index];
                    A_index++;
                    insert_index++;
                    if (A_index == A_last) break;
                } else {
                    array[insert_index] = array[B_index];
                    B_index++;
                    insert_index++;
                    if (B_index == B_last) break;
                }
            }
        }

        System.arraycopy(cache, A_index, array, insert_index, A_last - A_index);
    }

    void MergeInternal(T[] array, Range A, Range B, Comparator<T> comp, Range buffer) {
        int A_count = 0, B_count = 0, insert = 0;

        if (B.length() > 0 && A.length() > 0) {
            while (true) {
                if (comp.compare(array[B.start + B_count], array[buffer.start + A_count]) >= 0) {
                    T swap = array[A.start + insert];
                    array[A.start + insert] = array[buffer.start + A_count];
                    array[buffer.start + A_count] = swap;
                    A_count++;
                    insert++;
                    if (A_count >= A.length()) break;
                } else {
                    T swap = array[A.start + insert];
                    array[A.start + insert] = array[B.start + B_count];
                    array[B.start + B_count] = swap;
                    B_count++;
                    insert++;
                    if (B_count >= B.length()) break;
                }
            }
        }

        BlockSwap(array, buffer.start + A_count, A.start + insert, A.length() - A_count);
    }

    void MergeInPlace(T[] array, Range A, Range B, Comparator<T> comp) {
        if (A.length() == 0 || B.length() == 0) return;


        A = new Range(A.start, A.end);
        B = new Range(B.start, B.end);

        while (true) {
            int mid = BinaryFirst(array, array[A.start], B, comp);

            int amount = mid - A.end;
            Rotate(array, -amount, new Range(A.start, mid), true);
            if (B.end == mid) break;

            B.start = mid;
            A.set(A.start + amount, B.start);
            A.start = BinaryLast(array, array[A.start], A, comp);
            if (A.length() == 0) break;
        }
    }

    void NetSwap(T[] array, int[] order, Range range, Comparator<T> comp, int x, int y) {
        int compare = comp.compare(array[range.start + x], array[range.start + y]);
        if (compare > 0 || (order[x] > order[y] && compare == 0)) {
            T swap = array[range.start + x];
            array[range.start + x] = array[range.start + y];
            array[range.start + y] = swap;
            int swap2 = order[x];
            order[x] = order[y];
            order[y] = swap2;
        }
    }

    @SuppressWarnings("unchecked")
    void Sort(T[] array, Comparator<T> comp) {
        int size = array.length;

        if (size < 4) {
            if (size == 3) {
                if (comp.compare(array[1], array[0]) < 0) {
                    T swap = array[0];
                    array[0] = array[1];
                    array[1] = swap;
                }
                if (comp.compare(array[2], array[1]) < 0) {
                    T swap = array[1];
                    array[1] = array[2];
                    array[2] = swap;
                    if (comp.compare(array[1], array[0]) < 0) {
                        swap = array[0];
                        array[0] = array[1];
                        array[1] = swap;
                    }
                }
            } else if (size == 2) {
                if (comp.compare(array[1], array[0]) < 0) {
                    T swap = array[0];
                    array[0] = array[1];
                    array[1] = swap;
                }
            }

            return;
        }

        Iterator iterator = new Iterator(size, 4);
        while (!iterator.finished()) {
            int[] order = { 0, 1, 2, 3, 4, 5, 6, 7 };
            Range range = iterator.nextRange();

            if (range.length() == 8) {
                NetSwap(array, order, range, comp, 0, 1);
                NetSwap(array, order, range, comp, 2, 3);
                NetSwap(array, order, range, comp, 4, 5);
                NetSwap(array, order, range, comp, 6, 7);
                NetSwap(array, order, range, comp, 0, 2);
                NetSwap(array, order, range, comp, 1, 3);
                NetSwap(array, order, range, comp, 4, 6);
                NetSwap(array, order, range, comp, 5, 7);
                NetSwap(array, order, range, comp, 1, 2);
                NetSwap(array, order, range, comp, 5, 6);
                NetSwap(array, order, range, comp, 0, 4);
                NetSwap(array, order, range, comp, 3, 7);
                NetSwap(array, order, range, comp, 1, 5);
                NetSwap(array, order, range, comp, 2, 6);
                NetSwap(array, order, range, comp, 1, 4);
                NetSwap(array, order, range, comp, 3, 6);
                NetSwap(array, order, range, comp, 2, 4);
                NetSwap(array, order, range, comp, 3, 5);
                NetSwap(array, order, range, comp, 3, 4);

            } else if (range.length() == 7) {
                NetSwap(array, order, range, comp, 1, 2);
                NetSwap(array, order, range, comp, 3, 4);
                NetSwap(array, order, range, comp, 5, 6);
                NetSwap(array, order, range, comp, 0, 2);
                NetSwap(array, order, range, comp, 3, 5);
                NetSwap(array, order, range, comp, 4, 6);
                NetSwap(array, order, range, comp, 0, 1);
                NetSwap(array, order, range, comp, 4, 5);
                NetSwap(array, order, range, comp, 2, 6);
                NetSwap(array, order, range, comp, 0, 4);
                NetSwap(array, order, range, comp, 1, 5);
                NetSwap(array, order, range, comp, 0, 3);
                NetSwap(array, order, range, comp, 2, 5);
                NetSwap(array, order, range, comp, 1, 3);
                NetSwap(array, order, range, comp, 2, 4);
                NetSwap(array, order, range, comp, 2, 3);

            } else if (range.length() == 6) {
                NetSwap(array, order, range, comp, 1, 2);
                NetSwap(array, order, range, comp, 4, 5);
                NetSwap(array, order, range, comp, 0, 2);
                NetSwap(array, order, range, comp, 3, 5);
                NetSwap(array, order, range, comp, 0, 1);
                NetSwap(array, order, range, comp, 3, 4);
                NetSwap(array, order, range, comp, 2, 5);
                NetSwap(array, order, range, comp, 0, 3);
                NetSwap(array, order, range, comp, 1, 4);
                NetSwap(array, order, range, comp, 2, 4);
                NetSwap(array, order, range, comp, 1, 3);
                NetSwap(array, order, range, comp, 2, 3);

            } else if (range.length() == 5) {
                NetSwap(array, order, range, comp, 0, 1);
                NetSwap(array, order, range, comp, 3, 4);
                NetSwap(array, order, range, comp, 2, 4);
                NetSwap(array, order, range, comp, 2, 3);
                NetSwap(array, order, range, comp, 1, 4);
                NetSwap(array, order, range, comp, 0, 3);
                NetSwap(array, order, range, comp, 0, 2);
                NetSwap(array, order, range, comp, 1, 3);
                NetSwap(array, order, range, comp, 1, 2);

            } else if (range.length() == 4) {
                NetSwap(array, order, range, comp, 0, 1);
                NetSwap(array, order, range, comp, 2, 3);
                NetSwap(array, order, range, comp, 0, 2);
                NetSwap(array, order, range, comp, 1, 3);
                NetSwap(array, order, range, comp, 1, 2);
            }
        }
        if (size < 8) return;

        Range buffer1 = new Range(), buffer2 = new Range();
        Range blockA = new Range(), blockB = new Range();
        Range lastA = new Range(), lastB = new Range();
        Range firstA = new Range();
        Range A, B;

        Quin<Integer, Integer, Integer, Integer, Integer>[] pull = (Quin<Integer, Integer, Integer, Integer, Integer>[]) new Quin[2];
        pull[0] = new Quin<>(0, 0, 0, 0, 0);
        pull[1] = new Quin<>(0, 0, 0, 0, 0);

        do {

            if (iterator.length() < cache_size) {
                if ((iterator.length() + 1) * 4 <= cache_size && iterator.length() * 4 <= size) {
                    iterator.begin();
                    while (!iterator.finished()) {
                        Range A1 = iterator.nextRange();
                        Range B1 = iterator.nextRange();
                        Range A2 = iterator.nextRange();
                        Range B2 = iterator.nextRange();

                        if (comp.compare(array[B1.end - 1], array[A1.start]) < 0) {
                            System.arraycopy(array, A1.start, cache, B1.length(), A1.length());
                            System.arraycopy(array, B1.start, cache, 0, B1.length());
                        } else if (comp.compare(array[B1.start], array[A1.end - 1]) < 0) {
                            MergeInto(array, A1, B1, comp, cache, 0);
                        } else {
                            if (comp.compare(array[B2.start], array[A2.end - 1]) >= 0 && comp.compare(array[A2.start], array[B1.end - 1]) >= 0)
                                continue;

                            System.arraycopy(array, A1.start, cache, 0, A1.length());
                            System.arraycopy(array, B1.start, cache, A1.length(), B1.length());
                        }
                        A1.set(A1.start, B1.end);

                        if (comp.compare(array[B2.end - 1], array[A2.start]) < 0) {
                            System.arraycopy(array, A2.start, cache, A1.length() + B2.length(), A2.length());
                            System.arraycopy(array, B2.start, cache, A1.length(), B2.length());
                        } else if (comp.compare(array[B2.start], array[A2.end - 1]) < 0) {
                            MergeInto(array, A2, B2, comp, cache, A1.length());
                        } else {
                            System.arraycopy(array, A2.start, cache, A1.length(), A2.length());
                            System.arraycopy(array, B2.start, cache, A1.length() + A2.length(), B2.length());
                        }
                        A2.set(A2.start, B2.end);

                        Range A3 = new Range(0, A1.length());
                        Range B3 = new Range(A1.length(), A1.length() + A2.length());

                        if (comp.compare(cache[B3.end - 1], cache[A3.start]) < 0) {
                            System.arraycopy(cache, A3.start, array, A1.start + A2.length(), A3.length());
                            System.arraycopy(cache, B3.start, array, A1.start, B3.length());
                        } else if (comp.compare(cache[B3.start], cache[A3.end - 1]) < 0) {
                            MergeInto(cache, A3, B3, comp, array, A1.start);
                        } else {
                            System.arraycopy(cache, A3.start, array, A1.start, A3.length());
                            System.arraycopy(cache, B3.start, array, A1.start + A1.length(), B3.length());
                        }
                    }
                    iterator.nextLevel();

                } else {
                    iterator.begin();
                    while (!iterator.finished()) {
                        A = iterator.nextRange();
                        B = iterator.nextRange();

                        if (comp.compare(array[B.end - 1], array[A.start]) < 0) {
                            Rotate(array, A.length(), new Range(A.start, B.end), true);
                        } else if (comp.compare(array[B.start], array[A.end - 1]) < 0) {
                            System.arraycopy(array, A.start, cache, 0, A.length());
                            MergeExternal(array, A, B, comp);
                        }
                    }
                }
            } else {

                int block_size = (int) Math.sqrt(iterator.length());
                int buffer_size = iterator.length() / block_size + 1;

                int index, last, count, pull_index = 0;
                buffer1.set(0, 0);
                buffer2.set(0, 0);

                pull[0] = pull[0].withFirst(0).withSecond(0).withThird(0).withFourth(0).withFifth(0);
                pull[1] = pull[1].withFirst(0).withSecond(0).withThird(0).withFourth(0).withFifth(0);

                int find = buffer_size + buffer_size;
                boolean find_separately = false;

                if (block_size <= cache_size) {
                    find = buffer_size;
                } else if (find > iterator.length()) {
                    find = buffer_size;
                    find_separately = true;
                }


                iterator.begin();
                while (!iterator.finished()) {
                    A = iterator.nextRange();
                    B = iterator.nextRange();

                    for (last = A.start, count = 1; count < find; last = index, count++) {
                        index = FindLastForward(array, array[last], new Range(last + 1, A.end), comp, find - count);
                        if (index == A.end) break;
                    }
                    index = last;

                    if (count >= buffer_size) {
                        pull[pull_index] = pull[pull_index].withFirst(A.start).withSecond(B.end).withThird(count).withFourth(index).withFifth(A.start);
                        pull_index = 1;

                        if (count == buffer_size + buffer_size) {
                            buffer1.set(A.start, A.start + buffer_size);
                            buffer2.set(A.start + buffer_size, A.start + count);
                            break;
                        } else if (find == buffer_size + buffer_size) {
                            buffer1.set(A.start, A.start + count);
                            find = buffer_size;
                        } else if (block_size <= cache_size) {
                            buffer1.set(A.start, A.start + count);
                            break;
                        } else if (find_separately) {
                            buffer1 = new Range(A.start, A.start + count);
                            find_separately = false;
                        } else {
                            buffer2.set(A.start, A.start + count);
                            break;
                        }
                    } else if (pull_index == 0 && count > buffer1.length()) {
                        buffer1.set(A.start, A.start + count);
                        pull[pull_index].withFirst(A.start).withSecond(B.end).withThird(count).withFourth(index).withFifth(A.start);
                    }

                    for (last = B.end - 1, count = 1; count < find; last = index - 1, count++) {
                        index = FindFirstBackward(array, array[last], new Range(B.start, last), comp, find - count);
                        if (index == B.start) break;
                    }
                    index = last;

                    if (count >= buffer_size) {
                        pull[pull_index] = pull[pull_index].withFirst(A.start).withSecond(B.end).withThird(count).withFourth(index).withFifth(B.end);
                        pull_index = 1;

                        if (count == buffer_size + buffer_size) {
                            buffer1.set(B.end - count, B.end - buffer_size);
                            buffer2.set(B.end - buffer_size, B.end);
                            break;
                        } else if (find == buffer_size + buffer_size) {
                            buffer1.set(B.end - count, B.end);
                            find = buffer_size;
                        } else if (block_size <= cache_size) {
                            buffer1.set(B.end - count, B.end);
                            break;
                        } else if (find_separately) {
                            buffer1 = new Range(B.end - count, B.end);
                            find_separately = false;
                        } else {
                            if (pull[0].first() == A.start) pull[0] = pull[0].withSecond((pull[0].second() - pull[1].third()));

                            buffer2.set(B.end - count, B.end);
                            break;
                        }
                    } else if (pull_index == 0 && count > buffer1.length()) {
                        buffer1.set(B.end - count, B.end);
                        pull[pull_index] = pull[pull_index].withFirst(A.start).withSecond(B.end).withThird(count).withFourth(index).withFifth(B.end);
                    }
                }

                for (pull_index = 0; pull_index < 2; pull_index++) {
                    int length = pull[pull_index].third();

                    if (pull[pull_index].fifth() < pull[pull_index].fourth()) {
                        index = pull[pull_index].fourth();
                        for (count = 1; count < length; count++) {
                            index = FindFirstBackward(array, array[index - 1], new Range(pull[pull_index].fifth(), pull[pull_index].fourth() - (count - 1)), comp, length - count);
                            Range range = new Range(index + 1, pull[pull_index].fourth() + 1);
                            Rotate(array, range.length() - count, range, true);
                            pull[pull_index] = pull[pull_index].withFourth(index + count);
                        }
                    } else if (pull[pull_index].fifth() > pull[pull_index].fourth()) {
                        index = pull[pull_index].fourth() + 1;
                        for (count = 1; count < length; count++) {
                            index = FindLastForward(array, array[index], new Range(index, pull[pull_index].fifth()), comp, length - count);
                            Range range = new Range(pull[pull_index].fourth(), index - 1);
                            Rotate(array, count, range, true);
                            pull[pull_index] = pull[pull_index].withFourth(index - 1 - count);
                        }
                    }
                }

                buffer_size = buffer1.length();
                block_size = iterator.length() / buffer_size + 1;

                iterator.begin();
                while (!iterator.finished()) {
                    A = iterator.nextRange();
                    B = iterator.nextRange();

                    int start = A.start;
                    if (start == pull[0].first()) {
                        if (pull[0].fourth() > pull[0].fifth()) {
                            A.start += pull[0].third();

                            if (A.length() == 0) continue;
                        } else if (pull[0].fourth() < pull[0].fifth()) {
                            B.end -= pull[0].third();
                            if (B.length() == 0) continue;
                        }
                    }
                    if (start == pull[1].first()) {
                        if (pull[1].fourth() > pull[1].fifth()) {
                            A.start += pull[1].third();
                            if (A.length() == 0) continue;
                        } else if (pull[1].fourth() < pull[1].fifth()) {
                            B.end -= pull[1].third();
                            if (B.length() == 0) continue;
                        }
                    }

                    if (comp.compare(array[B.end - 1], array[A.start]) < 0) {
                        Rotate(array, A.length(), new Range(A.start, B.end), true);
                    } else if (comp.compare(array[A.end], array[A.end - 1]) < 0) {
                        blockA.set(A.start, A.end);
                        firstA.set(A.start, A.start + blockA.length() % block_size);
                        int indexA = buffer1.start;
                        for (index = firstA.end; index < blockA.end; index += block_size) {
                            T swap = array[indexA];
                            array[indexA] = array[index];
                            array[index] = swap;
                            indexA++;
                        }
                        lastA.set(firstA.start, firstA.end);
                        lastB.set(0, 0);
                        blockB.set(B.start, B.start + Math.min(block_size, B.length()));
                        blockA.start += firstA.length();
                        indexA = buffer1.start;

                        if (lastA.length() <= cache_size)
                            System.arraycopy(array, lastA.start, cache, 0, lastA.length());
                        else if (buffer2.length() > 0)
                            BlockSwap(array, lastA.start, buffer2.start, lastA.length());

                        if (blockA.length() > 0) {
                            while (true) {
                                if ((lastB.length() > 0 && comp.compare(array[lastB.end - 1], array[indexA]) >= 0) || blockB.length() == 0) {
                                    int B_split = BinaryFirst(array, array[indexA], lastB, comp);
                                    int B_remaining = lastB.end - B_split;

                                    int minA = blockA.start;
                                    for (int findA = minA + block_size; findA < blockA.end; findA += block_size)
                                        if (comp.compare(array[findA], array[minA]) < 0)
                                            minA = findA;
                                    BlockSwap(array, blockA.start, minA, block_size);

                                    T swap = array[blockA.start];
                                    array[blockA.start] = array[indexA];
                                    array[indexA] = swap;
                                    indexA++;

                                    if (lastA.length() <= cache_size)
                                        MergeExternal(array, lastA, new Range(lastA.end, B_split), comp);
                                    else if (buffer2.length() > 0)
                                        MergeInternal(array, lastA, new Range(lastA.end, B_split), comp, buffer2);
                                    else
                                        MergeInPlace(array, lastA, new Range(lastA.end, B_split), comp);

                                    if (buffer2.length() > 0 || block_size <= cache_size) {
                                        if (block_size <= cache_size)
                                            System.arraycopy(array, blockA.start, cache, 0, block_size);
                                        else
                                            BlockSwap(array, blockA.start, buffer2.start, block_size);

                                        BlockSwap(array, B_split, blockA.start + block_size - B_remaining, B_remaining);
                                    } else {
                                        Rotate(array, blockA.start - B_split, new Range(B_split, blockA.start + block_size), true);
                                    }

                                    lastA.set(blockA.start - B_remaining, blockA.start - B_remaining + block_size);
                                    lastB.set(lastA.end, lastA.end + B_remaining);

                                    blockA.start += block_size;
                                    if (blockA.length() == 0)
                                        break;

                                } else if (blockB.length() < block_size) {
                                    Rotate(array, -blockB.length(), new Range(blockA.start, blockB.end), false);

                                    lastB.set(blockA.start, blockA.start + blockB.length());
                                    blockA.start += blockB.length();
                                    blockA.end += blockB.length();
                                    blockB.end = blockB.start;
                                } else {
                                    BlockSwap(array, blockA.start, blockB.start, block_size);
                                    lastB.set(blockA.start, blockA.start + block_size);

                                    blockA.start += block_size;
                                    blockA.end += block_size;
                                    blockB.start += block_size;
                                    blockB.end += block_size;

                                    if (blockB.end > B.end)
                                        blockB.end = B.end;
                                }
                            }
                        }

                        if (lastA.length() <= cache_size)
                            MergeExternal(array, lastA, new Range(lastA.end, B.end), comp);
                        else if (buffer2.length() > 0)
                            MergeInternal(array, lastA, new Range(lastA.end, B.end), comp, buffer2);
                        else
                            MergeInPlace(array, lastA, new Range(lastA.end, B.end), comp);
                    }
                }

                InsertionSort(array, buffer2, comp);

                for (pull_index = 0; pull_index < 2; pull_index++) {
                    int unique = pull[pull_index].third() * 2;
                    if (pull[pull_index].fourth() > pull[pull_index].fifth()) {
                        Range buffer = new Range(pull[pull_index].first(), pull[pull_index].first() + pull[pull_index].third());
                        while (buffer.length() > 0) {
                            index = FindFirstForward(array, array[buffer.start], new Range(buffer.end, pull[pull_index].second()), comp, unique);
                            int amount = index - buffer.end;
                            Rotate(array, buffer.length(), new Range(buffer.start, index), true);
                            buffer.start += (amount + 1);
                            buffer.end += amount;
                            unique -= 2;
                        }
                    } else if (pull[pull_index].fourth() < pull[pull_index].fifth()) {
                        Range buffer = new Range(pull[pull_index].second() - pull[pull_index].third(), pull[pull_index].second());
                        while (buffer.length() > 0) {
                            index = FindLastBackward(array, array[buffer.end - 1], new Range(pull[pull_index].first(), buffer.start), comp, unique);
                            int amount = buffer.start - index;
                            Rotate(array, amount, new Range(index, buffer.end), true);
                            buffer.start -= amount;
                            buffer.end -= (amount + 1);
                            unique -= 2;
                        }
                    }
                }
            }

        } while (iterator.nextLevel());
    }
}