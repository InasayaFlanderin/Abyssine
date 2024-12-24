package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true) @ToString(callSuper = true)
public class SWB extends LFG {
    @Serial
    private static final long serialVersionUID = -3192449302063185263L;

    private long modulus;
    @Setter
    private long increment;

    public SWB(int firstLagged, int secondLagged, long increment, long modulus) {
        super(firstLagged, secondLagged);

        if(modulus <= 0) throw new IllegalArgumentException("Modulus must be larger than 0");

        this.modulus = modulus;
        this.increment = increment;
    }

    public SWB(long seed, int firstLagged, int secondLagged, long increment, long modulus) {
        super(seed, firstLagged, secondLagged);

        if(modulus <= 0) throw new IllegalArgumentException("Modulus must be larger than 0");

        this.modulus = modulus;
        this.increment = increment;
    }

    public double next() {
        var tempResult = calculate(subtract) - this.increment;
        var result = Math.abs(tempResult % this.modulus);

        this.increment -= tempResult;
        this.increment %= this.modulus;
        this.lagQueue.removeLast();
        this.lagQueue.add(result);

        if(result == 0) {
            setSeed(System.currentTimeMillis());

            return next();
        }

        return (double) result / this.modulus;
    }

    public void setModulus(long modulus) {
        if(modulus <= 0) throw new IllegalArgumentException("Modulus must be larger than 0");

        this.modulus = modulus;
    }
}
