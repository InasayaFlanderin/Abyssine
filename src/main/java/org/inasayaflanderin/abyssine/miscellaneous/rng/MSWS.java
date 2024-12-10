package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

public class MSWS implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = -2434213597478164745L;

    @Getter @Setter
    private long seed;
    private long w;
    private static final long s = 0xb5ad4eceda1ce2a9L;

    public MSWS() {
        this(System.currentTimeMillis());
    }

    public MSWS(long seed) {
        this.seed = seed;
        this.w = 0;
    }

    public double next() {
        this.seed *= this.seed;
        this.seed += (this.w += s);
        this.seed = (this.seed >> 32) | (this.seed << 32);
        this.seed = Math.abs(this.seed);

        return (double) this.seed / Long.MAX_VALUE;
    }
}
