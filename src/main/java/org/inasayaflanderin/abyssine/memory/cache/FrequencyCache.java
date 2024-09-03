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
        Arrays.fill(this.data, null);
        this.size = 0;
        this.lastItemPosition = -1;
    }

    public D read(K key) {
        var position = hash(key, this.data.length);

        while(this.data[position] == null) position = (position + 1) % this.data.length;

        var originalPosition = position;

        do {
            if(this.data[position].getFirst().equals(key)) {
                this.data[position] = this.data[position].withFifth(this.data[position].getFifth() + 1);
                return this.data[position].getSecond();
            }

            position = this.data[position].getFourth();
        } while(position != -1);

        position = originalPosition;

        do {
            if(this.data[position].getFirst().equals(key)) {
                this.data[position] = this.data[position].withFifth(this.data[position].getFifth() + 1);
                return this.data[position].getSecond();
            }

            position = this.data[position].getThird();
        } while(position != -1);

        return null;
    }

    public int write(K key, D datum) {
        if(this.size > this.cacheSize) throw new IllegalStateException("Current number of item is larger than cache's capacity");

        var position = hash(key, this.data.length);
        var originalPosition = position;
        var haveNotSearchNext = true;
        var searchNext = false;
        var searchPrevious = false;
        while(this.data[position] != null) {
            if(this.data[position].getFirst().equals(key)) {
                this.data[position] = this.data[position].withSecond(datum);
                this.data[position] = this.data[position].withFifth(this.data[position].getFifth() + 1);
                return position;
            }

            if(haveNotSearchNext) {
                if(this.data[position].getFourth() != -1) searchNext = true;
                haveNotSearchNext = false;

                if(!searchNext) {
                    if(this.data[position].getThird() != -1) {
                        searchPrevious = true;
                        position = this.data[position].getThird();
                        continue;
                    }
                }
            }

            if(searchNext) {
                position = this.data[position].getFourth();

                if(this.data[position].getFourth() == -1) {
                    searchNext = false;

                    if(this.data[originalPosition].getThird() != -1) {
                        searchPrevious = true;
                        position = this.data[originalPosition].getThird();
                    } else {
                        while(this.data[position] != null) position = (position + 1) % this.data.length;
                        break;
                    }
                }
            } else if(searchPrevious) {
                if(this.data[position].getThird() == -1) {
                    while(this.data[position] != null) position = (position + 1) % this.data.length;
                    break;
                }
                else position = this.data[position].getThird();
            } else {
                position = (position + 1) % this.data.length;
                break;
            }
        }

        this.data[position] = new Quin<>(key, datum, lastItemPosition, -1, 0L);
        this.data[lastItemPosition] = this.data[lastItemPosition].withFourth(position);
        this.lastItemPosition = position;
        this.size++;

        while(this.size >= cacheSize) {
            var evictedPosition = this.data[position].getThird();
            var currentSearchPosition = evictedPosition;

            do {
                if(evictCompare(evictedPosition, currentSearchPosition)) evictedPosition = currentSearchPosition;

                currentSearchPosition = this.data[currentSearchPosition].getThird();
            } while(currentSearchPosition != -1);

            var nextDatumPosition = this.data[evictedPosition].getFourth();
            var previousDatumPosition = this.data[evictedPosition].getThird();
            this.data[nextDatumPosition] = this.data[nextDatumPosition].withThird(previousDatumPosition);
            this.data[previousDatumPosition] = this.data[previousDatumPosition].withFourth(nextDatumPosition);
            this.data[evictedPosition] = null;
            this.size--;
        }

        return position;
    }

    protected abstract boolean evictCompare(int evicted, int current);
}
