package org.inasayaflanderin.abyssine.miscellaneous.sort;

import lombok.extern.slf4j.Slf4j;
import org.inasayaflanderin.abyssine.exception.ParallelExecutionException;
import org.inasayaflanderin.abyssine.miscellaneous.BinarySearchTree;
import org.inasayaflanderin.abyssine.miscellaneous.CartesianTree;
import org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils;
import org.inasayaflanderin.abyssine.primitives.Quin;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.*;
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

    /*public static <D> void wiki(D[] data, Comparator<D> comparator) {
        wiki(Arrays.asList(data), comparator);
    }*/

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
            var pos = Math.abs(RandomAccessUtils.binarySearch(data, start, i, data.get(i), comparator) + 1);
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
}