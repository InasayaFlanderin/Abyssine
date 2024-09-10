package org.inasayaflanderin.abyssine.memory.cache;

import lombok.ToString;

import java.io.Serial;

@ToString(callSuper = true)
public class LFUCache<K, D> extends FrequencyCache<K, D> {
    @Serial
    private static final long serialVersionUID = -1699712688221614501L;

    LFUCache(int initialCapacity) {
        super(initialCapacity);
    }

    protected boolean evictCompare(int evicted, int current) {
        return this.data[current].getFifth() <= this.data[evicted].getFifth();
    }
}