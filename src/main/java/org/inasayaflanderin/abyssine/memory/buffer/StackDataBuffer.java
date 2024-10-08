package org.inasayaflanderin.abyssine.memory.buffer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;
import org.inasayaflanderin.abyssine.parallel.ReentrantLock;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

@Log @ToString @SuppressWarnings("unchecked")
public class StackDataBuffer<D> implements DataBuffers<D, StackDataBuffer<D>> {
    @Serial
    private static final long serialVersionUID = -472081321054822264L;
    private final LinkedList<D> data;
    private transient D currentDatum;
    @Getter @Setter private int position;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StackDataBuffer<?> that = (StackDataBuffer<?>) o;
        return data.equals(that.data) && writeLock.equals(that.writeLock) && positionLock.equals(that.positionLock);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    private int markedPosition;
    private final ReentrantLock writeLock;
    private final ReentrantLock positionLock;

    StackDataBuffer(Collection<? extends D> c) {
        this.writeLock = new ReentrantLock("Stack data buffer write lock");
        this.positionLock = new ReentrantLock("Stack data buffer position lock");
        this.data = new LinkedList<>(c);
        this.currentDatum = this.data.removeLast();
        this.position = 0;
        this.markedPosition = -1;
    }

    @SafeVarargs
    StackDataBuffer(D... data) {
        this(Arrays.asList(data));
    }

    public void next() {
        this.writeLock.lock();
        this.positionLock.lock();
        try {
            if(this.data.isEmpty()) return;

            int maxIndex = this.data.size() - 1;
            this.position &= ~(this.position >> 31); //this.position &= ~((this.position >> 31) ^ (maxIndex >> 31))
            int sign = ((this.position - maxIndex) >> 31) & 1;
            this.position = (sign * this.position) + ((1 - sign) * maxIndex);

            var removedPosition = maxIndex - this.position;
            D datum = data.remove(removedPosition);

            if(datum == null) {
                 next();
                return;
            }

            this.currentDatum = datum;
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
            this.data.add(datum);
        } finally {
            this.writeLock.unlock();
        }
    }

    public void write(Collection<D> newData) {
        this.writeLock.lock();
        try {
            this.data.addAll(newData);
        } finally {
            this.writeLock.unlock();
        }
    }

    public void clear() {
        this.writeLock.lock();
        this.positionLock.lock();
        try {
            this.data.clear();
            this.position = 0;
            this.markedPosition = -1;
        } finally {
            this.writeLock.unlock();
            this.positionLock.unlock();
        }
    }

    public void flip() {
        this.writeLock.lock();
        try {
            Collections.reverse(this.data);
        } finally {
            this.writeLock.unlock();
        }
    }

    public D[] toArray() {
        return (D[]) this.data.toArray();
    }

    public void mark() {
        this.positionLock.lock();
        try {
            this.markedPosition = this.position;  
        } finally {
            this.positionLock.unlock();
        }
        
    }

    public StackDataBuffer<D> slice(int start, int end) {
        return new StackDataBuffer<>(this.data.subList(Math.max(start, 0), Math.min(this.data.size() - 1, end)));
    }

    public StackDataBuffer<D> duplicate() {
        var feedDataArray = this.data;
        feedDataArray.add(this.read());
        StackDataBuffer<D> duplication = new StackDataBuffer<>(feedDataArray);
        duplication.setPosition(this.markedPosition);
        duplication.mark();
        duplication.setPosition(this.position);

        return duplication;
    }

    public void reset() {
        this.positionLock.lock();
        try {
            this.position = this.markedPosition;
        } finally {
            this.positionLock.unlock();
        }
    }

    public int remain() {
        return this.data.size();
    }

    public boolean hasRemaining() {
        return !this.data.isEmpty();
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

    public void setFair(boolean fair) {
        synchronized(this) {
            this.writeLock.setFair(fair);
            this.positionLock.setFair(fair);
        }
    }
}