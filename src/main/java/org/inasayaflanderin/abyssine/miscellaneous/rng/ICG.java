package org.inasayaflanderin.abyssine.miscellaneous.rng;

import java.io.Serial;

public class ICG extends LCG {
    @Serial
    private static final long serialVersionUID = -7929973483726597265L;

    public ICG(double multiplier, double increment, double modulus) {
        super(multiplier, increment, modulus);

        if(increment == 0) throw new IllegalArgumentException("Increment must not be 0");
    }

    public ICG(double seed, double multiplier, double increment, double modulus) {
        super(seed, multiplier, increment, modulus);

        if(increment == 0) throw new IllegalArgumentException("Increment must not be 0");
    }

    public double next() {
        if(this.getSeed() == 0) {
            this.setSeed(this.getIncrement());

            return this.getSeed();
        }

        this.setSeed(1 / this.getSeed());

        return super.next();
    }
}
