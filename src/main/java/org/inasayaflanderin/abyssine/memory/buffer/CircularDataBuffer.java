package org.inasayaflanderin.abyssine.memory.buffer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CircularDataBuffer<D> extends AbstractDataBuffer<D, CircularDataBuffer<D>> {
    @Serial
    private static final long serialVersionUID = -2551388619940473400L;

    private int writePosition;
    @Getter
    private int capacity;

    public CircularDataBuffer() {
        super(new LinkedList<>());
        this.capacity = 10;
        this.writePosition = 0;
    }

    public CircularDataBuffer(int capacity) {
        super(new LinkedList<>());
        this.capacity = capacity;
        this.writePosition = 0;
    }

    @SafeVarargs
    public CircularDataBuffer(D... data) {
        super(new LinkedList<>(List.of(data)));
        this.capacity = data.length;
        this.writePosition = 0;
    }

    public CircularDataBuffer(Collection<D> data) {
        super(new LinkedList<>(data));
        this.capacity = data.size();
        this.writePosition = 0;
    }

    private CircularDataBuffer(List<D> data) {
        super(data);
        this.capacity = data.size();
        this.writePosition = 0;
    }

    public void next() {
        if(remain() == 0) return;

        while(this.data.get(this.position) == null) this.position = (this.position + 1) % this.capacity;

        this.currentDatum = this.data.get(this.position);
        this.data.set(this.position, null);
        this.position = (this.position + 1) % this.capacity;
    }

    public void write(D datum) {
        this.data.set(this.writePosition, datum);
        this.writePosition = (this.writePosition + 1) % this.capacity;
    }

    public int remain() {
        return (int) this.data.stream().parallel().filter(Objects::nonNull).count();
    }

    public void setCapacity(int capacity) {
        if(capacity <= 0) throw new IllegalArgumentException("Capacity must be greater than 0");

        if(capacity == this.capacity) return;
        if(capacity < this.capacity) {
            while(capacity < this.data.size()) this.data.removeLast();

            if(this.position > capacity - 1) this.position = 0;
            if(this.writePosition > capacity - 1) this.writePosition = 0;
        }

        this.capacity = capacity;
    }

    protected CircularDataBuffer<D> newBuffer(List<D> data) {
        return new CircularDataBuffer<>(data);
    }
}