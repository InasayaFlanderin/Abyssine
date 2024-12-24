package org.inasayaflanderin.abyssine.memory.buffer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.function.IntFunction;

@EqualsAndHashCode @ToString
public class QueuedDataBuffer<D> implements DataBuffers<D, QueuedDataBuffer<D>> {
    @Serial
    private static final long serialVersionUID = -8132556679353010615L;

    private final LinkedList<D> data;
    @Getter @Setter
    @EqualsAndHashCode.Exclude private int position;
    @Getter
    @EqualsAndHashCode.Exclude private int markPosition;
    @EqualsAndHashCode.Exclude private D currentDatum;

    public QueuedDataBuffer() {
        this(new LinkedList<>());
    }

    @SafeVarargs
    public QueuedDataBuffer(D... data) {
        this(new LinkedList<>(Arrays.asList(data)));
    }

    public QueuedDataBuffer(Collection<D> data) {
        this(new LinkedList<>(data));
    }

    private QueuedDataBuffer(LinkedList<D> data) {
        this.data = data;
        this.position = this.markPosition = 0;
        this.currentDatum = null;
    }

    public void next() {
        this.currentDatum = this.data.remove(this.position);
        this.position = Math.clamp(this.position, 0, this.data.size() - 1);
    }

    public D read() {
        return this.currentDatum;
    }

    public void write(D datum) {
        this.data.add(datum);
    }

    public void flip() {
        Collections.reverse(this.data);
    }

    public void clear() {
        this.data.clear();
        this.position = this.markPosition = 0;
        this.currentDatum = null;
    }

    public void reset() {
        this.position = this.markPosition;
    }

    public void mark() {
        this.markPosition = this.position;
    }

    public int remain() {
        return this.data.size();
    }

    public Object[] toArray() {
        return this.data.toArray();
    }

    public D[] toArray(IntFunction<D[]> generator) {
        return this.data.toArray(generator);
    }

    public D[] toArray(D[] array) {
        return this.data.toArray(array);
    }

    public QueuedDataBuffer<D> sub(int fromIndex, int toIndex) {
        return new QueuedDataBuffer<>(new LinkedList<>(this.data.subList(fromIndex, toIndex)));
    }

    public QueuedDataBuffer<D> duplicate() {
        QueuedDataBuffer<D> result = sub(0, this.data.size());
        result.write(this.currentDatum);
        result.setPosition(result.remain() - 1);
        result.next();
        result.setPosition(this.markPosition);
        result.mark();
        result.setPosition(this.position);

        return result;
    }

    public QueuedDataBuffer<D> view(int fromIndex, int toIndex) {
        return new QueuedDataBuffer<>(this.data.subList(fromIndex, toIndex));
    }
}