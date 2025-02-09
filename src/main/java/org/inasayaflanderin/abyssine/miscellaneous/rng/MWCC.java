package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
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