package org.inasayaflanderin.abyssine.miscellaneous.rng;

import java.io.Serial;

public class MLFG extends LFG {
    @Serial
    private static final long serialVersionUID = 9205774482296791133L;

    public MLFG(int firstLagged, int secondLagged) {
        super(firstLagged, secondLagged);
    }

    public MLFG(double seed, int firstLagged, int secondLagged) {
        super(seed, firstLagged, secondLagged);
    }

    public double next() {
        return super.next(multiply);
    }
}
