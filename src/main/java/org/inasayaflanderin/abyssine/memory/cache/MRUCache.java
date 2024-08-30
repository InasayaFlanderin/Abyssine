package org.inasayaflanderin.abyssine.memory.cache;

import org.inasayaflanderin.abyssine.primitives.Quad;

import java.io.Serial;
import java.lang.reflect.Array;
import java.util.Arrays;

public class MRUCache<K, D> implements Caches<K, D, LRUCache<K, D>> {
    @Serial
    private static final long serialVersionUID = -1699712688221614501L;
    private final Quad<K, D, Integer, Integer>[] data;
    private final int cacheSize;
    private int size;
    private int lastItemPosition;

    @SuppressWarnings("unchecked")
    public MRUCache(int initialCapacity) {
        if (initialCapacity < 1) throw new IllegalArgumentException("Expected capacity is less than 0");

        this.cacheSize = initialCapacity;
        this.data = (Quad<K, D, Integer, Integer>[]) Array.newInstance(Quad.class, calculatedDataSize(initialCapacity));
        clear();
        this.lastItemPosition = -1;
    }

    public void write(K key, D datum) {
        if(this.size > this.cacheSize) throw new IllegalStateException("Current number of item is larger than cache's capacity");

        var position = hash(key);
        var originalPosition = position;
        var haveNotSearchNext = true;
        var searchNext = false;
        var searchPrevious = false;
        while(this.data[position] != null) {
            if(this.data[position].equals(key)) {
                this.data[position] = this.data[position].withSecond(datum);
                return;
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

        this.data[position] = new Quad<>(key, datum, lastItemPosition, -1);
        this.data[lastItemPosition] = this.data[lastItemPosition].withFourth(position);
        this.lastItemPosition = position;
        this.size++;

        while(this.size >= cacheSize) {
           var evictedPosition = this.data[position].getThird();
           this.data[position] = this.data[position].withThird(this.data[evictedPosition].getThird());
           this.data[this.data[position].getThird()] = this.data[this.data[position].getThird()].withFourth(position);
           this.data[evictedPosition] = null;
           this.size--;
        }
    }

    public D read(K key) {
        var position = hash(key);

        while(this.data[position] == null) position = (position + 1) % this.data.length;

        var originalPosition = position;

        do {
            if(this.data[position].getFirst().equals(key)) return this.data[position].getSecond();

            position = this.data[position].getFourth();
        } while(position != -1);

        position = originalPosition;

        do {
            if(this.data[position].getFirst().equals(key)) return this.data[position].getSecond();

            position = this.data[position].getThird();
        } while(position != -1);

        return null;
    }

    public void clear() {
        Arrays.fill(this.data, null);
        this.size = 0;
        this.lastItemPosition = -1;
    }

    private int calculatedDataSize(int initialCapacity) {
        var capacity = (int) Math.min(Math.ceil(initialCapacity /  (-0x1.4afd7694c9f95p-62 * initialCapacity * initialCapacity + 0x1.ec58fd3dae086p-31 * initialCapacity + 0.5)), Integer.MAX_VALUE);
        capacity--;
        capacity |= capacity >> 1;
        capacity |= capacity >> 2;
        capacity |= capacity >> 4;
        capacity |= capacity >> 8;
        capacity |= capacity >> 16;

        return capacity;
    }

    private int hash(K key) {
        int result = key.hashCode() * 0x9E3779B9;

        return (result ^ (result >> 16)) & (this.data.length - 1);
    }

    public boolean contains(K key) {
        return read(key) != null;
    }
}