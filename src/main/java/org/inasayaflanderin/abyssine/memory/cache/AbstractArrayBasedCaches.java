package org.inasayaflanderin.abyssine.memory.cache;

import org.inasayaflanderin.abyssine.primitives.Pair;

import java.io.Serial;

public abstract class AbstractArrayBasedCaches<K, D> implements Caches<K, D> {
    @Serial
    private static final long serialVersionUID = 2351760277186569864L;

    protected int hash(K key, int size) {
        var result = key.hashCode() * 0x9E3779B9;

        return (result ^ (result >> 16)) & (size - 1);
    }

    protected int initialSize(int size) {
        var desiredCapacity = (long) (size / (-0x1.4afd7694c9f95p-62 * size * size + 0x1.ec58fd3dae986p-31 * size + 0.5)) - 1;

        desiredCapacity |= desiredCapacity >> 1;
        desiredCapacity |= desiredCapacity >> 2;
        desiredCapacity |= desiredCapacity >> 4;
        desiredCapacity |= desiredCapacity >> 8;
        desiredCapacity |= desiredCapacity >> 16;
        return (int) ((desiredCapacity | desiredCapacity >> 32) + 1);
    }

    public abstract D read(K key);
    public abstract void write(K key, D datum);
    public abstract void clear();
    public abstract void removeByKey(K key);
    public abstract void removeByDatum(D datum) throws InterruptedException;
    public abstract boolean containsByKey(K key);
    public abstract boolean containsByDatum(D datum);
    public abstract Pair<K, D>[] toArray();
    public abstract int getSize();
    public abstract boolean isEmpty();
}