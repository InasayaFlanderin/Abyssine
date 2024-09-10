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
    int hashCode();
    boolean equals(Object obj);
    String toString();

    static <K, D> Caches<K, D> createLFUCache(int initialCapacity) {
        return new LFUCache<>(initialCapacity);
    }

    static <K, D> Caches<K, D> createMFUCache(int initialCapacity) {
        return new MFUCache<>(initialCapacity);
    }

    static <K, D> Caches<K, D> createLRUCache(int initialCapacity) {
        return new LRUCache<>(initialCapacity);
    }

    static <K, D> Caches<K, D> createMRUCache(int initialCapacity) {
        return new MRUCache<>(initialCapacity);
    }

    static <K, D> Caches<K, D> createARCache(int initialCapacity) {
        return new ARCache<>(initialCapacity);
    }
}