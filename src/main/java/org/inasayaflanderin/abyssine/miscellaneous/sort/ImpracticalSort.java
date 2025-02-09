package org.inasayaflanderin.abyssine.miscellaneous.sort;

import org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils;
import org.inasayaflanderin.abyssine.miscellaneous.rng.RandomGenerators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;

public class ImpracticalSort {
    public static <D> void bogo(D[] array, BiConsumer<List<D>, RandomGenerators> shuffle, RandomGenerators rng, Comparator<D> comparator) {
        bogo(Arrays.asList(array), shuffle, rng, comparator);
    }

    public static <D> void bogo(List<D> list, BiConsumer<List<D>, RandomGenerators> shuffle, RandomGenerators rng, Comparator<D> comparator) {
        boolean sort = false;

        while(!sort) {
            shuffle.accept(list, rng);
            sort = RandomAccessUtils.isSort(list, comparator);
        }
    }

    public static <D> void pancake(D[] array, Comparator<D> comparator) {
        pancake(Arrays.asList(array), comparator);
    }

    public static <D> void pancake(List<D> list, Comparator<D> comparator) {
        for(int i = list.size(); i > 1; i--) {
            var maxIndex = IntStream.range(0, i)
                    .reduce((a, b) -> comparator.compare(list.get(a), list.get(b)) > 0 ? a : b).getAsInt();

            if(maxIndex != i - 1) {
                if(maxIndex != 0) RandomAccessUtils.flip(list, 0, maxIndex + 1);

                RandomAccessUtils.flip(list, 0, i);
            }
        }
    }

    public static <D> void stooge(D[] array, Comparator<D> comparator) {
        stooge(Arrays.asList(array), comparator);
    }

    public static <D> void stooge(List<D> list, Comparator<D> comparator) {
        stooge(list, comparator, 0, list.size() - 1);
    }

    public static <D> void slow(D[] array, Comparator<D> comparator) {
        slow(Arrays.asList(array), comparator);
    }

    public static <D> void slow(List<D> list, Comparator<D> comparator) {
        slow(list, comparator, 0, list.size() - 1);
    }

    public static <D> void sleep(D[] array, Function<D, Long> converter) {
        sleep(Arrays.asList(array), converter);
    }

    public static <D> void sleep(List<D> list, Function<D, Long> converter) {
        AtomicInteger i = new AtomicInteger(0);

        list.stream().parallel().forEach(entry -> {
            try {
                Thread.sleep(converter.apply(entry));
                list.set(i.getAndIncrement(), entry);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static <D> void miracle(D[] array, Comparator<D> comparator) {
        miracle(Arrays.asList(array), comparator);
    }

    public static <D> void miracle(List<D> list, Comparator<D> comparator) {
        if(!RandomAccessUtils.isSort(list, comparator)) miracle(list, comparator);
    }

    public static <D> void bogobogo(D[] array, BiConsumer<List<D>, RandomGenerators> shuffle, RandomGenerators rng, Comparator<D> comparator) {
        bogobogo(Arrays.asList(array), shuffle, rng, comparator);
    }

    public static <D> void bogobogo(List<D> list, BiConsumer<List<D>, RandomGenerators> shuffle, RandomGenerators rng, Comparator<D> comparator) {
        List<D> copy = new ArrayList<>(list);

        bogo2S(copy, shuffle, rng, comparator);

        if(!list.equals(copy)) {
            shuffle.accept(list, rng);

            bogobogo(list, shuffle, rng, comparator);
        }
    }

    private static <D> void stooge(List<D> list, Comparator<D> comparator, int start, int end) {
        if(comparator.compare(list.get(start), list.get(end)) > 0) RandomAccessUtils.swap(list, start, end);

        if(end - start > 1) {
            int third = (int) Math.ceil((double) (end - start) / 3);
            stooge(list, comparator, start, end - third);
            stooge(list, comparator, start + third, end);
            stooge(list, comparator, start, end - third);
        }
    }

    private static <D> void slow(List<D> list, Comparator<D> comparator, int start, int end) {
        if (start >= end) return;

        int mid = (start + end) / 2;
        slow(list, comparator, start, mid);
        slow(list, comparator, mid + 1, end);

        if (comparator.compare(list.get(end), list.get(mid)) < 0) RandomAccessUtils.swap(list, end, mid);

        slow(list, comparator, start, end - 1);
    }

    private static <D> void bogo2S(List<D> list, BiConsumer<List<D>, RandomGenerators> shuffle, RandomGenerators rng, Comparator<D> comparator) {
        if(list.size() > 1) bogobogo(list.subList(0, list.size() - 1), shuffle, rng, comparator);

        if(comparator.compare(list.get(list.size() - 2), list.getLast()) > 0) {
            shuffle.accept(list, rng);
        }
    }
}