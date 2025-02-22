package org.inasayaflanderin.abyssine.miscellaneous.sort;

import lombok.extern.slf4j.Slf4j;
import org.inasayaflanderin.abyssine.exception.ParallelExecutionException;
import org.inasayaflanderin.abyssine.miscellaneous.BinarySearchTree;
import org.inasayaflanderin.abyssine.miscellaneous.CartesianTree;
import org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils;
import org.inasayaflanderin.abyssine.primitives.Quin;

import java.util.Comparator;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils.*;

@Slf4j
public class Sort {
    private static final ExecutorService executors = Executors.newVirtualThreadPerTaskExecutor();

    public static <D> void selection(D[] data, Comparator<D> comparator) {
        selection(Arrays.asList(data), comparator);
    }

    public static <D> void doubleSelection(D[] data, Comparator<D> comparator) {
        doubleSelection(Arrays.asList(data), comparator);
    }

    public static <D> void insertion(D[] data, Comparator<D> comparator) {
        insertion(Arrays.asList(data), comparator);
    }

    public static <D> void binaryInsertion(D[] data, Comparator<D> comparator) {
        binaryInsertion(Arrays.asList(data), comparator);
    }

    public static <D> void bubble(D[] data, Comparator<D> comparator) {
        bubble(Arrays.asList(data), comparator);
    }

    public static <D> void shaker(D[] data, Comparator<D> comparator) {
        shaker(Arrays.asList(data), comparator);
    }

    public static <D> void quick(D[] data, Comparator<D> comparator) {
        quick(Arrays.asList(data), comparator);
    }

    public static <D> void merge(D[] data, Comparator<D> comparator) {
        merge(Arrays.asList(data), comparator);
    }

    public static <D> void heap(D[] data, Comparator<D> comparator) {
        heap(Arrays.asList(data), comparator);
    }

    public static <D> void comb(D[] data, Comparator<D> comparator) {
        comb(Arrays.asList(data), comparator);
    }

    public static <D> void shell(D[] data, Comparator<D> comparator) {
        shell(Arrays.asList(data), comparator);
    }

    public static <D> void cycle(D[] data, Comparator<D> comparator) {
        cycle(Arrays.asList(data), comparator);
    }

    public static <D> void patience(D[] data, Comparator<D> comparator) {
        patience(Arrays.asList(data), comparator);
    }

    public static <D> void exchange(D[] data, Comparator<D> comparator) {
        exchange(Arrays.asList(data), comparator);
    }

    public static <D> void oddEven(D[] data, Comparator<D> comparator) {
        oddEven(Arrays.asList(data), comparator);
    }

    public static <D> void circle(D[] data, Comparator<D> comparator) {
        circle(Arrays.asList(data), comparator);
    }

    public static <D> void mergeInsertion(D[] data, Comparator<D> comparator) {
        mergeInsertion(Arrays.asList(data), comparator);
    }

    public static <D> void mergeInsertion2(D[] data, Comparator<D> comparator) {
        mergeInsertion2(Arrays.asList(data), comparator);
    }

    public static <D> void tournament(D[] data, Comparator<D> comparator) {
        tournament(Arrays.asList(data), comparator);
    }

    public static <D> void tree(D[] data, Comparator<D> comparator) {
        tree(Arrays.asList(data), comparator);
    }

    public static <D> void gnome(D[] data, Comparator<D> comparator) {
        gnome(Arrays.asList(data), comparator);
    }

    public static <D> void strand(D[] data, Comparator<D> comparator) {
        strand(Arrays.asList(data), comparator);
    }

    public static <D> void bitonicNetwork(D[] data, Comparator<D> comparator) {
        bitonicNetwork(Arrays.asList(data), comparator);
    }

    public static <D> void oddEvenNetwork(D[] data, Comparator<D> comparator) {
        oddEvenNetwork(Arrays.asList(data), comparator);
    }

    public static <D> void quickLL(D[] data, Comparator<D> comparator) {
        quickLL(Arrays.asList(data), comparator);
    }

    public static <D> void dualPivotQuick(D[] data, Comparator<D> comparator) {
        dualPivotQuick(Arrays.asList(data), comparator);
    }

    public static <D> void intro(D[] data, Comparator<D> comparator) {
        intro(Arrays.asList(data), comparator);
    }

    public static <D> void tim(D[] data, Comparator<D> comparator) {
        tim(Arrays.asList(data), comparator);
    }

    public static <D> void weave(D[] data, Comparator<D> comparator) {
        weave(Arrays.asList(data), comparator);
    }

    public static <D> void smooth(D[] data, Comparator<D> comparator) {
        smooth(Arrays.asList(data), comparator);
    }

    public static <D> void cartesian(D[] data, Comparator<D> comparator) {
        cartesian(Arrays.asList(data), comparator);
    }

    public static <D> void sqrt(D[] data, Comparator<D> comparator) {
        sqrt(Arrays.asList(data), comparator);
    }

