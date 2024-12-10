package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
public class LCG implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = -4606388376591216708L;

    @Setter
    private long seed;
    @Setter
    private long multiplier;
    @Setter
    private long increment;
    private long modulus;

    public LCG(long multiplier, long increment, long modulus) {
        this(System.currentTimeMillis(), multiplier, increment, modulus);
    }

    public LCG(long seed, long multiplier, long increment, long modulus) {
        if(modulus <= 0) throw new IllegalArgumentException("Modulus must be larger than 0");

        this.seed = seed;
        this.multiplier = multiplier;
        this.increment = increment;
        this.modulus = modulus;
    }

    public double next() {
        this.seed = Math.abs(this.multiplier * this.seed + this.increment);

        this.seed %= this.modulus;

        return (double) this.seed / this.modulus;
    }

    public void setModulus(long modulus) {
        if(modulus <= 0) throw new IllegalArgumentException("Modulus must be larger than 0");

        this.modulus = modulus;
    }
}
