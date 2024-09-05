package org.inasayaflanderin.abyssine.memory.cache;

import org.inasayaflanderin.abyssine.primitives.Quin;

import java.io.Serial;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;

public class ARCache<K, D> extends AbstractCache<K, D> {
    @Serial
    private static final long serialVersionUID = 8311810786177027895L;
    private final Quin<K, D, Integer, Integer, Long>[] data;
    private final LinkedList<K> recentlyGarbageCollection;
    private final LinkedList<K> frequencyGarbageCollection;
    private final int cacheSize;
    private int size;
    private int evictedPosition;
    private int equalizer;
    private final int factor;
    private int totalFrequency;
    private int recentlyLastItemPosition;
    private int frequencyLastItemPosition;

    @SuppressWarnings("unchecked")
    public ARCache(int initialCapacity) {
        if (initialCapacity < 1) throw new IllegalArgumentException("Expected capacity is less than 0");

        this.cacheSize = initialCapacity;
        var dataSize = calculatedDataSize(initialCapacity);
        this.data = (Quin<K, D, Integer, Integer, Long>[]) Array.newInstance(Quin.class, dataSize);
        clear();
        this.factor = (int) Math.ceil((double) initialCapacity / (double) dataSize);
        this.recentlyGarbageCollection = new LinkedList<>();
        this.frequencyGarbageCollection = new LinkedList<>();
        this.totalFrequency = 0;
    }

    public int indexOf(K key) {
        if(this.size == 0) return -1;

        var position = indexOfSearch(key, this.equalizer, 0);
        if(position != -1) return position;

        return indexOfSearch(key, this.data.length - this.equalizer, this.equalizer);
    }

    public int write(K key, D data) {
        if(this.size > this.cacheSize) throw new IllegalStateException("Current number of item is larger than cache's capacity");

        var position = indexOf(key);
        if(position < 0) {
            if(recentlyGarbageCollection.contains(key)) {
                this.equalizer += this.factor;
                this.recentlyGarbageCollection.remove(key);
            }

            if(frequencyGarbageCollection.contains(key)) {
                this.equalizer -= this.factor;
                this.frequencyGarbageCollection.remove(key);
            }

            position = hash(key, this.equalizer);

            while(this.data[position] != null) position = (position + 1) & (this.equalizer - 1);
        } else if(position < this.equalizer) {
            Quin<K, D, Integer, Integer, Long> datum = this.data[position].withSecond(data);
            var newFrequency = datum.getFifth() + 1;
            this.totalFrequency++;

            if(newFrequency > totalFrequency / this.size) {
                remove(position);

                if(position == this.evictedPosition) this.evictedPosition = datum.getFourth();
                if(position == this.recentlyLastItemPosition) this.recentlyLastItemPosition = datum.getThird();

                var newPosition = hash(key, this.data.length - this.equalizer) + this.equalizer;
                this.data[newPosition] = datum.withFifth(newFrequency).withThird(frequencyLastItemPosition).withFourth(-1);
                this.size++;
                this.frequencyLastItemPosition = newPosition;

                return newPosition;
            } else {
                this.data[position] = datum.withFifth(newFrequency);
                callMove(position);

                return position;
            }
        } else {
            this.data[position] = this.data[position].withSecond(data).withFifth(this.data[position].getFifth() + 1);

            return position;
        }

        this.data[position] = new Quin<>(key, data, this.recentlyLastItemPosition, -1, 0L);
        this.data[this.recentlyLastItemPosition] = this.data[this.recentlyLastItemPosition].withFourth(position);
        this.recentlyLastItemPosition = position;
        this.size++;

        while(this.size >= this.cacheSize) {
            if(this.recentlyGarbageCollection.size() > this.frequencyGarbageCollection.size()) {
                var evictedPosition = this.data[this.frequencyLastItemPosition].getThird();
                var currentSearchPosition = evictedPosition;

                do {
                    if(this.data[currentSearchPosition].getFifth() <= this.data[evictedPosition].getFifth()) evictedPosition = currentSearchPosition;

                    currentSearchPosition = this.data[currentSearchPosition].getThird();
                } while(currentSearchPosition != -1);

                this.frequencyGarbageCollection.add(key);
                remove(evictedPosition);
            } else {
                this.recentlyGarbageCollection.add(key);
                remove(this.evictedPosition);
            }
        }

        while(this.recentlyGarbageCollection.size() + this.frequencyGarbageCollection.size() >= this.data.length) {
            if(this.recentlyGarbageCollection.size() > this.frequencyGarbageCollection.size()) this.recentlyGarbageCollection.removeFirst();
            else this.frequencyGarbageCollection.removeFirst();
        }

        return position;
    }

    public D read(K key) {
        var position = indexOf(key);

        if(position >= 0) {
            if(position < this.equalizer) {
                Quin<K, D, Integer, Integer, Long> datum = this.data[position];
                var newFrequency = datum.getFifth() + 1;
                this.totalFrequency++;

                if(newFrequency > totalFrequency / this.size) {
                    remove(position);

                    if(position == this.evictedPosition) this.evictedPosition = datum.getFourth();
                    if(position == this.recentlyLastItemPosition) this.recentlyLastItemPosition = datum.getThird();

                    var newPosition = hash(key, this.data.length - this.equalizer) + this.equalizer;
                    this.data[newPosition] = datum.withFifth(newFrequency).withThird(frequencyLastItemPosition).withFourth(-1);
                    this.size++;
                    this.frequencyLastItemPosition = newPosition;
                } else {
                    this.data[position] = datum.withFifth(newFrequency);
                    callMove(position);
                }
            }

            return this.data[position].getSecond();
        }

        return null;
    }

    public void remove(int index) {
        if(this.data[index] == null) return;

        var previous = this.data[index].getThird();
        var next = this.data[index].getFourth();

        if(index == this.evictedPosition) this.evictedPosition = next;
        if(index == this.frequencyLastItemPosition) this.frequencyLastItemPosition = previous;
        if(index == this.recentlyLastItemPosition) this.recentlyLastItemPosition = previous;

        this.totalFrequency -= this.data[index].getFifth();
        this.data[previous] = this.data[previous].withFourth(next);
        this.data[next] = this.data[next].withThird(previous);
        this.data[index] = null;
        size--;
    }

    public void remove(K key) {
        remove(indexOf(key));
    }

    public void clear() {
        Arrays.fill(this.data, null);
        this.size = 0;
        this.equalizer = this.data.length / 2;
        this.evictedPosition = -1;
        this.recentlyLastItemPosition = -1;
        this.frequencyLastItemPosition = -1;
    }

    private int indexOfSearch(K key, int length, int startPoint) {
        if(this.size == 0) return -1;

        var position = hash(key, length);
        var originalPosition = position;

        while(this.data[position] == null) {
            position = startPoint + (position + 1) & (length - 1);

            if(position == originalPosition) return -1;
        }

        originalPosition = position;

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

    protected void callMove(int position) {
        int previous = this.data[position].getThird();
        int next = this.data[position].getFourth();

        if(previous != -1) this.data[previous] = this.data[previous].withFourth(next);
        else evictedPosition = next;
        if(next != -1) this.data[next] = this.data[previous].withThird(previous);

        this.data[this.recentlyLastItemPosition] = this.data[this.recentlyLastItemPosition].withFourth(position);
        this.data[position] = this.data[position].withThird(this.recentlyLastItemPosition);

        this.recentlyLastItemPosition = position;
    }
}