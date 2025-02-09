package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class LFSR implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = 4605933907182896612L;

    @EqualsAndHashCode.Exclude
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

        return (double) this.seed / Long.MAX_VALUE;
    }
}