package org.inasayaflanderin.abyssine.memory.cache;

import java.io.Serializable;

public interface Caches<K, D> extends Serializable {
    D read(K key);
    int write(K key, D datum);
    boolean contains(K key);
    void clear();
    int indexOf(K key);
    void remove(int index);
    void remove(K key);
    void setFair(boolean fair);
}