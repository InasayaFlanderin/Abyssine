package org.inasayaflanderin.abyssine.miscellaneous.rng;

public class PCG extends LCG {
    public PCG(double multiplier, double increment, double modulus) {
        super(multiplier, increment, Math.pow(2, modulus));

        if(modulus >= 1024) throw new IllegalArgumentException("Modulus must be less than 1024");
    }

    public PCG(double seed, double multiplier, double increment, double modulus) {
        super(seed, multiplier, increment, Math.pow(2, modulus));

        if(modulus >= 1024) throw new IllegalArgumentException("Modulus must be less than 1024");
    }
}
