package org.inasayaflanderin.abyssine.miscellaneous.rng;

import java.io.Serial;

public class MWCC extends LMWCC {
    @Serial
    private static final long serialVersionUID = -2359415121569211524L;

    public MWCC(long multiplier, long increment, long modulus) {
        super(multiplier, increment, modulus, 1);
    }

    public MWCC(long seed, long multiplier, long increment, long modulus) {
        super(seed, multiplier, increment, modulus, 1);
    }
}
