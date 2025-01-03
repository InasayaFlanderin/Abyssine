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
public class XORShiftS implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = 5646140063188153325L;

    @EqualsAndHashCode.Exclude
    private long seed;

    public XORShiftS() {
        this(System.currentTimeMillis());
    }

    public XORShiftS(long seed) {
        this.seed = seed;
    }

    public double next() {
        this.seed = Math.abs(this.seed);
        this.seed ^= (this.seed << 12);
        this.seed ^= (this.seed >>> 25);
        this.seed ^= (seed << 27);
        this.seed *= 0x2545F4914F6CDD1DL;
        this.seed = Math.abs(this.seed);

        return (double) this.seed / Long.MAX_VALUE;
    }
}