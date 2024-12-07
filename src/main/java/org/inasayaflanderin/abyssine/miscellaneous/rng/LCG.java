package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter @Setter
public class LCG implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = -4606388376591216708L;

    private double seed;
    private double multiplier;
    private double increment;
    private double modulus;

    public LCG(double multiplier, double increment, double modulus) {
        this(System.currentTimeMillis(), multiplier, increment, modulus);
    }

    public LCG(double seed, double multiplier, double increment, double modulus) {
        this.seed = seed;
        this.multiplier = multiplier;
        this.increment = increment;
        this.modulus = modulus;
    }

    public double next() {
        this.seed = Math.abs(this.multiplier * this.seed + this.increment);

        if(Double.isInfinite(this.seed)) this.seed = Math.abs(System.currentTimeMillis());

        this.seed %= this.modulus;

        return this.seed / this.modulus;
    }
 }
