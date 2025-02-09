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
public class BLCG implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = -241714643502130085L;

    @EqualsAndHashCode.Exclude
    private long seed;
    private long multiplier;
    private long increment;
    private long modulus;

    public BLCG(long multiplier, long increment, long modulus) {
        this(System.currentTimeMillis(), multiplier, increment, modulus);
    }

    public BLCG(long seed, long multiplier, long increment, long modulus) {
        if(modulus <= 0) throw new IllegalArgumentException("Modulus must be larger than 0");

        this.seed = seed;
        this.multiplier = multiplier;
        this.increment = increment;
        this.modulus = modulus;
    }

    public double next() {
        this.seed = Math.abs(this.multiplier * this.seed + this.increment) ^ this.modulus;

        if(this.seed == Long.MAX_VALUE) this.seed = Math.abs(System.currentTimeMillis());

        this.seed %= this.modulus;

        return (double) this.seed / this.modulus;
    }
}