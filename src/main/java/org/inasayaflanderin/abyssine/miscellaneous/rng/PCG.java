package org.inasayaflanderin.abyssine.miscellaneous.rng;

import java.io.Serial;

public class PCG extends LCG {
    @Serial
    private static final long serialVersionUID = -9126076085543741127L;

    public PCG(double multiplier, double increment, double modulus) {
        super(multiplier, increment, Math.pow(2, modulus));

        if(modulus >= 1024) throw new IllegalArgumentException("Modulus must be less than 1024");
    }

    public PCG(double seed, double multiplier, double increment, double modulus) {
        super(seed, multiplier, increment, Math.pow(2, modulus));

        if(modulus >= 1024) throw new IllegalArgumentException("Modulus must be less than 1024");
    }
}
