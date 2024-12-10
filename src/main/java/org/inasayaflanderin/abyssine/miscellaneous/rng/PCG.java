package org.inasayaflanderin.abyssine.miscellaneous.rng;

import java.io.Serial;

public class PCG extends LCG {
    @Serial
    private static final long serialVersionUID = -9126076085543741127L;

    public PCG(long multiplier, long increment, long modulus) {
        super(multiplier, increment, (long) Math.pow(2, modulus));

        if(modulus >= 64) throw new IllegalArgumentException("Modulus must be less than 64");
    }

    public PCG(long seed, long multiplier, long increment, long modulus) {
        super(seed, multiplier, increment, (long) Math.pow(2, modulus));

        if(modulus >= 64) throw new IllegalArgumentException("Modulus must be less than 64");
    }
}
