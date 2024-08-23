package org.inasayaflanderin.abyssine.memory.buffer;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collection;

public interface DataBuffers<D, C extends DataBuffers<D, C>> extends Serializable {
    void setPosition(int index);
    void next();
    D read();
    void write(D data);
    void write(Collection<D> data);
    @SuppressWarnings("unchecked") void write(D... data);
    void flip();
    void clear();
    D[] toArray();
    void mark();
    C slice(int start, int end);
    void reset();
    C duplicate();
    int hashCode();
    boolean equals(Object obj);
    String toString();
    ByteBuffer getBuffer();
    int getPosition();
    int remain();
    boolean hasRemaining();
    void setFair(boolean fair);
}
