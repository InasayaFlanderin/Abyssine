package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@Getter @Setter @EqualsAndHashCode @ToString
public class MS implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = 9174087803386901937L;

    @EqualsAndHashCode.Exclude private long seed;

    public MS() {
        this(System.currentTimeMillis());
    }

    public MS(long seed) {
        this.seed = seed;
    }

    public double next() {
        this.seed *= this.seed;
        this.seed = (this.seed >> 32) | (this.seed << 32);

        if(this.seed == 0 || this.seed == 1) this.seed = System.currentTimeMillis();

        this.seed = Math.abs(this.seed);

        return (double) this.seed / Long.MAX_VALUE;
    }
}
