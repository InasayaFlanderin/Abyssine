package org.inasayaflanderin.abyssine.memory.cache;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.inasayaflanderin.abyssine.exception.ImpossibleException;
import org.inasayaflanderin.abyssine.primitives.Pair;
import org.inasayaflanderin.abyssine.primitives.Quin;

import java.io.Serial;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.IntBinaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ARCache<K, D> extends AbstractArrayBasedCaches<K, D>{
    @Serial
    private static final long serialVersionUID = 125013597378612569L;
    private static final ExecutorService executors = Executors.newVirtualThreadPerTaskExecutor();

    private final List<Quin<Integer, Integer, Long, K, D>> recently;
    private final List<Quin<Integer, Integer, Long, K, D>> frequency;
    @Getter
    @ToString.Exclude
    public final int size;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final List<K> recentlyGhost;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final List<K> frequencyGhost;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private double totalFrequency;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private int balancePoint;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private int recentlyEntries;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private int frequencyEntries;
    @Getter
    private final boolean leastRecently;
    @Getter
    private final boolean leastFrequency;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private int recentlyPrevious;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private int frequencyPrevious;

    @SuppressWarnings("unchecked")
    public ARCache(int size, boolean isLeastRecently, boolean isLeastFrequency) {
        int initialSize = initialSize(size);

        this.recently = Arrays.asList(new Quin[initialSize]);
        this.frequency = Arrays.asList(new Quin[initialSize]);
        this.recentlyGhost = new LinkedList<>();
        this.frequencyGhost = new LinkedList<>();
        this.balancePoint = size / 2;
        this.recentlyEntries = 0;
        this.frequencyEntries = 0;
        this.totalFrequency = 0;
        this.leastRecently = isLeastRecently;
        this.leastFrequency = isLeastFrequency;
        this.size = size;
        this.recentlyPrevious = -1;
        this.frequencyPrevious = -1;
    }

    public D read(K key) {
        if(isEmpty()) return null;

        var position = indexOf(key, true);

        if(position != -1) {
            this.recently.set(position, this.recently.get(position).withThird(this.recently.get(position).third() + 1));
            this.totalFrequency++;

            if(this.recently.get(position).third() < this.totalFrequency / (this.frequencyEntries + this.recentlyEntries)) callMove(position);
            else sendToFrequency(position);

            return this.recently.get(position).fifth();
        }

        if(this.recentlyGhost.contains(key)) this.balancePoint++;

        position = indexOf(key, false);

        if(position != -1) {
            this.totalFrequency++;
            this.frequency.set(position, this.frequency.get(position).withThird(this.frequency.get(position).third() + 1));

            return this.frequency.get(position).fifth();
        }

        if(this.frequencyGhost.contains(key)) this.balancePoint--;

        return null;
    }

    public void write(K key, D datum) {
        var position = indexOf(key, false);

        if(position != -1) {
            this.frequency.set(position, this.frequency.get(position).withFifth(datum).withThird(this.frequency.get(position).third() + 1));
            this.totalFrequency++;

            return;
        }

        position = indexOf(key, true);

        if(position != -1) {
            this.recently.set(position, this.recently.get(position).withFifth(datum).withThird(this.recently.get(position).third() + 1));
            this.totalFrequency++;

            if(this.recently.get(position).third() < this.totalFrequency / (this.frequencyEntries + this.recentlyEntries)) callMove(position);
            else sendToFrequency(position);

            return;
        }

        if(this.frequencyGhost.contains(key)) {
            this.balancePoint--;
            this.frequencyGhost.remove(key);
        }

        if(this.recentlyGhost.contains(key)) {
            this.balancePoint++;
            this.recentlyGhost.remove(key);
        }

        position = hash(key, this.recently.size());

        while(this.recently.get(position) != null) position = (position + 1) % this.recently.size();

        while(this.recentlyEntries >= this.balancePoint) evict(position, true);

        if(this.recentlyPrevious != -1) this.recently.set(this.recentlyPrevious, this.recently.get(this.recentlyPrevious).withSecond(position));

        this.recently.set(position, new Quin<>(this.recentlyPrevious, -1, 0L, key, datum));
        this.recentlyPrevious = position;
        this.recentlyEntries++;
    }

    public void clear() {
        this.recentlyGhost.clear();
        this.frequencyGhost.clear();
        this.recentlyEntries = 0;
        this.frequencyEntries = 0;
        this.balancePoint = this.size / 2;
        this.totalFrequency = 0;
        this.recentlyPrevious = -1;
        this.frequencyPrevious = -1;
        this.recently.clear();
        this.frequency.clear();
    }

    public void removeByKey(K key) {
        if(isEmpty()) return;

        var position = indexOf(key, true);

        if(position != -1) {
            remove(position, true);

            return;
        }

        position = indexOf(key, false);

        if(position != -1) remove(position, false);
    }

    public boolean containsByKey(K key) {
        return indexOf(key, true) != -1 || indexOf(key, false) != -1;
    }

    public void removeByDatum(D datum) throws InterruptedException {
        if(isEmpty()) return;

        List<Callable<Void>> tasks = new ArrayList<>(2);
        tasks.add(() -> {
            IntStream.range(0, this.recently.size())
                    .filter(i -> this.recently.get(i) != null && this.recently.get(i).fifth().equals(datum))
                    .forEach(i -> remove(i, true));

            return null;
        });
        tasks.add(() -> {
            IntStream.range(0, this.frequency.size())
                    .filter(i -> this.frequency.get(i) != null && this.frequency.get(i).fifth().equals(datum))
                    .forEach(i -> remove(i, false));

            return null;
        });

        executors.invokeAll(tasks);
    }

    public boolean containsByDatum(D datum) {
        if(isEmpty()) return false;

        return Stream.of(
                this.recently.stream().parallel().anyMatch(entry -> entry != null && entry.fifth().equals(datum)),
                this.frequency.stream().parallel().anyMatch(entry -> entry != null && entry.fifth().equals(datum))
        ).parallel().anyMatch(i -> i);
    }

    @SuppressWarnings("unchecked")
    public Pair<K, D>[] toArray() {
        if(isEmpty()) return new Pair[this.size];

        return Stream.concat(
                this.recently.stream().filter(Objects::nonNull),
                this.frequency.stream().filter(Objects::nonNull)
        ).map(entry -> new Pair<>(entry.fourth(), entry.fifth()))
                .toArray(Pair[]::new);
    }

    public boolean isEmpty() {
        return this.recentlyEntries == 0 && this.frequencyEntries == 0;
    }

    private void remove(int index, boolean isRecently) {
        if(isRecently) {
            if(index == this.recentlyPrevious) this.recentlyPrevious = this.recently.get(index).first();

            if(this.recently.get(index).first() != -1) this.recently.set(this.recently.get(index).first(), this.recently.get(this.recently.get(index).first()).withSecond(this.recently.get(index).second()));
            if(this.recently.get(index).second() != -1) this.recently.set(this.recently.get(index).second(), this.recently.get(this.recently.get(index).second()).withFirst(this.recently.get(index).first()));
            this.totalFrequency -= this.recently.get(index).third();
            addGhost(this.recently.get(index).fourth(), true);
            this.recently.set(index, null);
            this.recentlyEntries--;
        } else {
            if(index == this.frequencyPrevious) this.frequencyPrevious = this.frequency.get(index).first();

            if(this.frequency.get(index).first() != -1) this.frequency.set(this.frequency.get(index).first(), this.frequency.get(this.frequency.get(index).first()).withSecond(this.frequency.get(index).second()));
            if(this.frequency.get(index).second() != -1) this.frequency.set(this.frequency.get(index).second(), this.frequency.get(this.frequency.get(index).second()).withFirst(this.frequency.get(index).first()));
            this.totalFrequency -= this.frequency.get(index).third();
            addGhost(this.frequency.get(index).fourth(), false);
            this.frequency.set(index, null);
            this.frequencyEntries--;
        }
    }

    private int indexOf(K key, boolean isRecently) {
        if(isRecently) {
            if(this.recentlyEntries == 0) return -1;

            var position = hash(key, this.recently.size());

            while(this.recently.get(position) == null) position = (position + 1) % this.recently.size();

            return Stream.of(
                    IntStream.iterate(position, i -> i != -1, i -> this.recently.get(i).second())
                            .filter(i -> this.recently.get(i).fourth().equals(key)).findFirst(),
                    IntStream.iterate(position, i -> i != -1, i -> this.recently.get(i).first())
                            .filter(i -> this.recently.get(i).fourth().equals(key)).findFirst()
            ).parallel().filter(OptionalInt::isPresent).mapToInt(OptionalInt::getAsInt).findFirst().orElse(-1);
        } else {
            if(this.frequencyEntries == 0) return -1;

            var position = hash(key, this.frequency.size());

            while(this.frequency.get(position) == null) position = (position + 1) % this.frequency.size();

            return Stream.of(
                    IntStream.iterate(position, i -> i != -1, i -> this.frequency.get(i).second())
                            .filter(i -> this.frequency.get(i).fourth().equals(key)).findFirst(),
                    IntStream.iterate(position, i -> i != -1, i -> this.frequency.get(i).first())
                            .filter(i -> this.frequency.get(i).fourth().equals(key)).findFirst()
            ).parallel().filter(OptionalInt::isPresent).mapToInt(OptionalInt::getAsInt).findFirst().orElse(-1);
        }
    }

    private void callMove(int index) {
        if(this.recently.get(index).first() != -1) this.recently.set(this.recently.get(index).first(), this.recently.get(this.recently.get(index).first()).withSecond(this.recently.get(index).second()));
        if(this.recently.get(index).second() != -1) this.recently.set(this.recently.get(index).second(), this.recently.get(this.recently.get(index).second()).withFirst(this.recently.get(index).first()));
        this.recently.set(index, this.recently.get(index).withSecond(-1).withFirst(this.recentlyPrevious));
        this.recentlyPrevious = index;
    }

    private void addGhost(K key, boolean isRecently) {
        if(isRecently) {
            this.recentlyGhost.add(key);

            while(this.recentlyGhost.size() > this.balancePoint) this.recentlyGhost.removeFirst();
        } else {
            this.frequencyGhost.add(key);

            while(this.frequencyGhost.size() > (this.size - this.balancePoint)) this.recentlyGhost.removeFirst();
        }
    }

    private void sendToFrequency(int index) {
        var datum = this.recently.get(index);

        remove(index, true);

        var position = hash(datum.fourth(), this.frequency.size());

        while(this.frequency.get(position) != null) position = (position + 1) % this.frequency.size();
        while(this.frequencyEntries >= (this.size - this.balancePoint)) evict(position, false);

        this.frequency.set(position, datum.withFirst(this.frequencyPrevious).withSecond(-1));
        this.totalFrequency += datum.third();
        this.frequency.set(this.frequencyPrevious, this.frequency.get(this.frequencyPrevious).withSecond(position));
        this.frequencyPrevious = position;
        this.frequencyEntries++;
    }

    private void evict(int position, boolean isRecently) {
        IntBinaryOperator least = (first, second) -> this.recently.get(first).third() < this.recently.get(second).third() ? first : second;
        IntBinaryOperator most = (first, second) -> this.recently.get(first).third() > this.recently.get(second).third() ? first : second;
        var evictPosition = position;

        if(isRecently) {
            while(this.recently.get(evictPosition) == null) evictPosition = (evictPosition + 1) % this.recently.size();

            IntStream.iterate(evictPosition, i -> i != -1, i -> this.leastRecently ? this.recently.get(i).first() : this.recently.get(i).second())
                    .filter(i -> (this.leastRecently ? this.recently.get(i).first() : this.recently.get(i).second()) == -1).findFirst()
                    .ifPresentOrElse(i -> remove(i, true),
                            () -> {
                                log.error("Same as Recently Cache where this had the head");

                                throw new ImpossibleException();
                            });
        } else {
            while(this.frequency.get(evictPosition) == null) evictPosition = (evictPosition + 1) % this.frequency.size();

            Stream.of(
                            IntStream.iterate(evictPosition, i -> i != -1, i -> this.frequency.get(i).second()).reduce(leastFrequency ? least : most),
                            IntStream.iterate(evictPosition, i -> i != -1, i -> this.frequency.get(i).first()).reduce(leastFrequency ? least : most)
                    ).parallel().filter(OptionalInt::isPresent).mapToInt(OptionalInt::getAsInt)
                    .reduce(leastFrequency ? least : most).ifPresentOrElse(i -> remove(i, false),
                            () -> {
                                log.error("Same as Frequency Cache where this had default value because of reduce");

                                throw new ImpossibleException();
                            });
        }
    }
}