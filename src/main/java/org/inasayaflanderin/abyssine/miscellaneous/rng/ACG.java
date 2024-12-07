package org.inasayaflanderin.abyssine.miscellaneous.rng;

import java.io.Serial;

public class ACG extends LCG {
    @Serial
    private static final long serialVersionUID = -5223182888290600404L;

    public ACG(double increment, double modulus) {
        super(1, increment, modulus);
    }

    public ACG(long seed, double increment, double modulus) {
        super(seed, 1, increment, modulus);
    }
}
