package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
public class LCG implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = -4606388376591216708L;

    @Setter
    private double seed;
    private double multiplier;
    private double increment;
    private double modulus;

    public LCG(double multiplier, double increment, double modulus) {
        this(System.currentTimeMillis(), multiplier, increment, modulus);
    }

    public LCG(double seed, double multiplier, double increment, double modulus) {
        if(modulus <= 0) throw new IllegalArgumentException("Modulus must be larger than 0");
        if(Double.isInfinite(multiplier)) throw new IllegalArgumentException("Multiplier must not be infinite");
        if(Double.isInfinite(increment)) throw new IllegalArgumentException("Increment must not be infinite");
        if(Double.isInfinite(modulus)) throw new IllegalArgumentException("Modulus must not be infinite");

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

    public void setMultiplier(double multiplier) {
        if(Double.isInfinite(multiplier)) throw new IllegalArgumentException("Multiplier must not be infinite");

        this.multiplier = multiplier;
    }

    public void setIncrement(double increment) {
        if(Double.isInfinite(increment)) throw new IllegalArgumentException("Increment must not be infinite");

        this.increment = increment;
    }

    public void setModulus(double modulus) {
        if(modulus <= 0) throw new IllegalArgumentException("Modulus must be larger than 0");
        if(Double.isInfinite(modulus)) throw new IllegalArgumentException("Modulus must not be infinite");

        this.modulus = modulus;
    }
}
