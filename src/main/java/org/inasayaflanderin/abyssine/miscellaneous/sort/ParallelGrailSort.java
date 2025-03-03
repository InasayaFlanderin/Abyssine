package org.inasayaflanderin.abyssine.miscellaneous.sort;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.swap;

public class ParallelGrailSort {
    private static final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private static <D> void grailCommonSort(D[] array, Comparator<D> comparator, int start, int end, int nKeys) {
        var len = end - start;

        if (len <= 16) {
            Sort.insertion(Arrays.asList(array), comparator, start, end);

            return;
        }

        var bLen = (int) Math.sqrt(len);
        var tLen = len / bLen;
        var idl = bLen + tLen;
        var strategy1 = nKeys >= idl;

        if (!strategy1) idl = nKeys;

        var p = start;
        var keys = 1;
        var pEnd = start + keys;

        for (int i = pEnd; i < end && keys < idl; i++) {
            var loc = leftBinSearch(array, comparator, p, pEnd, array[i]);

            if (pEnd == loc || comparator.compare(array[i], array[loc]) != 0) {
                rotate(array, p, pEnd, i);
                var inc = i - pEnd;
                loc += inc;
                p += inc;
                pEnd += inc;
                var move = pEnd;
                var temp = array[move];

                while(move > loc) array[move] = array[--move];

                array[loc] = temp;
                keys++;
                pEnd++;
            }
        }

        rotate(array, start, p, pEnd);
        var start1 = start + keys;
        var mid = (start1 + end) / 2;

        if (strategy1 && keys == idl) {
            var finalMid = mid;
            var keysFinal = keys;
            List<Callable<Void>> tasks = List.of(
                    () -> {
                        grailCommonSort(array, comparator, start1, finalMid, keysFinal);

                        return null;
                    },
                    () -> {
                        grailCommonSort(array, comparator, finalMid, end, keysFinal);

                        return null;
                    }
            );

            try {
                executorService.invokeAll(tasks);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            var t = start;
            var start2 = start1 + (mid - start1) % bLen;
            var end1 = end - (end - mid) % bLen;
            var i = start2;
            var l = i - bLen;
            var r = mid;
            var mKey = array[t + (mid - i) / bLen];
            var f = start1;
            var frag = true;
            blockSelect(array, comparator, start2, end1, t, bLen);

            while (l < mid && r < end1) {
                var curr = comparator.compare(array[t++], mKey) < 0;

                if (frag != curr) {
                    f = mergeFW(array, comparator, f - bLen, f, i, i + bLen, frag);

                    if (f < i) {
                        var midShift = i;
                        var endShift = i + bLen;

                        while(midShift > f) swap(array, --endShift, --midShift);

                        f += bLen;
                    } else frag = curr;

                    if (frag) r += bLen;
                    else l += bLen;
                } else {
                    shiftFW(array, f - bLen, f, i);
                    f = i;

                    if (frag) l += bLen;
                    else r += bLen;
                }

                i += bLen;
            }

            if (l < mid) {
                f = mergeFW(array, comparator, f - bLen, f, end1, end, true);

                if (f >= end1) shiftFW(array, f - bLen, f, end);
            } else shiftFW(array, f - bLen, f, end);

            mid = leftBinSearch(array, comparator, start + tLen, end - bLen, array[start + tLen - 1]);
            var finalBLen = bLen;
            var finalM1 = mid;
            var startFinal = start;
            List<Callable<Void>> tasks1 = List.of(
                    () -> {
                        redistributeFW(array, comparator, startFinal, startFinal + tLen, finalM1);

                        return null;
                    },
                    () -> {
                        Sort.insertion(Arrays.asList(array), comparator, end - finalBLen, end);
                        inPlaceMergeBW(array, comparator, finalM1, end - finalBLen, end, false);

                        return null;
                    }
            );

            try {
                executorService.invokeAll(tasks1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            mid = (start1 + end) / 2;

            if (keys > 4) {
                bLen = (end - start1 - 1) / (keys - keys % 2) + 1;
                var finalM2 = mid;
                var keysFinal = keys;
                List<Callable<Void>> tasks = List.of(
                        () -> {
                            grailCommonSort(array, comparator, start1, finalM2, keysFinal);
                            return null;
                        },
                        () -> {
                            grailCommonSort(array, comparator, finalM2, end, keysFinal);
                            return null;
                        }
                );

                try {
                    executorService.invokeAll(tasks);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                var start2 = start + (mid - start) % bLen;
                var end1 = end - (end - mid) % bLen;
                var i = start2;
                var l = i - bLen;
                var r = mid;
                var mKey = array[start + (mid - i) / bLen];
                var f = start;
                var frag = true;
                blockSelect(array, comparator, start2, end1, start, bLen);

                while (l < mid && r < end1) {
                    var curr = comparator.compare(array[start++], mKey) < 0;

                    if (frag != curr) {
                        var tmp = frag;

                        if (f == i || comparator.compare(array[i - 1], array[i + bLen - 1]) < (frag ? 1 : 0)) frag = curr;

                        f = inPlaceMergeFW(array, comparator, f, i, i + bLen, tmp);

                        if (frag) r += bLen;
                        else l += bLen;
                    } else {
                        f = i;

                        if (frag) l += bLen;
                        else r += bLen;
                    }

                    i += bLen;
                }

                if (l < mid) inPlaceMergeBW(array, comparator, f, end1, end, true);

                redistributeFW(array, comparator, start, start2, end);
            } else if (keys > 1) lazyStableSort(array, comparator, start, end);
        }
    }

    private static <D> void lazyStableSort(D[] array, Comparator<D> comparator, int start, int end) {
        if (end - start <= 16) {
            Sort.insertion(Arrays.asList(array), comparator, start, end);

            return;
        }

        var mid = (start + end) / 2;
        List<Callable<Void>> tasks = List.of(
                () -> {
                    lazyStableSort(array, comparator, start, mid);

                    return null;
                },
                () -> {
                    lazyStableSort(array, comparator, mid, end);

                    return null;
                }
        );

        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        inPlaceMergeFW(array, comparator, start, mid, end, true);
    }

    private static <D> void redistributeFW(D[] array, Comparator<D> comparator, int start, int mid, int end) {
        Sort.insertion(Arrays.asList(array), comparator, start, mid);
        inPlaceMergeFW(array, comparator, start, mid, end, true);
    }

    private static <D> void blockSelect(D[] array, Comparator<D> comparator, int start, int end, int t, int bLen) {
        for (int j = start; j < end; j += bLen) {
            var min = j;

            for (int i = min + bLen; i < end; i += bLen) {
                var cmp = comparator.compare(array[i], array[min]);

                if (cmp < 0 || (cmp == 0 && comparator.compare(array[t + (i - start) / bLen], array[t + (min - start) / bLen]) < 0)) min = i;
            }

            if (min != j) {
                multiSwap(array, j, min, bLen);
                swap(array, t + (j - start) / bLen, t + (min - start) / bLen);
            }
        }
    }

    private static <D> int inPlaceMergeFW(D[] array, Comparator<D> comparator, int start, int mid, int end, boolean fwEq) {
        var i = start;
        var j = mid;
        int k;

        while (i < j && j < end) {
            if (comparator.compare(array[i], array[j]) > (fwEq ? 0 : -1)) {
                k = fwEq ? leftBinSearch(array, comparator, j + 1, end, array[i]) : rightBinSearch(array, comparator, j + 1, end, array[i]);
                rotate(array, i, j, k);
                i += k - j;
                j = k;
            } else i++;
        }

        return i;
    }

    private static <D> void inPlaceMergeBW(D[] array, Comparator<D> comparator, int start, int mid, int end, boolean fwEq) {
        int i = mid - 1;
        var j = end - 1;
        int k;

        while (j > i && i >= start) {
            if (comparator.compare(array[i], array[j]) > (fwEq ? 0 : -1)) {
                k = fwEq ? rightBinSearch(array, comparator, start, i, array[j]) : leftBinSearch(array, comparator, start, i, array[j]);
                rotate(array, k, i + 1, j + 1);
                j -= (i + 1) - k;
                i = k - 1;
            } else j--;
        }
    }

    private static <D> int mergeFW(D[] array, Comparator<D> comparator, int p, int start, int mid, int end, boolean fwEq) {
        int i = start;
        var j = mid;

        while (i < mid && j < end) {
            if (comparator.compare(array[i], array[j]) < (fwEq ? 1 : 0)) swap(array, p++, i++);
            else swap(array, p++, j++);
        }

        var f = (i < mid) ? i : j;

        if (i < mid && p < i) shiftFW(array, p, i, mid);

        return f;
    }

    private static <D> int leftBinSearch(D[] array, Comparator<D> comparator, int start, int end, D value) {
        while (start < end) {
            var mid = (start + end) >>> 1;

            if (comparator.compare(value, array[mid]) <= 0) end = mid;
            else start = mid + 1;
        }

        return start;
    }

    private static <D> int rightBinSearch(D[] array, Comparator<D> comparator, int start, int end, D value) {
        while (start < end) {
            var mid = (start + end) >>> 1;

            if (comparator.compare(value, array[mid]) < 0) end = mid;
            else start = mid + 1;
        }

        return start;
    }

    private static <D> void rotate(D[] array, int start, int mid, int end) {
        var l = mid - start;
        var r = end - mid;

        while (l > 0 && r > 0) {
            if (r < l) {
                multiSwap(array, mid - r, mid, r);
                end -= r;
                mid -= r;
                l -= r;
            } else {
                multiSwap(array, start, mid, l);
                start += l;
                mid += l;
                r -= l;
            }
        }
    }

    private static <D> void shiftFW(D[] array, int start, int mid, int end) {
        while (mid < end) swap(array, start++, mid++);
    }

    private static <D> void multiSwap(D[] array, int start, int end, int len) {
        for (int i = 0; i < len; i++) swap(array, start + i, end + i);
    }

    public static <D> void grail(D[] array, Comparator<D> comparator) {
        grailCommonSort(array, comparator, 0, array.length, Integer.MAX_VALUE);
    }
}