package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AWC extends LFG {
    @Serial
    private static final long serialVersionUID = -8664681395310709273L;

    private long modulus;
    @Setter
    private long increment;

    public AWC(int firstLagged, int secondLagged, long increment, long modulus) {
        super(firstLagged, secondLagged);

        if(modulus <= 0) throw new IllegalArgumentException("Modulus must be larger than 0");

        this.modulus = modulus;
        this.increment = increment;
    }

    public AWC(long seed, int firstLagged, int secondLagged, long increment, long modulus) {
        super(seed, firstLagged, secondLagged);

        if(modulus <= 0) throw new IllegalArgumentException("Modulus must be larger than 0");

        this.modulus = modulus;
        this.increment = increment;
    }

    public double next() {
        var tempResult = calculate(add) + this.increment;
        var result = Math.abs(tempResult % this.modulus);

        this.increment += tempResult;
        this.increment %= this.modulus;
        this.lagQueue.removeLast();
        this.lagQueue.add(result);

        return (double) result / this.modulus;
    }

    public void setModulus(long modulus) {
        if(modulus <= 0) throw new IllegalArgumentException("Modulus must be larger than 0");

        this.modulus = modulus;
    }
}