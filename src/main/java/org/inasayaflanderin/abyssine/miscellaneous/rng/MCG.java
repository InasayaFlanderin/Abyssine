package org.inasayaflanderin.abyssine.miscellaneous.rng;

import java.io.Serial;

public class MCG extends LCG {
    @Serial
    private static final long serialVersionUID = 7837947318768033003L;

    public MCG(long multiplier, long modulus) {
        super(multiplier, 0, modulus);
    }

    public MCG(long seed, long multiplier, long modulus) {
        super(seed, multiplier, 0, modulus);
    }
}
