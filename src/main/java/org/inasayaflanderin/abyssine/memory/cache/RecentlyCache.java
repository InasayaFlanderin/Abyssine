package org.inasayaflanderin.abyssine.memory.cache;

import org.inasayaflanderin.abyssine.primitives.Quad;

import java.io.Serial;
import java.lang.reflect.Array;
import java.util.Arrays;

public abstract class RecentlyCache<K, D> extends AbstractCache<K, D> {
    @Serial
    private static final long serialVersionUID = -6051303595305349806L;
    protected final Quad<K, D, Integer, Integer>[] data;
    protected final int cacheSize;
    protected int size;
    protected int lastItemPosition;

    @SuppressWarnings("unchecked")
    public RecentlyCache(int initialCapacity) {
        if (initialCapacity < 1) throw new IllegalArgumentException("Expected capacity is less than 0");

        this.cacheSize = initialCapacity;
        this.data = (Quad<K, D, Integer, Integer>[]) Array.newInstance(Quad.class, calculatedDataSize(initialCapacity));
        clear();
        this.lastItemPosition = -1;
    }

    public void clear() {
        Arrays.fill(this.data, null);
        this.size = 0;
        this.lastItemPosition = -1;
    }

    public int indexOf(K key) {
        if(this.size == 0) return -1;

        var position = hash(key, this.data.length);

        while(this.data[position] == null) position = (position + 1) & (this.data.length - 1);

        var originalPosition = position;

        do {
            if(this.data[position].getFirst().equals(key)) return position;

            position = this.data[position].getFourth();
        } while(position != -1);

        position = originalPosition;

        do {
            if(this.data[position].getFirst().equals(key)) return position;

            position = this.data[position].getThird();
        } while(position != -1);

        return -1;
    }

    public D read(K key) {
        var position = indexOf(key);

        if(position >= 0) {
            callMove(position);
            return this.data[position].getSecond();
        }

        return null;
    }

    public int write(K key, D datum) {
        if(this.size > this.cacheSize) throw new IllegalStateException("Current number of item is larger than cache's capacity");

        var position = indexOf(key);
        if(position >= 0) {
            callMove(position);
            this.data[position] = this.data[position].withSecond(datum);
            return position;
        } else {
            position = hash(key, this.data.length);

            while(this.data[position] != null) position = (position + 1) & (this.data.length - 1);
        }

        this.data[position] = new Quad<>(key, datum, this.lastItemPosition, -1);
        this.data[this.lastItemPosition] = this.data[this.lastItemPosition].withFourth(position);
        this.lastItemPosition = position;
        this.size++;


        while(this.size >= cacheSize) {
            evict(position);
        }

        return position;
    }

    public void remove(int index) {
        if(this.data[index] == null) return;

        var previous = this.data[index].getThird();
        var next = this.data[index].getFourth();

        if(index == this.lastItemPosition) this.lastItemPosition = previous;

        this.data[previous] = this.data[previous].withFourth(next);
        this.data[next] = this.data[next].withThird(previous);
        this.data[index] = null;
        size--;
    }

    public void remove(K key) {
        remove(indexOf(key));
    }

    protected abstract void callMove(int position);
    protected abstract void evict(int position);
}
