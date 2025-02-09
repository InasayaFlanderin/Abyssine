package org.inasayaflanderin.abyssine.memory.buffer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.IntFunction;

@EqualsAndHashCode
@ToString
public abstract class AbstractDataBuffer<D, C extends AbstractDataBuffer<D, C>> implements DataBuffers<D, C> {
    @Serial
    private static final long serialVersionUID = 1222963620426408136L;

    protected final List<D> data;
    @Getter
    @Setter
    @EqualsAndHashCode.Exclude
    protected int position;
    @Getter
    @EqualsAndHashCode.Exclude
    protected int markPosition;
    @EqualsAndHashCode.Exclude
    protected D currentDatum;

    protected AbstractDataBuffer(List<D> data) {
        this.data = data;
        this.position = this.markPosition = 0;
        this.currentDatum = null;
    }

    public abstract void next();

    public D read() {
        return currentDatum;
    }

    public abstract void write(D datum);

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

    public abstract int remain();

    public Object[] toArray() {
        return this.data.toArray();
    }

    public D[] toArray(IntFunction<D[]> generator) {
        return this.data.toArray(generator);
    }

    public D[] toArray(D[] array) {
        return this.data.toArray(array);
    }

    public C sub(int fromIndex, int toIndex) {
        return newBuffer(new LinkedList<>(this.data.subList(fromIndex, toIndex)));
    }

    public C duplicate() {
        C result = sub(0, this.data.size());
        result.write(this.currentDatum);
        result.setPosition(result.remain() - 1);
        result.next();
        result.setPosition(this.markPosition);
        result.mark();
        result.setPosition(this.position);

        return result;
    }

    public C view(int fromIndex, int toIndex) {
        return newBuffer(this.data.subList(fromIndex, toIndex));
    }

    protected abstract C newBuffer(List<D> data);
}