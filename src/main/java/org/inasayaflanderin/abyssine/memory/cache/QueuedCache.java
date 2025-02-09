package org.inasayaflanderin.abyssine.memory.cache;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.inasayaflanderin.abyssine.primitives.Pair;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@ToString
@EqualsAndHashCode
public class QueuedCache<K, D> implements Caches<K, D> {
    @Serial
    private static final long serialVersionUID = -7069958152458306480L;

    @Getter
    @ToString.Exclude
    private final int size;
    private final List<Pair<K, D>> data;

    public QueuedCache(int size) {
        this.size = size;
        this.data = new ArrayList<>(size);
    }

    public D read(K key) {
        if(this.data.isEmpty()) return null;

        return this.data.stream().parallel()
                .filter(entry -> entry.first().equals(key))
                .findFirst()
                .map(Pair::second)
                .orElse(null);
    }

    public void write(K key, D datum) {
        IntStream.range(0, this.data.size())
                .filter(i -> this.data.get(i).first().equals(key)).findFirst()
                .ifPresentOrElse(i -> this.data.set(i, this.data.get(i).withSecond(datum)),
                        () -> this.data.add(new Pair<>(key, datum)));

        while(this.data.size() > this.size) this.data.removeFirst();
    }

    public void clear() {
        this.data.clear();
    }

    public void removeByKey(K key) {
        this.data.removeIf(entry -> entry.first().equals(key));
    }

    public void removeByDatum(D datum) {
        this.data.removeIf(entry -> entry.second().equals(datum));
    }

    public boolean containsByKey(K key) {
        return this.data.stream().parallel().anyMatch(entry -> entry.first().equals(key));
    }

    public boolean containsByDatum(D datum) {
        return this.data.stream().parallel().anyMatch(entry -> entry.second().equals(datum));
    }

    @SuppressWarnings("unchecked")
    public Pair<K, D>[] toArray() {
        return this.data.toArray(new Pair[0]);
    }

    public boolean isEmpty() {
        return this.data.isEmpty();
    }
}