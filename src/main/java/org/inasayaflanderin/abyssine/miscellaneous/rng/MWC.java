package org.inasayaflanderin.abyssine.miscellaneous.rng;

import java.io.Serial;

public class MWC extends LMWC {
    @Serial
    private static final long serialVersionUID = 617819207934434494L;

    public MWC(long multiplier, long increment, long modulus) {
        super(multiplier, increment, modulus, 1);
    }

    public MWC(long seed, long multiplier, long increment, long modulus) {
        super(seed, multiplier, increment, modulus, 1);
    }
}
