package org.inasayaflanderin.abyssine.memory.cache;

import lombok.ToString;

import java.io.Serial;

@ToString(callSuper = true)
public class MRUCache<K, D> extends RecentlyCache<K, D> {
    @Serial
    private static final long serialVersionUID = -1699712688221614501L;

    MRUCache(int initialCapacity) {
        super(initialCapacity);
    }

    protected void evict(int position) {
        this.lock.lock();
        try {
            var evictedPosition = this.data[position].getThird();
            this.data[position] = this.data[position].withThird(this.data[evictedPosition].getThird());
            this.data[this.data[position].getThird()] = this.data[this.data[position].getThird()].withFourth(position);
            this.data[evictedPosition] = null;
            this.size--;
        } finally {
            this.lock.unlock();
        }
    }

    protected void callMove(int position) {
        this.lock.lock();
        try {
            int previous = this.data[position].getThird();
            int next = this.data[position].getFourth();

            if (previous != -1) this.data[previous] = this.data[previous].withFourth(next);
            if (next != -1) this.data[next] = this.data[previous].withThird(previous);

            this.data[this.lastItemPosition] = this.data[this.lastItemPosition].withFourth(position);
            this.data[position] = this.data[position].withThird(this.lastItemPosition).withFourth(-1);

            this.lastItemPosition = position;
        } finally {
            this.lock.unlock();
        }
    }
}