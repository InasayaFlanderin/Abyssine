package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ALFG extends LFG {
    @Serial
    private static final long serialVersionUID = 5230850968311584166L;

    public ALFG(int firstLagged, int secondLagged) {
        super(firstLagged, secondLagged);
    }

    public ALFG(long seed, int firstLagged, int secondLagged) {
        super(seed, firstLagged, secondLagged);
    }

    public double next() {
        return super.normalize(super.calculate(add));
    }
}