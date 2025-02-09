package org.inasayaflanderin.abyssine.memory.cache;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.inasayaflanderin.abyssine.primitives.Pair;
import org.inasayaflanderin.abyssine.primitives.Quad;

import java.io.Serial;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class AbstractRecentlyCache<K, D> extends AbstractArrayBasedCaches<K, D> {
    @Serial
    private static final long serialVersionUID = -1754038848127528311L;

    protected final List<Quad<Integer, Integer, K, D>> data;
    @Getter
    @ToString.Exclude
    protected final int size;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected int numEntries;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected int previousPosition;

    @SuppressWarnings("unchecked")
    protected AbstractRecentlyCache(int size) {
        this.size = size;
        this.numEntries = 0;
        this.data = Arrays.asList(new Quad[initialSize(size)]);
        this.previousPosition = -1;
    }

    public D read(K key) {
        var position = indexOf(key);

        if (position == -1) return null;

        callMove(position);

        return this.data.get(position).fourth();
    }

    public void write(K key, D datum) {
        var position = indexOf(key);

        if(position != -1) {
            this.data.set(position, this.data.get(position).withFourth(datum));

            callMove(position);

            return;
        }

        position = hash(key, this.data.size());

        while(this.numEntries >= this.size) findRemove(position);

        while(this.data.get(position) != null) position = (position + 1) % this.data.size();

        if(previousPosition != -1) this.data.set(previousPosition, this.data.get(previousPosition).withSecond(position));

        this.data.set(position, new Quad<>(previousPosition, -1, key, datum));
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

        IntStream.range(0, this.data.size())
                .filter(i -> this.data.get(i) != null && this.data.get(i).fourth().equals(datum))
                .forEach(this::remove);
    }

    public boolean containsByDatum(D datum) {
        if(isEmpty()) return false;

        return this.data.stream().parallel()
                .anyMatch(entry -> entry != null && entry.fourth().equals(datum));
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
                .map(entry -> new Pair<>(entry.third(), entry.fourth()))
                .toArray(Pair[]::new);
    }

    protected abstract void findRemove(int position);

    protected void remove(int index) {
        if(index == this.previousPosition) this.previousPosition = this.data.get(index).first();

        if(this.data.get(index).first() != -1) this.data.set(this.data.get(index).first(), this.data.get(this.data.get(index).first()).withSecond(this.data.get(index).second()));
        if(this.data.get(index).second() != -1) this.data.set(this.data.get(index).second(), this.data.get(this.data.get(index).second()).withFirst(this.data.get(index).first()));
        this.data.set(index, null);
        this.numEntries--;
    }

    private int indexOf(K key) {
        if(isEmpty()) return -1;

        var position = hash(key, this.data.size());

        while(this.data.get(position) == null) position = (position + 1) % this.data.size();

        return Stream.of(
                IntStream.iterate(position, i -> i != -1, i -> this.data.get(i).second())
                        .filter(i -> this.data.get(i).third().equals(key)).findFirst(),
                IntStream.iterate(position, i -> i != -1, i -> this.data.get(i).first())
                        .filter(i -> this.data.get(i).third().equals(key)).findFirst()
        ).parallel().filter(OptionalInt::isPresent).map(OptionalInt::getAsInt).findFirst().orElse(-1);
    }

    private void callMove(int position) {
        if(this.data.get(position).first() != -1) this.data.set(this.data.get(position).first(), this.data.get(this.data.get(position).first()).withSecond(this.data.get(position).second()));
        if(this.data.get(position).second() != -1) this.data.set(this.data.get(position).second(), this.data.get(this.data.get(position).second()).withFirst(this.data.get(position).first()));
        this.data.set(position, this.data.get(position).withSecond(-1));
        this.data.set(position, this.data.get(position).withFirst(previousPosition));
        this.previousPosition = position;
    }
}