    public static <D> void wiki(D[] data, Comparator<D> comparator) {
        wiki(Arrays.asList(data), comparator);
    }

    public static <D> void selection(List<D> data, Comparator<D> comparator) {
        for (int i = 0; i < data.size() - 1; i++) {
            var swap = i;

            for (int j = i + 1; j < data.size(); j++) if (comparator.compare(data.get(j), data.get(swap)) < 0) swap = j;

            if (swap != i) swap(data, i, swap);
        }
    }

    public static <D> void doubleSelection(List<D> data, Comparator<D> comparator) {
        var requiredSearch = data.size() / 2;

        for (int i = 0; i < requiredSearch; i++) {
            var iFinal = i;

            try {
                List<Callable<Integer>> task = List.of(
                        () -> {
                            var minIndex = iFinal;

                            for (int j = iFinal; j < data.size() - iFinal; j++) if (comparator.compare(data.get(j), data.get(minIndex)) < 0) minIndex = j;

                            return minIndex;
                        },

                        () -> {
                            var maxIndex = iFinal;

                            for(int j = iFinal; j < data.size() - iFinal; j++) if (comparator.compare(data.get(j), data.get(maxIndex)) > 0) maxIndex = j;

                            return maxIndex;
                        }
                );

                var result = executors.invokeAll(task);
                var min = result.getFirst().get();
                var max = result.getLast().get();

                swap(data, i, min);

                if(max == i) max = min;

                swap(data, data.size() - i - 1, max);
            } catch (InterruptedException e) {
                log.error("Thread got interrupted!");

                throw new ParallelExecutionException(e.getMessage());
            } catch (ExecutionException e) {
                log.error("There was something wrong with finding swapped index");

                throw new ParallelExecutionException(e.getMessage());
            }
        }
    }

    public static <D> void insertion(List<D> data, Comparator<D> comparator) {
        for(int i = 1; i < data.size(); i++) {
            var j = i;

            while(j > 0 && comparator.compare(data.get(j - 1), data.get(j)) > 0) {
                swap(data, j, j - 1);
                j--;
            }
        }
    }

    public static <D> void binaryInsertion(List<D> data, Comparator<D> comparator) {
        insertion(data, comparator, 0, data.size());
    }

    public static <D> void bubble(List<D> data, Comparator<D> comparator) {
        var length = data.size();

        while (length > 1) {
            int newLength = 0;

            for (int i = 1; i < length; i++) {
                if (comparator.compare(data.get(i - 1), data.get(i)) > 0) {
                    swap(data, i - 1, i);
                    newLength = i;
                }
            }

            length = newLength;
        }
    }

    public static <D> void shaker(List<D> data, Comparator<D> comparator) {
        var left = 0;
        var right = data.size() - 1;

        while (left < right) {
            int newRight = 0;
            for (int i = left; i < right; i++) {
                if (comparator.compare(data.get(i), data.get(i + 1)) > 0) {
                    swap(data, i, i + 1);
                    newRight = i;
                }
            }

            right = newRight;

            var newLeft = right;
            for (int i = right; i > left; i--) {
                if (comparator.compare(data.get(i - 1), data.get(i)) > 0) {
                    swap(data, i - 1, i);
                    newLeft = i;
                }
            }

            left = newLeft;
        }
    }

    public static <D> void quick(List<D> data, Comparator<D> comparator) {
        quick(data, comparator, 0, data.size() - 1);
    }

    public static <D> void merge(List<D> data, Comparator<D> comparator) {
        merge(data, comparator, 0, data.size() - 1);
    }

    public static <D> void heap(List<D> data, Comparator<D> comparator) {
        for (int i = data.size() / 2 - 1; i >= 0; i--) heapify(data, comparator, data.size(), i);

        for (int i = data.size() - 1; i > 0; i--) {
            swap(data, 0, i);
            heapify(data, comparator, i, 0);
        }
    }

    public static <D> void comb(List<D> data, Comparator<D> comparator) {
        var gap = data.size();
        var swapped = true;

        while(gap != 1 || swapped) {
            gap = Math.max(1, gap * 10 / 13);
            swapped = false;

            for(int i = 0; i < data.size() - gap; i++) if(comparator.compare(data.get(i), data.get(i + gap)) > 0) {
                swap(data, i, i + gap);
                swapped = true;
            }
        }
    }

    public static <D> void shell(List<D> data, Comparator<D> comparator) {
        for(int i = data.size() / 2; i > 0; i /= 2) {
            for(int j = i; j < data.size(); j++) {
                var temp = data.get(j);
                int k;

                for(k = j; k >= i && comparator.compare(data.get(k - i), temp) > 0; k -= i) data.set(k, data.get(k - i));

                data.set(k, temp);
            }
        }
    }

