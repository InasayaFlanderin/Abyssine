package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter @Setter
public class XORShift implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = 5646140063188153325L;

    private long seed;

    public XORShift() {
        this(System.currentTimeMillis());
    }

    public XORShift(long seed) {
        this.seed = seed;
    }

    public double next() {
        this.seed = Math.abs(this.seed);
        this.seed ^= (this.seed << 21);
        this.seed ^= (this.seed >>> 35);
        this.seed ^= (seed << 4);
        this.seed = Math.abs(this.seed);

        return (double) this.seed / (this.seed % 10 == 0 ? this.seed : Math.pow(10, Math.ceil(Math.log10(this.seed))));
    }
}
