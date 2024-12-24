package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@EqualsAndHashCode @ToString
public class MSWS implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = -2434213597478164745L;

    private static final long s = 0xb5ad4eceda1ce2a9L;

    @Getter @Setter
    @EqualsAndHashCode.Exclude private long seed;
    @EqualsAndHashCode.Exclude @ToString.Exclude private long w;

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