    public static <D> void cycle(List<D> data, Comparator<D> comparator) {
        for (int cycleStart = 0; cycleStart < data.size() - 1; cycleStart++) {
            D item = data.get(cycleStart);
            int pos = cycleStart;

            var itemFinal = item;
            pos += (int) IntStream.range(cycleStart + 1, data.size())
                    .filter(i -> comparator.compare(data.get(i), itemFinal) < 0)
                    .count();

            if (pos == cycleStart) continue;

            while (item.equals(data.get(pos))) pos++;

            if (pos != cycleStart) {
                D temp = item;
                item = data.get(pos);
                data.set(pos, temp);
            }

            while (pos != cycleStart) {
                pos = cycleStart;

                var itemFinal2 = item;

                pos += (int) IntStream.range(cycleStart + 1, data.size())
                        .filter(i -> comparator.compare(data.get(i), itemFinal2) < 0)
                        .count();

                while (item.equals(data.get(pos))) pos++;

                if (!item.equals(data.get(pos))) {
                    D temp = item;
                    item = data.get(pos);
                    data.set(pos, temp);
                }
            }
        }
    }

    public static <D> void patience(List<D> data, Comparator<D> comparator) {
        List<Stack<D>> piles = new ArrayList<>();

        data.forEach(datum -> piles.stream()
                .filter(p -> comparator.compare(datum, p.peek()) <= 0)
                .findFirst().ifPresentOrElse(
                        p -> p.push(datum),
                        () -> {
                            Stack<D> newPile = new Stack<>();
                            newPile.push(datum);
                            piles.add(newPile);
                        }
                ));

        PriorityQueue<Stack<D>> minHeap = new PriorityQueue<>(Comparator.comparing(Stack::peek, comparator));
        minHeap.addAll(piles);

        List<D> sorted = IntStream.range(0, data.size()).mapToObj(i -> {
            Stack<D> smallestPile = minHeap.poll();

            assert smallestPile != null;

            D smallest = smallestPile.pop();

            if (!smallestPile.isEmpty()) {
                minHeap.add(smallestPile);
            }

            return smallest;
        }).toList();

        for (int i = 0; i < data.size(); i++) {
            data.set(i, sorted.get(i));
        }
    }

    public static <D> void exchange(List<D> data, Comparator<D> comparator) {
        for (int i = 0; i < data.size() - 1; i++) for(int j = i + 1; j < data.size(); j++) if (comparator.compare(data.get(i), data.get(j)) > 0) swap(data, i, j);
    }

    public static <D> void oddEven(List<D> data, Comparator<D> comparator) {
        while(!isSort(data, comparator)) {
            List<Callable<Void>> tasks = List.of(
                    () -> {
                        for (int i = 1; i < data.size() - 1; i += 2) if (comparator.compare(data.get(i), data.get(i + 1)) > 0) swap(data, i, i + 1);

                        return null;
                    },

                    () -> {
                        for (int i = 0; i < data.size() - 1; i += 2) if (comparator.compare(data.get(i), data.get(i + 1)) > 0) swap(data, i, i + 1);

                        return null;
                    }
            );

            try {
                executors.invokeAll(tasks);
            } catch (InterruptedException e) {
                log.error("Thread got interrupted!");

                throw new ParallelExecutionException(e.getMessage());
            }
        }
    }

    public static <D> void circle(List<D> data, Comparator<D> comparator) {
        boolean swapped;

        do {
            swapped = circle(data, comparator, 0, data.size() - 1);
        } while(swapped);
    }

    public static <D> void mergeInsertion(List<D> data, Comparator<D> comparator) {
        mergeInsertion(data, comparator, data.size() - 1);
    }

    public static <D> void mergeInsertion2(List<D> data, Comparator<D> comparator) {
        if(data.size() <= 16) {
            binaryInsertion(data, comparator);

            return;
        }

        merge(data, comparator);
    }

    public static <D> void tournament(List<D> data, Comparator<D> comparator) {
        List<D> temporary = new ArrayList<>(data);

        for (int i = 0; i < data.size(); i++) {
            D winner = tournamentExecute(temporary, comparator);

            data.set(i, winner);
            temporary.remove(winner);
        }
    }

    public static <D> void tree(List<D> data, Comparator<D> comparator) {
        BinarySearchTree<D> tree = new BinarySearchTree<>(comparator);

        data.forEach(tree::insert);
        tree.inorderTransverse(data);
    }

    public static <D> void gnome(List<D> data, Comparator<D> comparator) {
        var i = 0;

        while(i < data.size()) {
            if (i == 0 || comparator.compare(data.get(i), data.get(i - 1)) >= 0) i++;
            else {
                swap(data, i, i - 1);
                i--;
            }
        }
    }

    public static <D> void strand(List<D> data, Comparator<D> comparator) {
        List<D> result = strandExecute(data, comparator);

        for (int i = 0; i < result.size(); i++) data.set(i, result.get(i));
    }

    public static <D> void bitonicNetwork(List<D> data, Comparator<D> comparator) {
        bitonicNetworkExecute(data, comparator, 0, data.size());
    }

