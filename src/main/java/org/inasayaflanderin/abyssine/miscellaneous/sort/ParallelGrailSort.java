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
        int len = end - start;

        if (len <= 16) {
            Sort.insertion(Arrays.asList(array), comparator, start, end);

            return;
        }

        int bLen = (int) Math.sqrt(len);
        int tLen = len / bLen;

        int idl = bLen + tLen;
        boolean strat1 = nKeys >= idl;
        if (!strat1) idl = nKeys;

        int keys = findKeys(array, comparator, start, end, idl);
        int start1 = start + keys;
        int mid = (start1 + end) / 2;

        if (strat1 && keys == idl) {
            int finalMid = mid;
            List<Callable<Void>> tasks = List.of(
                    () -> {
                        grailCommonSort(array, comparator, start1, finalMid, keys);

                        return null;
                    },
                    () -> {
                        grailCommonSort(array, comparator, finalMid, end, keys);

                        return null;
                    }
            );

            try {
                executorService.invokeAll(tasks);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            blockMerge(array, comparator, start, start1, mid, end, bLen);

            mid = leftBinSearch(array, comparator, start + tLen, end - bLen, array[start + tLen - 1]);

            int finalBLen = bLen;
            int finalM1 = mid;
            List<Callable<Void>> tasks1 = List.of(
                    () -> {
                        redistFW(array, comparator, start, start + tLen, finalM1);
                        return null;
                    },
                    () -> {
                        redistBW(array, comparator, finalM1, end - finalBLen, end);
                        return null;
                    }
            );

            try {
                executorService.invokeAll(tasks1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            mid = (start1 + end) / 2;
            if (keys > 4) {
                bLen = (end - start1 - 1) / (keys - keys % 2) + 1;

                int finalM2 = mid;
                List<Callable<Void>> tasks = List.of(
                        () -> {
                            grailCommonSort(array, comparator, start1, finalM2, keys);
                            return null;
                        },
                        () -> {
                            grailCommonSort(array, comparator, finalM2, end, keys);
                            return null;
                        }
                );

                try {
                    executorService.invokeAll(tasks);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                blockMergeFewKeys(array, comparator, start, start1, mid, end, bLen);
                redistFW(array, comparator, start, start1, end);
            } else if (keys > 1) {
                lazyStableSort(array, comparator, start, end);
            }
        }
    }

    private static <D> void lazyStableSort(D[] array, Comparator<D> comparator, int start, int end) {
        if (end - start <= 16) {
            Sort.insertion(Arrays.asList(array), comparator, start, end);
            return;
        }

        int mid = (start + end) / 2;

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
            throw new RuntimeException(e);
        }

        inPlaceMergeFW(array, comparator, start, mid, end, true);
    }

    private static <D> void redistFW(D[] array, Comparator<D> comparator, int start, int mid, int end) {
        Sort.insertion(Arrays.asList(array), comparator, start, mid);
        inPlaceMergeFW(array, comparator, start, mid, end, true);
    }
    private static <D> void redistBW(D[] array, Comparator<D> comparator, int start, int mid, int end) {
        Sort.insertion(Arrays.asList(array), comparator, mid, end);
        inPlaceMergeBW(array, comparator, start, mid, end, false);
    }

    private static <D> void blockMergeFewKeys(D[] array, Comparator<D> comparator, int t, int start, int mid, int end, int bLen) {
        int start1 = start + (mid - start) % bLen;
        int end1 = end - (end - mid) % bLen;
        int i = start1, l = i - bLen, r = mid;

        D mKey = array[t + (mid - i) / bLen];
        int f = start;
        boolean frag = true;

        blockSelect(array, comparator, start1, end1, t, bLen);

        while (l < mid && r < end1) {
            boolean curr = comparator.compare(array[t++], mKey) < 0;

            if (frag != curr) {
                boolean tmp = frag;

                if (f == i || comparator.compare(array[i - 1], array[i + bLen - 1]) < (frag ? 1 : 0)) {
                    frag = curr;
                }

                f = inPlaceMergeFW(array, comparator, f, i, i + bLen, tmp);

                if (frag) {
                    r += bLen;
                } else {
                    l += bLen;
                }
            } else {
                f = i;

                if (frag) {
                    l += bLen;
                } else {
                    r += bLen;
                }
            }
            i += bLen;
        }

        if (l < mid) {
            inPlaceMergeBW(array, comparator, f, end1, end, true);
        }
    }

    private static <D> void blockMerge(D[] array, Comparator<D> comparator, int t, int start, int mid, int end, int bLen) {
        int start1 = start + (mid - start) % bLen;
        int end1 = end - (end - mid) % bLen;
        int i = start1, l = i - bLen, r = mid;

        D mKey = array[t + (mid - i) / bLen];
        int f = start;
        boolean frag = true;

        blockSelect(array, comparator, start1, end1, t, bLen);

        while (l < mid && r < end1) {
            boolean curr = comparator.compare(array[t++], mKey) < 0;

            if (frag != curr) {
                f = mergeFW(array, comparator, f - bLen, f, i, i + bLen, frag);

                if (f < i) {
                    shiftBW(array, f, i, i + bLen);
                    f += bLen;
                } else {
                    frag = curr;
                }

                if (frag) {
                    r += bLen;
                } else {
                    l += bLen;
                }
            } else {
                shiftFW(array, f - bLen, f, i);
                f = i;

                if (frag) {
                    l += bLen;
                } else {
                    r += bLen;
                }
            }
            i += bLen;
        }

        if (l < mid) {
            f = mergeFW(array, comparator, f - bLen, f, end1, end, true);
            if (f >= end1) {
                shiftFW(array, f - bLen, f, end);
            }
        } else {
            shiftFW(array, f - bLen, f, end);
        }
    }

    private static <D> void blockSelect(D[] array, Comparator<D> comparator, int start, int end, int t, int bLen) {
        for (int j = start; j < end; j += bLen) {
            int min = j;

            for (int i = min + bLen; i < end; i += bLen) {
                int cmp = comparator.compare(array[i], array[min]);

                if (cmp < 0 || (cmp == 0 && comparator.compare(array[t + (i - start) / bLen], array[t + (min - start) / bLen]) < 0)) {
                    min = i;
                }
            }

            if (min != j) {
                multiSwap(array, j, min, bLen);
                swap(array, t + (j - start) / bLen, t + (min - start) / bLen);
            }
        }
    }

    private static <D> int findKeys(D[] array, Comparator<D> comparator, int start, int end, int n) {
        int p = start, nKeys = 1, pEnd = start + nKeys;

        for (int i = pEnd; i < end && nKeys < n; i++) {
            int loc = leftBinSearch(array, comparator, p, pEnd, array[i]);

            if (pEnd == loc || comparator.compare(array[i], array[loc]) != 0) {
                rotate(array, p, pEnd, i);
                int inc = i - pEnd;
                loc += inc;
                p += inc;
                pEnd += inc;

                insertTo(array, pEnd, loc);
                nKeys++;
                pEnd++;
            }
        }

        rotate(array, start, p, pEnd);
        return nKeys;
    }

    private static <D> int inPlaceMergeFW(D[] array, Comparator<D> comparator, int start, int mid, int end, boolean fwEq) {
        int i = start, j = mid, k;

        while (i < j && j < end) {
            if (comparator.compare(array[i], array[j]) > (fwEq ? 0 : -1)) {
                k = fwEq ? leftBinSearch(array, comparator, j + 1, end, array[i])
                        : rightBinSearch(array, comparator, j + 1, end, array[i]);

                rotate(array, i, j, k);

                i += k - j;
                j = k;
            } else {
                i++;
            }
        }

        return i;
    }

    private static <D> void inPlaceMergeBW(D[] array, Comparator<D> comparator, int start, int mid, int end, boolean fwEq) {
        int i = mid - 1, j = end - 1, k;

        while (j > i && i >= start) {
            if (comparator.compare(array[i], array[j]) > (fwEq ? 0 : -1)) {
                k = fwEq ? rightBinSearch(array, comparator, start, i, array[j])
                        : leftBinSearch(array, comparator, start, i, array[j]);

                rotate(array, k, i + 1, j + 1);

                j -= (i + 1) - k;
                i = k - 1;
            } else {
                j--;
            }
        }
    }

    private static <D> int mergeFW(D[] array, Comparator<D> comparator, int p, int start, int mid, int end, boolean fwEq) {
        int i = start, j = mid;

        while (i < mid && j < end) {
            if (comparator.compare(array[i], array[j]) < (fwEq ? 1 : 0)) {
                swap(array, p++, i++);
            } else {
                swap(array, p++, j++);
            }
        }

        int f = (i < mid) ? i : j;
        if (i < mid && p < i) {
            shiftFW(array, p, i, mid);
        }

        return f;
    }

    private static <D> void insertTo(D[] array, int start, int end) {
        D temp = array[start];
        while (start > end) {
            array[start] = array[--start];
        }
        array[end] = temp;
    }

    private static <D> int leftBinSearch(D[] array, Comparator<D> comparator, int start, int end, D value) {
        while (start < end) {
            int mid = start + (end - start) / 2;
            if (comparator.compare(value, array[mid]) <= 0) {
                end = mid;
            } else {
                start = mid + 1;
            }
        }
        return start;
    }

    private static <D> int rightBinSearch(D[] array, Comparator<D> comparator, int start, int end, D value) {
        while (start < end) {
            int mid = start + (end - start) / 2;
            if (comparator.compare(value, array[mid]) < 0) {
                end = mid;
            } else {
                start = mid + 1;
            }
        }
        return start;
    }

    private static <D> void rotate(D[] array, int start, int mid, int end) {
        int l = mid - start, r = end - mid;

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
        while (mid < end) {
            swap(array, start++, mid++);
        }
    }

    private static <D> void shiftBW(D[] array, int start, int mid, int end) {
        while (mid > start) {
            swap(array, --end, --mid);
        }
    }

    private static <D> void multiSwap(D[] array, int start, int end, int len) {
        for (int i = 0; i < len; i++) {
            swap(array, start + i, end + i);
        }
    }

    public static <D> void grail(D[] array, Comparator<D> comparator) {
        grailCommonSort(array, comparator, 0, array.length, Integer.MAX_VALUE);
    }
}