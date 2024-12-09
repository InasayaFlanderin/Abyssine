package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter @Setter
public class LFSR implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = 4605933907182896612L;

    private long seed;

    public LFSR() {
        this(System.currentTimeMillis());
    }

    public LFSR(long seed) {
        this.seed = seed;
    }

    public double next() {
        this.seed ^= 0xD800000000000000L;
        this.seed = Math.abs((this.seed >> 1) | (this.seed << 63));

        if(this.seed == 0) this.seed = Math.abs(System.currentTimeMillis());

        return this.seed / (this.seed % 10 == 0 ? this.seed : Math.pow(10, Math.ceil(Math.log10(this.seed))));
    }
}
