package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.Getter;

import java.io.Serial;
import java.util.LinkedList;

public class MT implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = -5587861316672676165L;

    private final LinkedList<Long> seed;
    @Getter
    private int state;
    private int index;

    public MT(int state) {
        this(System.currentTimeMillis(), state);
    }

    public MT(long seed, int state) {
        if(state <= 0) throw new IllegalArgumentException("State must be greater than 0");
        this.seed = new LinkedList<>();
        this.state = state;
        this.index = 0;
        initialSeed(seed);
    }

    public double next() {
        if(this.index >= this.state) {
            if(this.index == this.state + 1) initialSeed(System.currentTimeMillis());

            for (int i = 0; i < this.state; i++) {
                var result = (this.seed.get(i) & 0xFFFFFFFF80000000L) + (this.seed.get((i + 1) % this.state) & 0x7FFFFFFFL);
                var resultMatrix = result >> 1;
                if ((result & 1) != 0) resultMatrix ^= 0xB5026F5AA96619E9L;
                this.seed.set(i, this.seed.get((i + (this.state / 2)) % this.state) ^ resultMatrix);
            }

            this.index = 0;
        }

        long result = seed.get(index++);
        result ^= (result >> 29) & 0x5555555555555555L;
        result ^= (result << 17) & 0x71D67FFFEDA60000L;
        result ^= (result << 37) & 0xFFF7EEE000000000L;
        result ^= (result >> 43);

        return (double) result / (result % 10 == 0 ? result : Math.pow(10, Math.ceil(Math.log10(result))));
    }

    public long getSeed() {
        return this.seed.getFirst();
    }

    public void setSeed(long seed) {
        initialSeed(seed);
    }

    public void setState(int state) {
        if(state <= 0) throw new IllegalArgumentException("State must be greater than 0");
        this.state = state;

        while(this.seed.size() < this.state) this.seed.add(0x5851F42D4C957F2DL * (this.seed.getLast() | (this.seed.getLast() >> 62)) + this.seed.size());
        while(this.state < this.seed.size()) this.seed.removeLast();
    }

    private void initialSeed(long seed) {
        this.seed.clear();

        this.seed.add(seed);
        while(this.seed.size() < this.state) this.seed.add(0x5851F42D4C957F2DL * (this.seed.getLast() | (this.seed.getLast() >> 62)) + this.seed.size());
    }
}
