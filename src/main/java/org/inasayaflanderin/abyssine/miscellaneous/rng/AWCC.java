package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.Getter;

import java.io.Serial;

@Getter
public class AWCC extends LFG {
    @Serial
    private static final long serialVersionUID = -3097602726304469484L;

    private double modulus;
    private double increment;

    public AWCC(int firstLagged, int secondLagged, double increment, double modulus) {
        super(firstLagged, secondLagged);

        if(modulus <= 0) throw new IllegalArgumentException("Modulus must be larger than 0");
        if(Double.isInfinite(increment)) throw new IllegalArgumentException("Increment must not be infinite");
        if(Double.isInfinite(modulus)) throw new IllegalArgumentException("Modulus must not be infinite");

        this.modulus = modulus;
        this.increment = increment;
    }

    public AWCC(double seed, int firstLagged, int secondLagged, double increment, double modulus) {
        super(seed, firstLagged, secondLagged);

        if(modulus <= 0) throw new IllegalArgumentException("Modulus must be larger than 0");
        if(Double.isInfinite(increment)) throw new IllegalArgumentException("Increment must not be infinite");
        if(Double.isInfinite(modulus)) throw new IllegalArgumentException("Modulus must not be infinite");

        this.modulus = modulus;
        this.increment = increment;
    }

    public double next() {
        var tempResult = calculate(add) + this.increment;
        var result = Math.abs((2 * this.modulus - 1 - tempResult) % this.modulus);

        this.increment += tempResult;
        this.increment %= this.modulus;
        this.lagQueue.removeLast();
        this.lagQueue.add(result);

        return result / this.modulus;
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
