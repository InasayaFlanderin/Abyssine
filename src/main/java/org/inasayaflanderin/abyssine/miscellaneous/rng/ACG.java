package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ACG extends LCG {
    @Serial
    private static final long serialVersionUID = -5223182888290600404L;

    public ACG(long increment, long modulus) {
        super(1, increment, modulus);
    }

    public ACG(long seed, long increment, long modulus) {
        super(seed, 1, increment, modulus);
    }
}