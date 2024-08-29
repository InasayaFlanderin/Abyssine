package org.inasayaflanderin.abyssine.memory.buffer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;
import org.inasayaflanderin.abyssine.parallel.ReentrantLock;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@Log @ToString @SuppressWarnings("unchecked")
public class CircularDataBuffer<D> implements DataBuffers<D, CircularDataBuffer<D>> {
    @Serial
    private static final long serialVersionUID = 5119552828850176735L;

    private Object[] data;
    private transient D currentDatum;
    @Getter @Setter
    private int position;
    private int markedPosition;
    private int writerController;
    private final ReentrantLock writeLock;
    private final ReentrantLock positionLock;

    public CircularDataBuffer(Collection<D> c) {
        this.writeLock = new ReentrantLock("Circular data buffer write lock");
        this.positionLock = new ReentrantLock("Circular data buffer position lock");
        this.data = new Object[c.size() + (10 - c.size() % 10)];
        this.writerController = this.position = 0;
        this.markedPosition = -1;
        this.write(c);

        var writerControllerMark = this.writerController;

        for(; this.writerController != 0; this.writerController = (this.writerController + 1) % this.data.length) {
            this.data[this.writerController] = null;
        }

        this.writerController = writerControllerMark;

        this.next();
    }

    @SafeVarargs
    public CircularDataBuffer(D... data) {
        this(Arrays.asList(data));
    }

    public void next() {
        this.writeLock.lock();
        this.positionLock.lock();
        try {
            if (!this.hasRemaining()) return;

            int maxIndex = this.data.length - 1;
            this.position &= ~(this.position >> 31); //this.position &= ~((this.position >> 31) ^ (maxIndex >> 31))
            int sign = ((this.position - maxIndex) >> 31) & 1;
            this.position = (sign * this.position) + ((1 - sign) * maxIndex);

            while (this.data[this.position] == null) this.position = (this.position + 1) % this.data.length;

            this.currentDatum = (D) this.data[this.position];
            this.data[this.position] = null;

            this.position = (this.position + 1) % this.data.length;
        } finally {
            this.writeLock.unlock();
            this.positionLock.unlock();
        }
    }

    public D read() {
        return this.currentDatum;
    }

    public void write(D datum) {
        this.writeLock.lock();
        try {
            this.data[this.writerController] = datum;
            this.writerController = (this.writerController + 1) % this.data.length;
        } finally {
            this.writeLock.unlock();
        }
    }

    public void write(Collection<D> newData) {
        this.writeLock.lock();
        try {
            newData.forEach(this::write);
        } finally {
            this.writeLock.unlock();
        }
    }

    @SafeVarargs
    public final void write(D... newData) {
        this.writeLock.lock();
        try {
            for (D datum : newData) write(datum);
        } finally {
            this.writeLock.unlock();
        }
    }

    public void flip() {
        this.writeLock.lock();
        try {
            Object[] newData = new Object[this.data.length];

            for (int i = 0; i < this.data.length; i++) {
                newData[i] = this.data[this.data.length - 1 - i];
            }

            this.data = newData;
        } finally {
            this.writeLock.unlock();
        }
    }

    public void clear() {
        this.writeLock.lock();
        this.positionLock.lock();
        try {
            Arrays.fill(this.data, null);
            this.writerController = 0;
            this.position = 0;
            this.markedPosition = -1;
        } finally {
            this.writeLock.unlock();
            this.positionLock.unlock();
        }
    }

    public D[] toArray() {
        return (D[]) this.data;
    }

    public void mark() {
        this.positionLock.lock();
        try {
            this.markedPosition = this.position;
        } finally {
            this.positionLock.unlock();
        }
    }

    public CircularDataBuffer<D> slice(int start, int end) {
        var initialStart = Math.max(0, start);
        var initialEnd = Math.min(end, this.data.length);
        Object[] newData = new Object[initialEnd - initialStart];

        for(int i = initialStart; i < initialEnd; i++) {
            newData[i - initialStart] = this.data[start] != null ? this.data : null;
        }

        return new CircularDataBuffer<>((D[]) newData);
    }

    public void reset() {
        this.positionLock.lock();
        try {
            this.position = this.markedPosition;
        } finally {
            this.positionLock.unlock();
        }
    }

    public CircularDataBuffer<D> duplicate() {
        CircularDataBuffer<D> newBuffer = new CircularDataBuffer<>(this.read());
        newBuffer.clear();
        newBuffer.limit(this.data.length);
        newBuffer.write((D[]) this.data);
        newBuffer.setPosition(this.markedPosition);
        newBuffer.mark();
        newBuffer.setPosition(this.position);

        return newBuffer;
    }

    public int remain() {
        int count = 0;

        for(Object datum : data) if(datum != null) count++;

        return count;
    }

    public boolean hasRemaining() {
        for(Object datum : data) if(datum != null) return true;

        return false;
    }

    public void limit(int newLimit) {
        this.writeLock.lock();
        this.positionLock.lock();
        try {
            Object[] newData = new Object[newLimit];

            if (newLimit == this.data.length) {
                return;
            } else if (newLimit < this.data.length) {
                this.writerController--;
                var ensurePosition = -1;
                var ensureMark = -1;
                for (int i = newLimit - 1; i >= 0; i--) {
                    if (this.writerController < 0) this.writerController = this.data.length - 1;
                    newData[i] = this.data[this.writerController];
                    if (this.writerController == this.position) ensurePosition = i;
                    if (this.writerController == this.markedPosition) ensureMark = i;
                }

                this.writerController = 0;
                this.position = Math.max(ensurePosition, 0);
                this.markedPosition = ensureMark >= 0 ? ensureMark : -1;
            } else {
                for (int i = 0; i < newLimit; i++) {
                    newData[i] = i < this.data.length ? this.data[i] : null;
                }
            }

            this.data = newData;
        } finally {
            this.positionLock.unlock();
            this.writeLock.unlock();
        }
    }

    public void setFair(boolean fair) {
        synchronized(this) {
            this.writeLock.setFair(fair);
            this.positionLock.setFair(fair);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CircularDataBuffer<?> that = (CircularDataBuffer<?>) o;
        return Objects.deepEquals(data, that.data) && Objects.equals(writeLock, that.writeLock) && Objects.equals(positionLock, that.positionLock);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }
}