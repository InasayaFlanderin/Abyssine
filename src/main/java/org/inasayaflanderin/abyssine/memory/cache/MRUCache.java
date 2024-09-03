package org.inasayaflanderin.abyssine.memory.cache;

import java.io.Serial;

public class MRUCache<K, D> extends RecentlyCache<K, D> {
    @Serial
    private static final long serialVersionUID = -1699712688221614501L;

    public MRUCache(int initialCapacity) {
        super(initialCapacity);
    }

    protected void evict(int position) {
        var evictedPosition = this.data[position].getThird();
        this.data[position] = this.data[position].withThird(this.data[evictedPosition].getThird());
        this.data[this.data[position].getThird()] = this.data[this.data[position].getThird()].withFourth(position);
        this.data[evictedPosition] = null;
        this.size--;
    }

    protected void callMove(int position) {
        int previous = this.data[position].getThird();
        int next = this.data[position].getFourth();

        if(previous != -1) this.data[previous] = this.data[previous].withFourth(next);
        if(next != -1) this.data[next] = this.data[previous].withThird(previous);

        this.data[lastItemPosition] = this.data[lastItemPosition].withFourth(position);
        this.data[position] = this.data[position].withThird(lastItemPosition);

        lastItemPosition = position;
    }
}