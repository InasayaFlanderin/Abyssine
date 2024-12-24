package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.math.BigDecimal;

@Getter @EqualsAndHashCode(callSuper = true) @ToString(callSuper = true)
public class MECG extends LCG {
    @Serial
    private static final long serialVersionUID = 8375653722788522189L;

    private long power;

    public MECG(long multiplier, long increment, long modulus, long power) {
        super(multiplier, increment, modulus);

        if(power <= 0) throw new IllegalArgumentException("Power must be larger than 0");

        this.power = power;
    }

    public MECG(long seed, long multiplier, long increment, long modulus, long power) {
        super(seed, multiplier, increment, modulus);

        if(power <= 0) throw new IllegalArgumentException("Power must be larger than 0");

        this.power = power;
    }

    public double next() {
        var seed = BigDecimal.valueOf(this.getSeed());
        var localPow = this.power;

        while(localPow != 0) {
            if(localPow < Integer.MAX_VALUE) {
                seed = seed.pow((int) localPow);
                localPow = 0;
            } else {
                seed = seed.pow(Integer.MAX_VALUE);
                localPow -= Integer.MAX_VALUE;
            }
        }

        super.setSeed(seed.longValue());

        return super.next();
    }

    public void setPower(long newPower) {
        if(newPower <= 0) throw new IllegalArgumentException("Power must be larger than 0");

        this.power = newPower;
    }
}
