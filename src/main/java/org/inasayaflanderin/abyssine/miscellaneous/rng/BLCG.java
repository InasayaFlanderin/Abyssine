package org.inasayaflanderin.abyssine.miscellaneous.rng;

import java.io.Serial;

public class BLCG extends LCG {
    @Serial
    private static final long serialVersionUID = -241714643502130085L;

    public BLCG(double multiplier, double increment, double modulus) {
        super(multiplier, increment, modulus);
    }

    public BLCG(long seed, double multiplier, double increment, double modulus) {
        super(seed, multiplier, increment, modulus);
    }

    public double next() {
        this.setSeed(Math.abs((long) (this.getMultiplier() * this.getSeed() + this.getIncrement()) ^ (long) this.getModulus()));

        return (double) this.getSeed() / this.getModulus();
    }
}
