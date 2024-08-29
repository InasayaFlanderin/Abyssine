package org.inasayaflanderin.abyssine.memory.cache;

import java.io.Serializable;

public interface Caches<K, D, C extends Caches<K, D, C>> extends Serializable {
    void limit(int newLimit);
    D read(K key);
    void write(K key, D datum);
    boolean contains(K key);
    void clear();
}
