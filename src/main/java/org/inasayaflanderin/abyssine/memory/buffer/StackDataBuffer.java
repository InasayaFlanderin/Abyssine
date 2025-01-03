package org.inasayaflanderin.abyssine.memory.buffer;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StackDataBuffer<D> extends AbstractDataBuffer<D, StackDataBuffer<D>> {
    @Serial
    private static final long serialVersionUID = 1857886865191354498L;

    public StackDataBuffer() {
        super(new LinkedList<>());
    }

    @SafeVarargs
    public StackDataBuffer(D... data) {
        super(new LinkedList<>(Arrays.asList(data)));
    }

    public StackDataBuffer(Collection<D> data) {
        super(new LinkedList<>(data));
    }

    private StackDataBuffer(List<D> data) {
        super(data);
    }

    public void next() {
        if(data.isEmpty()) return;

        this.currentDatum = this.data.remove(this.position);
        this.position--;
        this.position = Math.clamp(this.position, 0, this.data.size() - 1);
    }

    public void write(D datum) {
        this.data.add(datum);

        if(this.position == this.data.size() - 2) this.position++;
    }

    public int remain() {
        return this.data.size();
    }

    protected StackDataBuffer<D> newBuffer(List<D> data) {
        return new StackDataBuffer<>(data);
    }
}