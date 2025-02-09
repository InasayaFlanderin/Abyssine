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
public class QueuedDataBuffer<D> extends AbstractDataBuffer<D, QueuedDataBuffer<D>> {
    @Serial
    private static final long serialVersionUID = -8132556679353010615L;

    public QueuedDataBuffer() {
        super(new LinkedList<>());
    }

    @SafeVarargs
    public QueuedDataBuffer(D... data) {
        super(new LinkedList<>(Arrays.asList(data)));
    }

    public QueuedDataBuffer(Collection<D> data) {
        super(new LinkedList<>(data));
    }

    private QueuedDataBuffer(List<D> data) {
        super(data);
    }

    public void next() {
        if(data.isEmpty()) return;

        this.currentDatum = this.data.remove(this.position);
        this.position = Math.clamp(this.position, 0, this.data.size() - 1);
    }

    public void write(D datum) {
        this.data.add(datum);
    }

    public int remain() {
        return this.data.size();
    }

    protected QueuedDataBuffer<D> newBuffer(List<D> data) {
        return new QueuedDataBuffer<>(data);
    }
}