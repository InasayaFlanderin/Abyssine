package org.inasayaflanderin.abyssine.miscellaneous.sort;

import java.util.Comparator;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.swap;

public class CircularGrailSort {
    public static <D> void grail(D[] data, Comparator<D> comparator) {
        if (data.length <= 16) {
            insertion(data, comparator, 0, data.length);

            return;
        }

        var bLen = (long) Math.ceil(Math.sqrt(data.length));
        bLen--;
        bLen |= bLen >> 1;
        bLen |= bLen >> 2;
        bLen |= bLen >> 4;
        bLen |= bLen >> 8;
        bLen |= bLen >> 16;
        bLen++;

        long i = bLen;
        long j = 1;
        long len = data.length - i;
        long b = data.length;

        while (j <= bLen) {
            while (i + 2 * j < b) {
                merge(data, comparator, i - j, i, i + j, i + 2 * j, true);
                i += 2 * j;
            }

            if (i + j < b) merge(data, comparator, i - j, i, i + j, b, true);
            else shiftFW(data, i - j, i, b);

            i = b + bLen - j;
            b = i + len;
            j *= 2;
        }

        while (j < len) {
            while (i + 2 * j < b) {
                blockMerge(data, comparator, i, i + j, i + 2 * j, bLen);
                i += 2 * j;
            }

            if (i + j < b) blockMerge(data, comparator, i, i + j, b, bLen);
            else shiftFW(data, i - bLen, i, b);

            i = b;
            b += len;
            j *= 2;
        }

        insertion(data, comparator, i - bLen, i);
        inPlaceMerge(data, comparator, i - bLen, i, b);

        rotate(data, 0, (i - bLen) % data.length, data.length);
    }

    private static <D> void blockMerge(D[] data, Comparator<D> comparator, long a, long m, long b, long bLen) {
        var b1 = b - (b - m - 1) % bLen - 1;

        if (b1 > m) {
            var b2 = b1;

            for (long i = m - bLen; i > a && blockLessThan(data, comparator, b1, i, bLen); i -= bLen, b2 -= bLen);
            for (long j = a; j < b1 - bLen; j += bLen) {
                var min = j;

                for (long i = min + bLen; i < b1; i += bLen) if (blockLessThan(data, comparator, i, min, bLen)) min = i;

                if (min != j) multiSwap(data, j, min, bLen);
            }

            var f = a;

            for (long i = a + bLen; i < b2; i += bLen) {
                f = merge(data, comparator, f - bLen, f, i, i + bLen, false);

                if (f < i) {
                    shiftBW(data, f, i, i + bLen);
                    f += bLen;
                }
            }

            merge(data, comparator, f - bLen, f, b1, b, true);
        } else merge(data, comparator, a - bLen, a, m, b, true);
    }

    private static <D> long merge(D[] data, Comparator<D> comparator, long p, long a, long m, long b, boolean full) {
        var i = a;
        var j = m;

        while (i < m && j < b) {
            if (comparator.compare(data[(int) (i % data.length)], data[(int) (j % data.length)]) <= 0) swap(data, (int) (p++ % data.length), (int) (i++ % data.length));
            else swap(data, (int) (p++ % data.length), (int) (j++ % data.length));
        }

        if (i < m) {
            if (i > p) shiftFW(data, p, i, m);
        } else if (full) shiftFW(data, p, j, b);

        return i < m ? i : j;
    }

    private static <D> boolean blockLessThan(D[] data, Comparator<D> comparator, long a, long b, long bLen) {
        long cmp = comparator.compare(data[(int) (a % data.length)], data[(int) (b % data.length)]);

        return cmp < 0 || (cmp == 0 && comparator.compare(data[(int) ((a + bLen - 1) % data.length)], data[(int) ((b + bLen - 1) % data.length)]) < 0);
    }

    private static <D> void rotate(D[] data, long a, long m, long b) {
        long l = m - a, r = b - m;

        while (l > 0 && r > 0) {
            if (r < l) {
                multiSwap(data, m - r, m, r);
                b -= r;
                m -= r;
                l -= r;
            } else {
                multiSwap(data, a, m, l);
                a += l;
                m += l;
                r -= l;
            }
        }
    }

    private static <D> void inPlaceMerge(D[] data, Comparator<D> comparator, long a, long m, long b) {
        var i = a;
        var j = m;
        long k;

        while (i < j && j < b) {
            if (comparator.compare(data[(int) (i % data.length)], data[(int) (j % data.length)]) > 0) {
                k = j;

                while (++k < b && comparator.compare(data[(int) (i % data.length)], data[(int) (k % data.length)]) > 0);

                rotate(data, i, j, k);
                i += k - j;
                j = k;
            } else i++;
        }
    }

    private static <D> void shiftFW(D[] data, long a, long m, long b) {
        while (m < b) swap(data, (int) (a++ % data.length), (int) (m++ % data.length));
    }

    private static <D> void shiftBW(D[] data, long a, long m, long b) {
        while (m > a) swap(data, (int) (--b % data.length), (int) (--m % data.length));
    }

    private static <D> void insertion(D[] data, Comparator<D> comparator, long a, long b) {
        for (long i = a + 1; i < b; i++) while (i > a && comparator.compare(data[(int) ((i - 1) % data.length)], data[(int) (i % data.length)]) > 0) swap(data, (int) (i % data.length), (int) (--i % data.length));
    }

    private static <D> void multiSwap(D[] data, long start, long secondStart, long len) {
        for (long i = 0; i < len; i++) swap(data, (int) ((start + i) % data.length), (int) ((secondStart + i) % data.length));
    }
}