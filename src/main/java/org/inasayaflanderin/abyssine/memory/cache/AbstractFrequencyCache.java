package org.inasayaflanderin.abyssine.memory.cache;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.inasayaflanderin.abyssine.exception.ImpossibleException;
import org.inasayaflanderin.abyssine.primitives.Pair;
import org.inasayaflanderin.abyssine.primitives.Quin;

import java.io.Serial;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.IntBinaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class AbstractFrequencyCache<K, D> extends AbstractArrayBasedCaches<K, D> {
    @Serial
    private static final long serialVersionUID = 5456673969142172291L;

    protected List<Quin<Integer, Integer, Long, K, D>> data;
    @Getter
    @ToString.Exclude
    private final int size;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private int numEntries;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private int previousPosition;

    @SuppressWarnings("unchecked")
    protected AbstractFrequencyCache(int size) {
        this.size = size;
        this.data = Arrays.asList(new Quin[initialSize(size)]);
        this.numEntries = 0;
        this.previousPosition = -1;
    }

    public D read(K key) {
        var position = indexOf(key);

        if (position == -1) return null;

        this.data.set(position, this.data.get(position).withThird(this.data.get(position).third() + 1));

        return this.data.get(position).fifth();
    }

    protected void write(K key, D datum, IntBinaryOperator comparator) {
        var position = indexOf(key);

        if(position != -1) {
            this.data.set(position, this.data.get(position).withFifth(datum).withThird(this.data.get(position).third() + 1));

            return;
        }

        position = hash(key, this.data.size());

        while(this.numEntries >= this.size) {
            var searchPosition = position;

            while(this.data.get(searchPosition) == null) searchPosition = (searchPosition + 1) % this.data.size();

            Stream.of(
                            IntStream.iterate(searchPosition, i -> i != -1, i -> this.data.get(i).second()).reduce(comparator),
                            IntStream.iterate(searchPosition, i -> i != -1, i -> this.data.get(i).first()).reduce(comparator)
                    ).parallel().filter(OptionalInt::isPresent).mapToInt(OptionalInt::getAsInt)
                    .reduce(comparator).ifPresentOrElse(this::remove,
                            () -> {
                                log.error("This has default value of the first item found while search, if this was here, this must be non empty cache.");

                                throw new ImpossibleException();
                            });
        }

        while(this.data.get(position) != null) position = (position + 1) % this.data.size();

        if(previousPosition != -1) this.data.set(previousPosition, this.data.get(previousPosition).withSecond(position));

        this.data.set(position, new Quin<>(previousPosition, -1, 0L, key, datum));
        previousPosition = position;
        this.numEntries++;
    }

    public boolean containsByKey(K key) {
        return indexOf(key) != -1;
    }

    public void removeByKey(K key) {
        var index = indexOf(key);

        if(index != -1) remove(index);
    }

    public void removeByDatum(D datum) {
        if(isEmpty()) return;

        for (int i = 0; i < this.data.size(); i++) if (this.data.get(i) != null && this.data.get(i).fifth().equals(datum)) remove(i);
    }

    public boolean containsByDatum(D datum) {
        if(isEmpty()) return false;

        return this.data.stream().parallel().anyMatch(entry -> entry != null && entry.fifth().equals(datum));
    }

    public void clear() {
        this.data.clear();
        this.numEntries = 0;
        this.previousPosition = -1;
    }

    public boolean isEmpty() {
        return this.numEntries == 0;
    }

    @SuppressWarnings("unchecked")
    public Pair<K, D>[] toArray() {
        if(isEmpty()) return new Pair[this.size];

        return this.data.stream()
                .filter(Objects::nonNull)
                .map(entry -> new Pair<>(entry.fourth(), entry.fifth()))
                .toArray(Pair[]::new);
    }

    private int indexOf(K key) {
        if(isEmpty()) return -1;

        var position = hash(key, this.data.size());

        while(this.data.get(position) == null) position = (position + 1) % this.data.size();

        return Stream.of(
                IntStream.iterate(position, i -> i != -1, i -> this.data.get(i).second())
                        .filter(i -> this.data.get(i).fourth().equals(key)).findFirst(),
                IntStream.iterate(position, i -> i != -1, i -> this.data.get(i).first())
                        .filter(i -> this.data.get(i).fourth().equals(key)).findFirst()
        ).parallel().filter(OptionalInt::isPresent).mapToInt(OptionalInt::getAsInt).findFirst().orElse(-1);
    }

    protected void remove(int index) {
        if(index == this.previousPosition) this.previousPosition = this.data.get(index).first();

        if(this.data.get(index).first() != -1) this.data.set(this.data.get(index).first(), this.data.get(this.data.get(index).first()).withSecond(this.data.get(index).second()));
        if(this.data.get(index).second() != -1) this.data.set(this.data.get(index).second(), this.data.get(this.data.get(index).second()).withFirst(this.data.get(index).first()));
        this.data.set(index, null);
        this.numEntries--;
    }
}