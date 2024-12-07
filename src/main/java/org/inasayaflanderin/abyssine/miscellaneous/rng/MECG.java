package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.Getter;

import java.io.Serial;
import java.math.BigInteger;

@Getter
public class MECG extends LCG {
    @Serial
    private static final long serialVersionUID = 8375653722788522189L;

    private long power;

    public MECG(double multiplier, double increment, double modulus, long power) {
        super(multiplier, increment, modulus);

        if(power <= 0) throw new IllegalArgumentException("Power must be larger than 0");

        this.power = power;
    }

    public MECG(long seed, double multiplier, double increment, double modulus, int power) {
        super(seed, multiplier, increment, modulus);

        if(power <= 0) throw new IllegalArgumentException("Power must be larger than 0");

        this.power = power;
    }

    public double next() {
        var seed = BigInteger.valueOf(this.getSeed());
        var localPow = this.power;

        while(localPow != 0) {
            if(localPow < Integer.MAX_VALUE) seed = seed.pow((int) localPow);
            else {
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
