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
    int getPosition();
    int remain();
    boolean hasRemaining();
    void setFair(boolean fair);

    static <D> DataBuffers<D, ? extends DataBuffers<D, ?>> createDataBuffer(Collection<D> c) {
        return new DataBuffer<>(c);
    }

    static <D> DataBuffers<D, ? extends DataBuffers<D, ?>> createDataBuffer(D... data) {
        return new DataBuffer<>(data);
    }

    static <D> DataBuffers<D, ? extends DataBuffers<D, ?>> createStackDataBuffer(Collection<D> c) {
        return new StackDataBuffer<>(c);
    }

    static <D> DataBuffers<D, ? extends DataBuffers<D, ?>> createStackDataBuffer(D... data) {
        return new StackDataBuffer<>(data);
    }

    static <D> DataBuffers<D, ? extends DataBuffers<D, ?>> createCircularDataBuffer(Collection<D> c) {
        return new CircularDataBuffer<>(c);
    }

    static <D> DataBuffers<D, ? extends DataBuffers<D, ?>> createCircularDataBuffer(D... data) {
        return new CircularDataBuffer<>(data);
    }
}
