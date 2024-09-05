package org.inasayaflanderin.abyssine.memory.cache;

import java.io.Serial;

public class LRUCache<K, D> extends RecentlyCache<K, D> {
    @Serial
    private static final long serialVersionUID = -1699712688221614501L;
    private int evictedPosition;

    public LRUCache(int initialCapacity) {
        super(initialCapacity);
        this.evictedPosition = -1;
    }

    public void clear() {
        super.clear();
        this.evictedPosition = -1;
    }

    public int write(K key, D data) {
        var position = super.write(key, data);
        if(this.size == 1) this.evictedPosition = position;

        return position;
    }

    protected void evict(int position) {
        var nextDatumPosition = this.data[this.evictedPosition].getFourth();
        this.data[nextDatumPosition] = this.data[nextDatumPosition].withThird(-1);
        this.data[this.evictedPosition] = null;
        this.evictedPosition = nextDatumPosition;
        this.size--;
    }

    protected void callMove(int position) {
        int previous = this.data[position].getThird();
        int next = this.data[position].getFourth();

        if(previous != -1) this.data[previous] = this.data[previous].withFourth(next);
        else evictedPosition = next;
        if(next != -1) this.data[next] = this.data[previous].withThird(previous);

        this.data[lastItemPosition] = this.data[lastItemPosition].withFourth(position);
        this.data[position] = this.data[position].withThird(lastItemPosition).withFourth(-1);

        this.lastItemPosition = position;
    }

    public void remove(int index) {
        if(this.data[index] == null) return;

        var previous = this.data[index].getThird();
        var next = this.data[index].getFourth();

        if(index == this.evictedPosition) this.evictedPosition = next;
        if(index == this.lastItemPosition) this.lastItemPosition = previous;

        this.data[previous] = this.data[previous].withFourth(next);
        this.data[next] = this.data[next].withThird(previous);
        this.data[index] = null;
        size--;
    }
}