package org.inasayaflanderin.abyssine.miscellaneous.rng;

import java.io.Serial;

public class ALFG extends LFG {
    @Serial
    private static final long serialVersionUID = 5230850968311584166L;

    public ALFG(int firstLagged, int secondLagged) {
        super(firstLagged, secondLagged);
    }

    public ALFG(double seed, int firstLagged, int secondLagged) {
        super(seed, firstLagged, secondLagged);
    }

    public double next() {
        return super.next(add);
    }
}
