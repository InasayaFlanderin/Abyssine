package org.inasayaflanderin.abyssine.memory.cache;

import org.inasayaflanderin.abyssine.primitives.Quin;

import java.io.Serial;
import java.lang.reflect.Array;
import java.util.Arrays;

public abstract class FrequencyCache<K, D> extends AbstractCache<K, D> {
    @Serial
    private static final long serialVersionUID = -5811213831195193861L;
    protected final Quin<K, D, Integer, Integer, Long>[] data;
    protected final int cacheSize;
    protected int size;
    protected int lastItemPosition;

    @SuppressWarnings("unchecked")
    protected FrequencyCache(int initialCapacity) {
        if (initialCapacity < 1) throw new IllegalArgumentException("Expected capacity is less than 0");

        this.cacheSize = initialCapacity;
        this.data = (Quin<K, D, Integer, Integer, Long>[]) Array.newInstance(Quin.class, calculatedDataSize(initialCapacity));
        clear();
        this.lastItemPosition = -1;
    }

    public void clear() {
        this.lock.lock();
        try {
            Arrays.fill(this.data, null);
            this.size = 0;
            this.lastItemPosition = -1;
        } finally {
            this.lock.unlock();
        }
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

    public void remove(int index) {
        this.lock.lock();
        try {
            if (this.data[index] == null) return;

            var previous = this.data[index].getThird();
            var next = this.data[index].getFourth();

            if (index == this.lastItemPosition) this.lastItemPosition = previous;

            this.data[previous] = this.data[previous].withFourth(next);
            this.data[next] = this.data[next].withThird(previous);
            this.data[index] = null;
            size--;
        } finally {
            this.lock.unlock();
        }
    }

    public void remove(K key) {
        this.lock.lock();
        try {
            remove(indexOf(key));
        } finally {
            this.lock.unlock();
        }
    }

    public D read(K key) {
        this.lock.lock();
        try {
            var position = indexOf(key);

            if (position >= 0) {
                this.data[position] = this.data[position].withFifth(this.data[position].getFifth() + 1);

                return this.data[position].getSecond();
            }

            return null;
        } finally {
            this.lock.unlock();
        }
    }

    public int write(K key, D datum) {
        this.lock.lock();
        try {
            if (this.size > this.cacheSize)
                throw new IllegalStateException("Current number of item is larger than cache's capacity");

            var position = indexOf(key);
            if (position >= 0) {
                this.data[position] = this.data[position].withSecond(datum).withFifth(this.data[position].getFifth() + 1);
                return position;
            } else {
                position = hash(key, this.data.length);

                while (this.data[position] != null) position = (position + 1) & (this.data.length - 1);
            }

            this.data[position] = new Quin<>(key, datum, this.lastItemPosition, -1, 0L);
            this.data[lastItemPosition] = this.data[this.lastItemPosition].withFourth(position);
            this.lastItemPosition = position;
            this.size++;

            while (this.size >= cacheSize) {
                var evictedPosition = this.data[position].getThird();
                var currentSearchPosition = evictedPosition;

                do {
                    if (evictCompare(evictedPosition, currentSearchPosition)) evictedPosition = currentSearchPosition;

                    currentSearchPosition = this.data[currentSearchPosition].getThird();
                } while (currentSearchPosition != -1);

                var nextDatumPosition = this.data[evictedPosition].getFourth();
                var previousDatumPosition = this.data[evictedPosition].getThird();
                this.data[nextDatumPosition] = this.data[nextDatumPosition].withThird(previousDatumPosition);
                this.data[previousDatumPosition] = this.data[previousDatumPosition].withFourth(nextDatumPosition);
                this.data[evictedPosition] = null;
                this.size--;
            }

            return position;
        } finally {
            this.lock.unlock();
        }
    }

    protected abstract boolean evictCompare(int evicted, int current);
}
