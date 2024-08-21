package org.inasayaflanderin.abyssine.memory.buffer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;

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

    public DataBuffer(Collection<D> c) {
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

    public synchronized void next() {
        if (this.data.isEmpty()) return;

        int maxIndex = this.data.size() - 1;
        this.position &= ~(this.position >> 31); //this.position &= ~((this.position >> 31) ^ (maxIndex >> 31))
        int sign = ((this.position - maxIndex) >> 31) & 1;
        this.position = (sign * this.position) + ((1 - sign) * maxIndex);

        try {
            D datum = data.remove(this.position);

            if(datum == null) {
                next();
                return;
            }

            oos.writeObject(datum);
            oos.reset();
            this.buffer = ByteBuffer.wrap(byteOStream.toByteArray());
        } catch (IOException e) {
            log.severe(e.toString());
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

    public synchronized void write(D datum) {
        this.data.add(datum);
    }

    public synchronized void write(Collection<D> newData) {
        this.data.addAll(newData);
    }

    public synchronized void clear() {
        this.data.clear();
        this.position = 0;
        this.markedPosition = -1;
    }

    public synchronized void flip() {
        Collections.reverse(this.data);
    }

    public D[] toArray() {
        return (D[]) this.data.toArray();
    }

    public synchronized void mark() {
        this.markedPosition = this.position;
    }

    public synchronized void reset() {
        this.position = this.markedPosition;
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
    public synchronized final void write(D... newData) {
        for(D newDatum : newData) write(newDatum);
    }
}