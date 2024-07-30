package org.inasayaflanderin.abyssine.memory.cache;

import java.io.Serializable;

public interface Caches<D, C extends Caches<D, C>> extends Serializable {
    void limit(int newLimit);
    D read(int index);
    void write(D datum);
    int contain(D datum);
    void clear();
    C duplicate();
    C slice(int start, int end);
}
