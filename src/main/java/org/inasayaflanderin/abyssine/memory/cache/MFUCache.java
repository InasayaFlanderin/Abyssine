package org.inasayaflanderin.abyssine.memory.cache;

import java.io.Serial;

public class MFUCache<K, D> extends FrequencyCache<K, D> {
    @Serial
    private static final long serialVersionUID = -1699712688221614501L;

    public MFUCache(int initialCapacity) {
        super(initialCapacity);
    }

    protected boolean evictCompare(int evicted, int current) {
        return this.data[current].getFifth() >= this.data[evicted].getFifth();
    }
}