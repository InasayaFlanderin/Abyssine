package org.inasayaflanderin.abyssine.memory.cache;

import org.inasayaflanderin.abyssine.primitives.Pair;

import java.io.Serializable;

public interface Caches<K, D> extends Serializable {
    D read(K key);
    void write(K key, D datum);
    void clear();
    void removeByKey(K key);
    void removeByDatum(D datum) throws InterruptedException;
    boolean containsByKey(K key);
    boolean containsByDatum(D datum);
    Pair<K, D>[] toArray();
    int getSize();
    boolean isEmpty();
}