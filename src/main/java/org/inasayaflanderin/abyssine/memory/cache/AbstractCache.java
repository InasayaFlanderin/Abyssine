package org.inasayaflanderin.abyssine.memory.cache;

import java.io.Serial;

public abstract class AbstractCache<K, D> implements Caches<K, D>{
    @Serial
    private static final long serialVersionUID = 2998285765070928191L;

    protected int calculatedDataSize(int initialCapacity) {
        var capacity = (int) Math.min(Math.ceil(initialCapacity /  (-0x1.4afd7694c9f95p-62 * initialCapacity * initialCapacity + 0x1.ec58fd3dae086p-31 * initialCapacity + 0.5)), Integer.MAX_VALUE);
        capacity--;
        capacity |= capacity >> 1;
        capacity |= capacity >> 2;
        capacity |= capacity >> 4;
        capacity |= capacity >> 8;
        capacity |= capacity >> 16;
        capacity++;

        return capacity;
    }

    protected int hash(K key, int cacheSize) {
        int result = key.hashCode() * 0x9E3779B9;

        return (result ^ (result >> 16)) & (cacheSize - 1);
    }

    public abstract D read(K key);

    public boolean contains(K key) {
        return read(key) != null;
    }
}