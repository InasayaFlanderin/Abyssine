package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

public class CWG implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = 5309861199187848563L;

    @Getter @Setter
    private long seed;
    private long a = 0, w = 0;
    private static final long s = 0xb5ad4eceda1ce2a9L;

    public CWG() {
        this(System.currentTimeMillis());
    }

    public CWG(long seed) {
        this.seed = seed;
    }

    public double next() {
        this.seed = Math.abs(this.seed);
        this.seed = (this.seed >> 1) * ((a += this.seed) | 1) ^ (w += s);
        this.seed = Math.abs(this.seed);

        return (double) this.seed / Long.MAX_VALUE;
    }
}