    public static <D> void oddEvenNetwork(List<D> data, Comparator<D> comparator) {
        oddEvenNetworkExecute(data, comparator, 0, data.size());
    }

    public static <D> void quickLL(List<D> data, Comparator<D> comparator) {
        quickLL(data, comparator, 0, data.size() - 1);
    }

    public static <D> void dualPivotQuick(List<D> data, Comparator<D> comparator) {
        dualPivotQuick(data, comparator, 0, data.size() - 1);
    }

    public static <D> void intro(List<D> data, Comparator<D> comparator) {
        intro(data, comparator, 0, data.size() - 1, 2 * (int) Math.floor(Math.log(data.size()) / Math.log(2)));
    }

    public static <D> void tim(List<D> data, Comparator<D> comparator) {
        var r = 0;
        var n = data.size();

        while(n >= 32) {
            r |= n & 1;
            n >>= 1;
        }

        var minRun = n + r;

        for (int i = 0; i < data.size(); i += minRun) insertion(data, comparator, i, Math.min(i + 32, data.size()));
        for(int i = minRun; i < data.size(); i *= 2) {
            for (int j = 0; j < data.size(); j += 2 * i) {
                var mid = j + i - 1;
                var right = Math.min(j + 2 * i - 1, data.size() - 1);

                if(mid < right) {
                    List<D> leftCopy = new ArrayList<>(data.subList(j, mid + 1));
                    List<D> rightCopy = new ArrayList<>(data.subList(mid + 1, right + 1));

                    var k = 0;
                    var l = 0;
                    var m = j;

                    while(k < leftCopy.size() && l < rightCopy.size()) {
                        if(comparator.compare(leftCopy.get(k), rightCopy.get(l)) < 0) data.set(m++, leftCopy.get(k++));
                        else data.set(m++, rightCopy.get(l++));
                    }

                    while(k < leftCopy.size()) data.set(m++, leftCopy.get(k++));
                    while(l < rightCopy.size()) data.set(m++, rightCopy.get(l++));
                }
            }
        }
    }

    public static <D> void weave(List<D> data, Comparator<D> comparator) {
        weave(data, comparator, 0, data.size());
    }

    public static <D> void smooth(List<D> data, Comparator<D> comparator) {
        var p = data.size() - 1;
        var q = p;
        var r = 0;

        while (p > 0) {
            if ((r & 0x03) == 0) {
                heapifySmooth(data, comparator, r, q);
            }

            if(leonardo(r) != p) {
                r = r - 1;
                q = q - leonardo(r);
                heapifySmooth(data, comparator, r, q);
                q = r - 1;
            }

            r = r + 1;

            swap(data, 0, p);
            p = p - 1;
        }

        for (int i = 0; i < data.size() - 1; i++) for (int j = i + 1; j > 0 && comparator.compare(data.get(j), data.get(j - 1)) < 0; j--) swap(data, j, j - 1);
    }

    public static <D> void cartesian(List<D> data, Comparator<D> comparator) {
        CartesianTree<D> tree = new CartesianTree<>(data, comparator);
        tree.pQBasedTraversal(data);
    }

    public static <D> void sqrt(List<D> data, Comparator<D> comparator) {
        var r = (int) Math.sqrt(data.size());

        if (r == 0) return;

        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < data.size(); i += r) {
            var iFinal = i;

            tasks.add(() -> {
                tim(data.subList(iFinal, Math.min(iFinal + r, data.size())), comparator);

                return null;
            });
        }

        try {
            executors.invokeAll(tasks);
        } catch (InterruptedException e) {
            log.error("Thread got interrupted!");

            throw new ParallelExecutionException(e.getMessage());
        }

