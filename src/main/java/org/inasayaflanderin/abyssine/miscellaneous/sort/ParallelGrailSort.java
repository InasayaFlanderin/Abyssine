package org.inasayaflanderin.abyssine.miscellaneous.sort;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.swap;

public class ParallelGrailSort<D> {
    ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private void grailCommonSort(D[] array, Comparator<D> comparator, int a, int b, int nKeys) {
        int len = b - a;

        if (len <= 16) {
            Sort.insertion(Arrays.asList(array), comparator, a, b);

            return;
        }

        int bLen = (int) Math.sqrt(len);
        int tLen = len / bLen;

        int idl = bLen + tLen;
        boolean strat1 = nKeys >= idl;
        if (!strat1) idl = nKeys;

        int keys = this.findKeys(array, comparator, a, b, idl);
        int a1 = a + keys;
        int m = (a1 + b) / 2;

        if (strat1 && keys == idl) {
            int finalM = m;
            List<Callable<Void>> tasks = List.of(
                    () -> {
                        this.grailCommonSort(array, comparator, a1, finalM, keys);

                        return null;
                    },
                    () -> {
                        this.grailCommonSort(array, comparator, finalM, b, keys);

                        return null;
                    }
            );

            try {
                executorService.invokeAll(tasks);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            this.blockMerge(array, comparator, a, a1, m, b, bLen);

            m = this.leftBinSearch(array, comparator, a + tLen, b - bLen, array[a + tLen - 1]);

            int finalBLen = bLen;
            int finalM1 = m;
            List<Callable<Void>> tasks1 = List.of(
                    () -> {
                        this.redistFW(array, comparator, a, a + tLen, finalM1);
                        return null;
                    },
                    () -> {
                        this.redistBW(array, comparator, finalM1, b - finalBLen, b);
                        return null;
                    }
            );

            try {
                executorService.invokeAll(tasks1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            m = (a1 + b) / 2;
            if (keys > 4) {
                bLen = (b - a1 - 1) / (keys - keys % 2) + 1;

                int finalM2 = m;
                List<Callable<Void>> tasks = List.of(
                        () -> {
                            this.grailCommonSort(array, comparator, a1, finalM2, keys);
                            return null;
                        },
                        () -> {
                            this.grailCommonSort(array, comparator, finalM2, b, keys);
                            return null;
                        }
                );

                try {
                    executorService.invokeAll(tasks);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                this.blockMergeFewKeys(array, comparator, a, a1, m, b, bLen);
                this.redistFW(array, comparator, a, a1, b);
            } else if (keys > 1) {
                this.lazyStableSort(array, comparator, a, b);
            }
        }
    }

    private void lazyStableSort(D[] array, Comparator<D> comparator, int a, int b) {
        if (b - a <= 16) {
            Sort.insertion(Arrays.asList(array), comparator, a, b);
            return;
        }

        int m = (a + b) / 2;

        List<Callable<Void>> tasks = List.of(
                () -> {
                    this.lazyStableSort(array, comparator, a, m);
                    return null;
                },
                () -> {
                    this.lazyStableSort(array, comparator, m, b);
                    return null;
                }
        );

        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.inPlaceMergeFW(array, comparator, a, m, b, true);
    }

    private void redistFW(D[] array, Comparator<D> comparator, int a, int m, int b) {
        Sort.insertion(Arrays.asList(array), comparator, a, m);
        this.inPlaceMergeFW(array, comparator, a, m, b, true);
    }
    private void redistBW(D[] array, Comparator<D> comparator, int a, int m, int b) {
        Sort.insertion(Arrays.asList(array), comparator, m, b);
        this.inPlaceMergeBW(array, comparator, a, m, b, false);
    }

    private void blockMergeFewKeys(D[] array, Comparator<D> comparator, int t, int a, int m, int b, int bLen) {
        int a1 = a + (m - a) % bLen;
        int b1 = b - (b - m) % bLen;
        int i = a1, l = i - bLen, r = m;

        D mKey = array[t + (m - i) / bLen];
        int f = a;
        boolean frag = true;

        this.blockSelect(array, comparator, a1, b1, t, bLen);

        while (l < m && r < b1) {
            boolean curr = comparator.compare(array[t++], mKey) < 0;

            if (frag != curr) {
                boolean tmp = frag;

                if (f == i || comparator.compare(array[i - 1], array[i + bLen - 1]) < (frag ? 1 : 0)) {
                    frag = curr;
                }

                f = this.inPlaceMergeFW(array, comparator, f, i, i + bLen, tmp);

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

        if (l < m) {
            this.inPlaceMergeBW(array, comparator, f, b1, b, true);
        }
    }

    private void blockMerge(D[] array, Comparator<D> comparator, int t, int a, int m, int b, int bLen) {
        int a1 = a + (m - a) % bLen;
        int b1 = b - (b - m) % bLen;
        int i = a1, l = i - bLen, r = m;

        D mKey = array[t + (m - i) / bLen];
        int f = a;
        boolean frag = true;

        this.blockSelect(array, comparator, a1, b1, t, bLen);

        while (l < m && r < b1) {
            boolean curr = comparator.compare(array[t++], mKey) < 0;

            if (frag != curr) {
                f = this.mergeFW(array, comparator, f - bLen, f, i, i + bLen, frag);

                if (f < i) {
                    this.shiftBW(array, f, i, i + bLen);
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
                this.shiftFW(array, f - bLen, f, i);
                f = i;

                if (frag) {
                    l += bLen;
                } else {
                    r += bLen;
                }
            }
            i += bLen;
        }

        if (l < m) {
            f = this.mergeFW(array, comparator, f - bLen, f, b1, b, true);
            if (f >= b1) {
                this.shiftFW(array, f - bLen, f, b);
            }
        } else {
            this.shiftFW(array, f - bLen, f, b);
        }
    }

    private void blockSelect(D[] array, Comparator<D> comparator, int a, int b, int t, int bLen) {
        for (int j = a; j < b; j += bLen) {
            int min = j;

            for (int i = min + bLen; i < b; i += bLen) {
                int cmp = comparator.compare(array[i], array[min]);

                if (cmp < 0 || (cmp == 0 && comparator.compare(array[t + (i - a) / bLen], array[t + (min - a) / bLen]) < 0)) {
                    min = i;
                }
            }

            if (min != j) {
                this.multiSwap(array, j, min, bLen);
                swap(array, t + (j - a) / bLen, t + (min - a) / bLen);
            }
        }
    }

    private int findKeys(D[] array, Comparator<D> comparator, int a, int b, int n) {
        int p = a, nKeys = 1, pEnd = a + nKeys;

        for (int i = pEnd; i < b && nKeys < n; i++) {
            int loc = this.leftBinSearch(array, comparator, p, pEnd, array[i]);

            if (pEnd == loc || comparator.compare(array[i], array[loc]) != 0) {
                this.rotate(array, p, pEnd, i);
                int inc = i - pEnd;
                loc += inc;
                p += inc;
                pEnd += inc;

                this.insertTo(array, pEnd, loc);
                nKeys++;
                pEnd++;
            }
        }

        this.rotate(array, a, p, pEnd);
        return nKeys;
    }

    private int inPlaceMergeFW(D[] array, Comparator<D> comparator, int a, int m, int b, boolean fwEq) {
        int i = a, j = m, k;

        while (i < j && j < b) {
            if (comparator.compare(array[i], array[j]) > (fwEq ? 0 : -1)) {
                k = fwEq ? this.leftBinSearch(array, comparator, j + 1, b, array[i])
                        : this.rightBinSearch(array, comparator, j + 1, b, array[i]);

                this.rotate(array, i, j, k);

                i += k - j;
                j = k;
            } else {
                i++;
            }
        }

        return i;
    }

    private void inPlaceMergeBW(D[] array, Comparator<D> comparator, int a, int m, int b, boolean fwEq) {
        int i = m - 1, j = b - 1, k;

        while (j > i && i >= a) {
            if (comparator.compare(array[i], array[j]) > (fwEq ? 0 : -1)) {
                k = fwEq ? this.rightBinSearch(array, comparator, a, i, array[j])
                        : this.leftBinSearch(array, comparator, a, i, array[j]);

                this.rotate(array, k, i + 1, j + 1);

                j -= (i + 1) - k;
                i = k - 1;
            } else {
                j--;
            }
        }
    }

    private int mergeFW(D[] array, Comparator<D> comparator, int p, int a, int m, int b, boolean fwEq) {
        int i = a, j = m;

        while (i < m && j < b) {
            if (comparator.compare(array[i], array[j]) < (fwEq ? 1 : 0)) {
                swap(array, p++, i++);
            } else {
                swap(array, p++, j++);
            }
        }

        int f = (i < m) ? i : j;
        if (i < m && p < i) {
            this.shiftFW(array, p, i, m);
        }

        return f;
    }

    private void insertTo(D[] array, int a, int b) {
        D temp = array[a];
        while (a > b) {
            array[a] = array[--a];
        }
        array[b] = temp;
    }

    private int leftBinSearch(D[] array, Comparator<D> comparator, int a, int b, D value) {
        while (a < b) {
            int m = a + (b - a) / 2;
            if (comparator.compare(value, array[m]) <= 0) {
                b = m;
            } else {
                a = m + 1;
            }
        }
        return a;
    }

    private int rightBinSearch(D[] array, Comparator<D> comparator, int a, int b, D value) {
        while (a < b) {
            int m = a + (b - a) / 2;
            if (comparator.compare(value, array[m]) < 0) {
                b = m;
            } else {
                a = m + 1;
            }
        }
        return a;
    }

    private void rotate(D[] array, int a, int m, int b) {
        int l = m - a, r = b - m;

        while (l > 0 && r > 0) {
            if (r < l) {
                this.multiSwap(array, m - r, m, r);
                b -= r;
                m -= r;
                l -= r;
            } else {
                this.multiSwap(array, a, m, l);
                a += l;
                m += l;
                r -= l;
            }
        }
    }

    private void shiftFW(D[] array, int a, int m, int b) {
        while (m < b) {
            swap(array, a++, m++);
        }
    }

    private void shiftBW(D[] array, int a, int m, int b) {
        while (m > a) {
            swap(array, --b, --m);
        }
    }

    private void multiSwap(D[] array, int a, int b, int len) {
        for (int i = 0; i < len; i++) {
            swap(array, a + i, b + i);
        }
    }

    public void runSort(D[] array, Comparator<D> comparator) {
        grailCommonSort(array, comparator, 0, array.length, Integer.MAX_VALUE);
    }
}