package org.inasayaflanderin.abyssine.memory.cache;

import java.io.Serial;

public class MFU<K, D> extends AbstractFrequencyCache<K, D> {
    @Serial
    private static final long serialVersionUID = -8066902178550245148L;

    public MFU(int size) {
        super(size);
    }

    public void write(K key, D datum) {
        super.write(key, datum, (first, second) -> this.data.get(first).third() > this.data.get(second).third() ? first : second);
    }
}