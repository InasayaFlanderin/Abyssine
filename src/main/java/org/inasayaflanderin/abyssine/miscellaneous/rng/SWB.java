package org.inasayaflanderin.abyssine.miscellaneous.rng;

import java.io.Serial;

public class SWB extends LFG{
    @Serial
    private static final long serialVersionUID = -3192449302063185263L;

    private double modulus;
    private double increment;

    public SWB(int firstLagged, int secondLagged, double increment, double modulus) {
        super(firstLagged, secondLagged);

        if(modulus <= 0) throw new IllegalArgumentException("Modulus must be larger than 0");
        if(Double.isInfinite(increment)) throw new IllegalArgumentException("Increment must not be infinite");
        if(Double.isInfinite(modulus)) throw new IllegalArgumentException("Modulus must not be infinite");

        this.modulus = modulus;
        this.increment = increment;
    }

    public SWB(double seed, int firstLagged, int secondLagged, double increment, double modulus) {
        super(seed, firstLagged, secondLagged);

        if(modulus <= 0) throw new IllegalArgumentException("Modulus must be larger than 0");
        if(Double.isInfinite(increment)) throw new IllegalArgumentException("Increment must not be infinite");
        if(Double.isInfinite(modulus)) throw new IllegalArgumentException("Modulus must not be infinite");

        this.modulus = modulus;
        this.increment = increment;
    }

    public double next() {
        var tempResult = calculate(subtract) - this.increment;
        var result = Math.abs(tempResult % this.modulus);

        this.increment -= tempResult;
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
