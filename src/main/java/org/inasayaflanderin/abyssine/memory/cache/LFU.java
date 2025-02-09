package org.inasayaflanderin.abyssine.memory.cache;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LFU<K, D> extends AbstractFrequencyCache<K, D> {
    @Serial
    private static final long serialVersionUID = -9159720254013766244L;

    public LFU(int size) {
        super(size);
    }

    public void write(K key, D datum) {
        super.write(key, datum, (first, second) -> this.data.get(first).third() < this.data.get(second).third() ? first : second);
    }
}