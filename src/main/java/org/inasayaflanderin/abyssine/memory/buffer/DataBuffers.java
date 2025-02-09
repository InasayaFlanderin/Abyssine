package org.inasayaflanderin.abyssine.memory.buffer;

import java.io.Serializable;
import java.util.function.IntFunction;

public interface DataBuffers<D, C extends DataBuffers<D, C>> extends Serializable {
    void setPosition(int newPosition);
    int getPosition();
    int getMarkPosition();
    void next();
    D read();
    void write(D datum);
    void flip();
    void clear();
    void reset();
    void mark();
    int remain();
    Object[] toArray();
    D[] toArray(IntFunction<D[]> generator);
    D[] toArray(D[] array);
    C sub(int fromIndex, int toIndex);
    C duplicate();
    C view(int fromIndex, int toIndex);
}