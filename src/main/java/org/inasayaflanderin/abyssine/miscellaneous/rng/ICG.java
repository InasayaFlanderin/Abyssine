package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ICG extends LCG {
    @Serial
    private static final long serialVersionUID = -7929973483726597265L;

    public ICG(long multiplier, long increment, long modulus) {
        super(multiplier, increment, modulus);

        if(increment == 0) throw new IllegalArgumentException("Increment must not be 0");
    }

    public ICG(long seed, long multiplier, long increment, long modulus) {
        super(seed, multiplier, increment, modulus);

        if(increment == 0) throw new IllegalArgumentException("Increment must not be 0");
    }

    public double next() {
        if(this.getSeed() == 0) {
            this.setSeed(this.getIncrement());

            return this.getSeed();
        }

        this.setSeed((long) ((double) 1 / this.getSeed() * Long.MAX_VALUE));

        return super.next();
    }
}