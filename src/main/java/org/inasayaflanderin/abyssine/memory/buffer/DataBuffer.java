package org.inasayaflanderin.abyssine.memory.buffer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;
import org.inasayaflanderin.abyssine.parallel.ReentrantLock;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

@Log @EqualsAndHashCode @ToString @SuppressWarnings("unchecked")
public class DataBuffer<D> implements DataBuffers<D, DataBuffer<D>> {
    @Serial
    private static final long serialVersionUID = -3668388205802965444L;
    private final LinkedList<D> data;
    @EqualsAndHashCode.Exclude
    @Getter
    private transient ByteBuffer buffer;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private static final ByteArrayOutputStream byteOStream = new ByteArrayOutputStream();
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private static ObjectOutputStream oos;
    @EqualsAndHashCode.Exclude
    @Getter
    @Setter
    private int position;
    @EqualsAndHashCode.Exclude
    private int markedPosition;
    private final ReentrantLock writeLock;
    private final ReentrantLock positionLock;

    public DataBuffer(Collection<D> c) {
        this.writeLock = new ReentrantLock("Data buffer write lock");
        this.positionLock = new ReentrantLock("Data buffer position lock");

        this.data = new LinkedList<>(c);
        try {
            oos = new ObjectOutputStream(byteOStream);
            oos.writeObject(data.removeFirst());
            oos.flush();
            this.buffer = ByteBuffer.wrap(byteOStream.toByteArray());
        } catch (IOException e) {
            log.severe(e.toString());
        }
        this.position = 0;
    }
    @SafeVarargs
    public DataBuffer(D... data) {
        this(Arrays.asList(data));
    }

    public void next() {
        this.writeLock.lock();
        this.positionLock.lock();
        try {
            if (this.data.isEmpty()) return;

            int maxIndex = this.data.size() - 1;
            this.position &= ~(this.position >> 31); //this.position &= ~((this.position >> 31) ^ (maxIndex >> 31))
            int sign = ((this.position - maxIndex) >> 31) & 1;
            this.position = (sign * this.position) + ((1 - sign) * maxIndex);
            D datum = data.remove(this.position);

            if (datum == null) {
                next();
                return;
            }

            oos.writeObject(datum);
            oos.reset();
            this.buffer = ByteBuffer.wrap(byteOStream.toByteArray());
        }catch(IOException e) {
            log.severe(e.toString());
        } finally {
            this.writeLock.unlock();
            this.positionLock.unlock();
        }
    }

    public D read() {
        byte[] dataArray = new byte[this.buffer.remaining()];
        this.buffer.get(dataArray);

        try {
            var byteIStream = new ByteArrayInputStream(dataArray);
            var ois = new ObjectInputStream(byteIStream);
            return (D) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.severe(e.toString());
            return null;
        }
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

    public void reset() {
        this.positionLock.lock();
        try {
            this.position = this.markedPosition;
        } finally {
            this.positionLock.unlock();
        }
    }

    public DataBuffer<D> slice(int start, int end) {
        return new DataBuffer<>(this.data.subList(Math.max(0, start), Math.min(end, this.data.size() - 1)));
    }

    public DataBuffer<D> duplicate() {
        var feedData = this.data;
        feedData.addFirst(this.read());
        DataBuffer<D> duplication = new DataBuffer<>(feedData);
        duplication.setPosition(this.markedPosition);
        duplication.mark();
        duplication.setPosition(this.position);

        return duplication;
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
            for(D newDatum : newData) write(newDatum);
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