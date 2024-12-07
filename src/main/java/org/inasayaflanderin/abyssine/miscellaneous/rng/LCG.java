package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter @Setter
public class LCG implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = -4606388376591216708L;

    private long seed;
    private double multiplier;
    private double increment;
    private double modulus;

    public LCG(double multiplier, double increment, double modulus) {
        this(System.currentTimeMillis(), multiplier, increment, modulus);
    }

    public LCG(long seed, double multiplier, double increment, double modulus) {
        this.seed = seed;
        this.multiplier = multiplier;
        this.increment = increment;
        this.modulus = modulus;
    }

    public double next() {
        this.seed = (long) Math.abs((this.multiplier * this.seed + this.increment) % this.modulus);
        return (double) this.seed / this.modulus;
    }
 }