        for(int size = r; size < data.size(); size *= 2) {
            for(int left = 0; left < data.size() - size; left += 2 * size) {
                var mid = left + size - 1;
                var right = Math.min(left + 2 * size - 1, data.size() - 1);
                var i = left;
                var j = mid + 1;

                while(i <= mid && j <= right) {
                    if(comparator.compare(data.get(i), data.get(j)) > 0) {
                        D temp = data.get(j);

                        for(int k = j; k > i; k--) data.set(k, data.get(k - 1));

                        data.set(i, temp);
                        mid++;
                        j++;
                    }

                    i++;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <D> void wiki(List<D> data, Comparator<D> comparator) {
        D[] cache = (D[]) new Object[512];

        if (data.size() < 4) {
            binaryInsertion(data, comparator);

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
        List<Callable<Void>> tasks = new ArrayList<>();

        while(decimal < data.size()) {
            int[] order = { 0, 1, 2, 3, 4, 5, 6, 7 };
            var rangeStart = decimal;
            decimal += decimalStep;
            numerator += numeratorStep;

            if (numerator >= denominator) {
                numerator -= denominator;
                decimal++;
            }

            var rangeEnd = decimal;

            tasks.add(() -> {
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

                return null;
            });
        }

        try {
            executors.invokeAll(tasks);
        } catch (InterruptedException e) {
            log.error("Thread got interrupted!");

            throw new ParallelExecutionException(e.getMessage());
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

                insertion(data, comparator, buffer2Start, buffer2End);

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

    private static <D> void quick(List<D> data, Comparator<D> comparator, int left, int right) {
        if (left < right) {
            final var pivotFinal = partitionQuick(data, comparator, left, right);

            List<Callable<Void>> tasks = List.of(
                    () -> {
                        quick(data, comparator, left, pivotFinal - 1);

                        return null;
                    },
                    () -> {
                        quick(data, comparator, pivotFinal + 1, right);

                        return null;
                    }
            );

            try {
                executors.invokeAll(tasks);
            } catch (InterruptedException e) {
                log.error("Thread got interrupted!");

                throw new ParallelExecutionException(e.getMessage());
            }
        }
    }

    private static <D> void merge(List<D> data, Comparator<D> comparator, int left, int right) {
        if(left < right) {
            var middle = (left + right) / 2;
            List<Callable<Void>> tasks = List.of(
                    () -> {
                        merge(data, comparator, left, middle);

                        return null;
                    },
                    () -> {
                        merge(data, comparator, middle + 1, right);

                        return null;
                    }
            );

            try {
                executors.invokeAll(tasks);
            } catch (InterruptedException e) {
                log.error("Thread got interrupted!");

                throw new ParallelExecutionException(e.getMessage());
            }
            List<D> leftCopy = new ArrayList<>(data.subList(left, middle + 1));
            List<D> rightCopy = new ArrayList<>(data.subList(middle + 1, right + 1));

            merge(data, leftCopy, rightCopy, comparator, left);
        }
    }

    private static <D> void heapify(List<D> data, Comparator<D> comparator, int length, int index) {
        int largest = index;
        int left = 2 * index + 1;
        int right = 2 * index + 2;

        if (left < length && comparator.compare(data.get(left), data.get(largest)) > 0) largest = left;
        if (right < length && comparator.compare(data.get(right), data.get(largest)) > 0) largest = right;
        if (largest != index) {
            swap(data, index, largest);
            heapify(data, comparator, length, largest);
        }
    }

    private static <D> boolean circle(List<D> list, Comparator<D> comparator, int left, int right) {
        var swapped = false;

        if(left == right) return false;

        var i = left;
        var j = right;

        while(i < j) {
            if(comparator.compare(list.get(i), list.get(j)) > 0) {
                swap(list, i, j);
                swapped = true;
            }

            i++;
            j--;
        }

        if(i == j && comparator.compare(list.get(i), list.get(i + 1)) > 0) {
            swap(list, i, i + 1);
            swapped = true;
        }

        int mid = (left + right) / 2;

        List<Callable<Boolean>> tasks = List.of(
                () -> circle(list, comparator, left, mid),
                () -> circle(list, comparator, mid + 1, right)
        );

        try {
            List<Future<Boolean>> result = executors.invokeAll(tasks);
            swapped = swapped || result.getFirst().get() || result.getLast().get();
        } catch (InterruptedException e) {
            log.error("Thread got interrupted!");

            throw new ParallelExecutionException(e.getMessage());
        } catch (ExecutionException e) {
            log.error("There was something wrong sub sorting circle");

            throw new ParallelExecutionException(e.getMessage());
        }

        return swapped;
    }

    private static <D> void mergeInsertion(List<D> data, Comparator<D> comparator, int right) {
        if(data.size() < 2) return;

        var mid = right / 2;

        List<D> leftCopy = new ArrayList<>(data.subList(0, mid + 1));
        List<D> rightCopy = new ArrayList<>(data.subList(mid + 1, right + 1));
        List<Callable<Void>> tasks = List.of(
                () -> {
                    mergeInsertion(leftCopy, comparator, leftCopy.size() - 1);

                    return null;
                },
                () -> {
                    mergeInsertion(rightCopy, comparator, rightCopy.size() - 1);

                    return null;
                }
        );

        try {
            executors.invokeAll(tasks);
        } catch (InterruptedException e) {
            log.error("Thread got interrupted!");

            throw new ParallelExecutionException(e.getMessage());
        }

        merge(data, leftCopy, rightCopy, comparator, 0);
    }

    private static <D> void merge(List<D> data, List<D> leftCopy, List<D> rightCopy, Comparator<D> comparator, int left) {
        var dataIndex = left;

        while(!leftCopy.isEmpty() && !rightCopy.isEmpty()) {
            if(comparator.compare(leftCopy.getFirst(), rightCopy.getFirst()) < 0) data.set(dataIndex, leftCopy.removeFirst());
            else data.set(dataIndex, rightCopy.removeFirst());

            dataIndex++;
        }

        while(!leftCopy.isEmpty()) data.set(dataIndex++, leftCopy.removeFirst());
        while(!rightCopy.isEmpty()) data.set(dataIndex++, rightCopy.removeFirst());
    }

    private static <D> D tournamentExecute(List<D> data, Comparator<D> comparator) {
        if(data.size() < 2) return data.getFirst();

        List<D> competition = new ArrayList<>();

        for (int i = 0; i < data.size(); i += 2) {
            if(i + 1 < data.size()) {
                if(comparator.compare(data.get(i), data.get(i + 1)) < 0) competition.add(data.get(i));
                else competition.add(data.get(i + 1));
            } else competition.add(data.get(i));
        }

        return tournamentExecute(competition, comparator);
    }

    private static <D> List<D> strandExecute(List<D> data, Comparator<D> comparator) {
        if (data.size() < 2) return data;

        List<D> leftCopy = new ArrayList<>();
        List<D> rightCopy = new ArrayList<>(data);

        leftCopy.add(rightCopy.removeFirst());

        Iterator<D> iterator = rightCopy.iterator();
        while (iterator.hasNext()) {
            D item = iterator.next();

            if (comparator.compare(item, leftCopy.getLast()) >= 0) {
                leftCopy.add(item);
                iterator.remove();
            }
        }

        rightCopy = strandExecute(rightCopy, comparator);
        List<D> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < leftCopy.size() && j < rightCopy.size()) {
            if (comparator.compare(leftCopy.get(i), rightCopy.get(j)) <= 0) result.add(leftCopy.get(i++));
            else result.add(rightCopy.get(j++));
        }
        while (i < leftCopy.size()) result.add(leftCopy.get(i++));
        while (j < rightCopy.size()) result.add(rightCopy.get(j++));

        return result;
    }

    private static <D> void bitonicNetworkExecute(List<D> data, Comparator<D> comparator, int left, int length) {
        if(length > 1) {
            var mid = length / 2;
            List<Callable<Void>> tasks = List.of(
                    () -> {
                        bitonicNetworkExecute(data, comparator.reversed(), left, mid);

                        return null;
                    },
                    () -> {
                        bitonicNetworkExecute(data, comparator, left + mid, length - mid);

                        return null;
                    }
            );

            try {
                executors.invokeAll(tasks);
            } catch (InterruptedException e) {
                log.error("Thread got interrupted!");

                throw new ParallelExecutionException(e.getMessage());
            }

            bitonicMerge(data, comparator, left, length);
        }
    }

    private static <D> void bitonicMerge(List<D> data, Comparator<D> comparator, int left, int length) {
        if(length > 1) {
            var mid = getPowerOfTwoLess(length);

            for(int i = left; i < left + length - mid; i++) if(comparator.compare(data.get(i), data.get(i + mid)) > 0) swap(data, i, i + mid);

            List<Callable<Void>> tasks = List.of(
                    () -> {
                        bitonicMerge(data, comparator, left, mid);

                        return null;
                    },
                    () -> {
                        bitonicMerge(data, comparator, left + mid, length - mid);

                        return null;
                    }
            );

            try {
                executors.invokeAll(tasks);
            } catch (InterruptedException e) {
                log.error("Thread got interrupted!");

                throw new ParallelExecutionException(e.getMessage());
            }
        }
    }

    private static <D> void oddEvenNetworkExecute(List<D> data, Comparator<D> comparator, int left, int length) {
        if(length > 1) {
            var mid = getPowerOfTwoLess(length);
            List<Callable<Void>> tasks = List.of(
                    () -> {
                        oddEvenNetworkExecute(data, comparator, left, mid);

                        return null;
                    },
                    () -> {
                        oddEvenNetworkExecute(data, comparator, left + mid, mid);

                        return null;
                    }
            );

            try {
                executors.invokeAll(tasks);
            } catch (InterruptedException e) {
                log.error("Thread got interrupted!");

                throw new ParallelExecutionException(e.getMessage());
            }

            oddEvenMerge(data, comparator, left, length, 1);
        }
    }

    private static <D> void oddEvenMerge(List<D> data, Comparator<D> comparator, int left, int right, int step) {
        var gap = step * 2;

        if(gap < right) {
            List<Callable<Void>> tasks = List.of(
                    () -> {
                        oddEvenMerge(data, comparator, left, right, gap);
                        return null;
                    },
                    () -> {
                        oddEvenMerge(data, comparator, left + step, right, gap);
                        return null;
                    }
            );

            try {
                executors.invokeAll(tasks);
            } catch (InterruptedException e) {
                log.error("Thread got interrupted!");

                throw new ParallelExecutionException(e.getMessage());
            }

            for(int i = left + step; i + step < left + right; i += gap) if (comparator.compare(data.get(i), data.get(i + step)) > 0) swap(data, i, i + step);
        } else if (comparator.compare(data.get(left), data.get(left + step)) > 0) {
            swap(data, left, left + step);
        }
    }

    private static <D> void dualPivotQuick(List<D> data, Comparator<D> comparator, int left, int right) {
        if(left < right) {
            if(comparator.compare(data.get(left), data.get(right)) > 0) swap(data, left, right);

            var i = left + 1;
            var j = left + 1;
            var k = right - 1;
            var leftPivot = data.get(left);
            var rightPivot = data.get(right);

            while(j <= k) {
                if(comparator.compare(data.get(j), leftPivot) < 0) {
                    swap(data, i, j);
                    i++;
                } else if(comparator.compare(data.get(j), rightPivot) >= 0) {
                    while(j < k && comparator.compare(data.get(k), rightPivot) >= 0) k--;

                    swap(data, j, k);
                    k--;

                    if(comparator.compare(data.get(j), leftPivot) < 0) {
                        swap(data, i, j);
                        i++;
                    }
                }

                j++;
            }

            i--;
            k++;

            swap(data, left, i);
            swap(data, right, k);

            var iFinal = i;
            var kFinal = k;

            List<Callable<Void>> tasks = List.of(
                    () -> {
                        dualPivotQuick(data, comparator, left, iFinal - 1);

                        return null;
                    },
                    () -> {
                        dualPivotQuick(data, comparator, iFinal + 1, kFinal - 1);

                        return null;
                    },
                    () -> {
                        dualPivotQuick(data, comparator, kFinal + 1, right);

                        return null;
                    }
            );

            try {
                executors.invokeAll(tasks);
            } catch (InterruptedException e) {
                log.error("Thread got interrupted!");

                throw new ParallelExecutionException(e.getMessage());
            }
        }
    }

    private static <D> void intro(List<D> data, Comparator<D> comparator, int left, int right, int depth) {
        if(right - left <= 16 ) {
            binaryInsertion(data, comparator);

            return;
        }

        if(depth == 0) heap(data, comparator);

        depth--;
        var pivotFinal = partitionQuick(data, comparator, left, right);

        intro(data, comparator, left, pivotFinal - 1, depth);
        intro(data, comparator, pivotFinal + 1, right, depth);
    }

    private static <D> int partitionQuick(List<D> data, Comparator<D> comparator, int left, int right) {
        D pivotDatum = data.get(right);
        int pivot = left - 1;

        for (int i = left; i < right; i++) {
            if (comparator.compare(data.get(i), pivotDatum) < 0) {
                pivot++;
                swap(data, pivot, i);
            }
        }

        swap(data, pivot + 1, right);
        return pivot + 1;
    }

    private static <D> void insertion(List<D> data, Comparator<D> comparator, int start, int stop) {
        for (int i = start + 1; i < stop; i++) {
            var pos = Math.abs(RandomAccessUtils.binarySearch(data, comparator, start, i, data.get(i)) + 1);
            var datum = data.get(i);

            for(int j = i; j > pos; j--) data.set(j, data.get(j - 1));

            data.set(pos, datum);
        }
    }

    private static <D> void weave(List<D> data, Comparator<D> comparator, int left, int right) {
        if(left < right) {
            var middle = (left + right) / 2;

            weave(data, comparator, left, middle);
            weave(data, comparator, middle + 1, right);

            List<D> leftCopy = new ArrayList<>(data.subList(left, middle));
            List<D> rightCopy = new ArrayList<>(data.subList(middle, right));

            var i = left;

            while(i < right) {
                if(i % 2 == 0 && !leftCopy.isEmpty()) data.set(i++, leftCopy.removeFirst());
                else if(i % 2 == 1 && !rightCopy.isEmpty()) data.set(i++, rightCopy.removeFirst());
                else if(!leftCopy.isEmpty()) data.set(i++, leftCopy.removeFirst());
                else data.set(i++, rightCopy.removeFirst());
            }

            insertion(data, comparator, left, right);
        }
    }

    private static <D> void heapifySmooth(List<D> data, Comparator<D> comparator, int start, int end) {
        var i = start;
        var j = 0;
        var k = 0;

        while (k < end - start + 1) {
            if ((k & 0xAAAAAAAA) != 0) {
                j = j + i;
                i = i >> 1;
            }
            else {
                i = i + j;
                j = j >> 1;
            }

            k = k + 1;
        }

        while (i > 0) {
            j = j >> 1;
            k = i + j;
            while (k < end) {
                if (comparator.compare(data.get(k), data.get(k - i)) > 0) break;

                swap(data, k, k - i);
                k = k + i;
            }

            i = j;
        }
    }

    private static int leonardo(int k) {
        if (k < 2) {
            return 1;
        }
        return leonardo(k - 1) + leonardo(k - 2) + 1;
    }

    private static int getPowerOfTwoLess(int length) {
        var mid = 1;

        while(mid > 0 && mid < length) mid <<= 1;

        return mid >>> 1;
    }

    private static <D> void quickLL(List<D> data, Comparator<D> comparator, int left, int right) {
        if(left < right) {
            D pivot = data.get(left);
            int leftPointer = left + 1;
            int rightPointer = left + 1;

            while (rightPointer <= right) {
                if (comparator.compare(data.get(rightPointer), pivot) < 0) {
                    swap(data, leftPointer, rightPointer);
                    leftPointer++;
                }

                rightPointer++;
            }

            swap(data, left, leftPointer - 1);
            var pivotIndex = leftPointer - 1;

            List<Callable<Void>> tasks = List.of(
                    () -> {
                        quickLL(data, comparator, left, pivotIndex - 1);

                        return null;
                    },
                    () -> {
                        quickLL(data, comparator, pivotIndex + 1, right);

                        return null;
                    }
            );

            try {
                executors.invokeAll(tasks);
            } catch (InterruptedException e) {
                log.error("Thread got interrupted!");

                throw new ParallelExecutionException(e.getMessage());
            }
        }
    }

    private static <D> int binaryFirst(List<D> data, Comparator<D> comparator, D value, int rangeStart, int rangeEnd) {
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

    private static <D> int binaryLast(List<D> data,  Comparator<D> comparator, D value, int rangeStart, int rangeEnd) {
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

    private static <D> int findFirstForward(List<D> data, Comparator<D> comparator, D value, int rangeStart, int rangeEnd, int unique) {
        if (rangeEnd - rangeStart == 0) return rangeStart;

        int index;
        var skip = Math.max((rangeEnd - rangeStart)/unique, 1);

        for (index = rangeStart + skip; comparator.compare(data.get(index - 1), value) < 0; index += skip) if (index >= rangeEnd - skip) return binaryFirst(data, comparator, value, index, rangeEnd);

        return binaryFirst(data, comparator, value, index - skip, index);
    }

    private static <D> int findLastForward(List<D> data, Comparator<D> comparator, D value, int rangeStart, int rangeEnd, int unique) {
        if (rangeEnd - rangeStart== 0) return rangeStart;
        int index;
        var skip = Math.max((rangeEnd - rangeStart) / unique, 1);

        for (index = rangeStart + skip; comparator.compare(value, data.get(index - 1)) >= 0; index += skip) if (index >= rangeEnd - skip) return binaryLast(data, comparator, value, index, rangeEnd);

        return binaryLast(data, comparator, value, index - skip, index);
    }

    private static <D> int findFirstBackward(List<D> data, Comparator<D> comparator, D value, int rangeStart, int rangeEnd, int unique) {
        if (rangeEnd - rangeStart == 0) return rangeStart;

        int index;
        var skip = Math.max((rangeEnd - rangeStart) / unique, 1);

        for (index = rangeEnd - skip; index > rangeStart && comparator.compare(data.get(index - 1), value) >= 0; index -= skip) if (index < rangeStart + skip) return binaryFirst(data, comparator, value, rangeStart, index);

        return binaryFirst(data, comparator, value, index, index + skip);
    }

    private static <D> int findLastBackward(List<D> data, Comparator<D> comparator, D value, int rangeStart, int rangeEnd, int unique) {
        if (rangeEnd - rangeStart == 0) return rangeStart;

        int index;
        var skip = Math.max((rangeEnd - rangeStart) / unique, 1);

        for (index = rangeEnd - skip; index > rangeStart && comparator.compare(value, data.get(index - 1)) < 0; index -= skip) if (index < rangeStart + skip) return binaryLast(data, comparator, value, rangeStart, index);

        return binaryLast(data, comparator, value, index, index + skip);
    }

    private static <D> void blockSwap(List<D> data, int start1, int start2, int blockSize) {
        for (int index = 0; index < blockSize; index++) swap(data, start1 + index, start2 + index);
    }

    private static <D> void rotate(List<D> data, int amount, int rangeStart, int rangeEnd, D[] cache) {
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

    private static <D> void mergeInto(List<D> from, Comparator<D> comparator, int AStart, int AEnd, int BStart, int BEnd, D[] into, int atIndex) {
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

    private static <D> void mergeExternal(List<D> data, Comparator<D> comparator, int AStart, int AEnd, int BStart, int BEnd, D[] cache) {
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

    private static <D> void mergeInternal(List<D> data, Comparator<D> comparator, int AStart, int AEnd, int BStart, int BEnd, int bufferStart) {
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

    private static <D> void mergeInPlace(List<D> data, Comparator<D> comparator, int AStart, int AEnd, int BStart, int BEnd, D[] cache) {
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

    private static <D> void netSwap(List<D> data, Comparator<D> comparator, int[] order, int rangeStart, int x, int y) {
        var compare = comparator.compare(data.get(rangeStart + x), data.get(rangeStart + y));
        if (compare > 0 || (order[x] > order[y] && compare == 0)) {
            swap(data, rangeStart + x, rangeStart + y);
            var swap2 = order[x];
            order[x] = order[y];
            order[y] = swap2;
        }
    }
}