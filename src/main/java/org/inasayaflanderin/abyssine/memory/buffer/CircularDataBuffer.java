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
import java.util.stream.IntStream;

@Log @EqualsAndHashCode @ToString
public class CircularDataBuffer<D> implements DataBuffers<D, CircularDataBuffer<D>> {
    @Serial
    private static final long serialVersionUID = 5119552828850176735L;

    private Object[] data;
    @EqualsAndHashCode.Exclude @Getter
    private transient ByteBuffer buffer;
    @EqualsAndHashCode.Exclude @ToString.Exclude private static final ByteArrayOutputStream byteOStream = new ByteArrayOutputStream();
    @EqualsAndHashCode.Exclude @ToString.Exclude private static ObjectOutputStream oos;
    @EqualsAndHashCode.Exclude @Getter @Setter
    private int position;
    @EqualsAndHashCode.Exclude private int markedPosition;
    @EqualsAndHashCode.Exclude private int writerController;

    public CircularDataBuffer(Collection<D> c) {
        try {
            oos = new ObjectOutputStream(byteOStream);
        } catch(IOException e) {
            log.severe(e.toString());
        }

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

    public synchronized void next() {
        if(!this.hasRemaining()) return;

        this.position = Math.min(Math.max(0, this.position), this.data.length - 1);

        while(this.data[this.position] == null) this.position = (this.position + 1) % this.data.length;

        this.buffer.clear();
        try {
            oos.writeObject(data[this.position]);
            oos.flush();
            this.buffer = ByteBuffer.wrap(byteOStream.toByteArray());
            this.data[this.position] = null;
        } catch(IOException e) {
            log.severe(e.toString());
        }

        this.position = (this.position + 1) % this.data.length;
    }


    @SuppressWarnings("unchecked")
    public D read() {
        byte[] dataArray = new byte[this.buffer.remaining()];
        this.buffer.get(dataArray);

        try {
            var byteIStream = new ByteArrayInputStream(dataArray);
            var ois = new ObjectInputStream(byteIStream);
            return (D) ois.readObject();
        } catch(IOException | ClassNotFoundException e) {
            log.severe(e.toString());
            return null;
        }
    }

    public synchronized void write(D datum) {
        this.data[this.writerController] = datum;
        this.writerController = (this.writerController + 1) % this.data.length;
    }

    public synchronized void write(Collection<D> newData) {
        newData.forEach(this::write);
    }

    @SafeVarargs
    public synchronized final void write(D... newData) {
        IntStream.range(0, newData.length).forEach(i -> write(newData[i]));
    }

    public synchronized void flip() {
        this.data = IntStream.range(0, this.data.length).mapToObj(i -> this.data[this.data.length - 1 - i]).toArray();
    }

    public synchronized void clear() {
        Arrays.fill(this.data, null);
        this.writerController = 0;
        this.position = 0;
        this.markedPosition = -1;
    }

    @SuppressWarnings("unchecked")
    public D[] toArray() {
        return (D[]) this.data;
    }

    public synchronized void mark() {
        this.markedPosition = this.position;
    }

    @SuppressWarnings("unchecked")
    public CircularDataBuffer<D> slice(int start, int end) {
        return new CircularDataBuffer<>((D[]) IntStream.range(start, end).mapToObj(i -> this.data[i] != null ? this.data[i] : null).toArray());
    }

    public synchronized void reset() {
        this.position = this.markedPosition;
    }

    @SuppressWarnings("unchecked")
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
        return (int) IntStream.range(0, this.data.length).filter(i -> this.data[i] != null).count();
    }

    public boolean hasRemaining() {
        return IntStream.range(0, this.data.length).anyMatch(i -> this.data[i] != null);
    }

    public synchronized void limit(int newLimit) {
        Object[] newData = new Object[newLimit];

        if(newLimit == this.data.length) {
            return;
        } else if(newLimit < this.data.length) {
            this.writerController--;
            var ensurePosition = -1;
            var ensureMark = -1;
            for(int i = newLimit - 1; i >= 0; i--) {
                if(this.writerController < 0) this.writerController = this.data.length - 1;
                newData[i] = this.data[this.writerController];
                if(this.writerController == this.position) ensurePosition = i;
                if(this.writerController == this.markedPosition) ensureMark = i;
            }

            this.writerController = 0;
            this.position = Math.max(ensurePosition, 0);
            this.markedPosition = ensureMark >= 0 ? ensureMark : -1;
        } else {
            IntStream.range(0, newLimit).forEach(i -> newData[i] = i < this.data.length ? this.data[i] : null);
        }

        this.data = newData;
    }
}
