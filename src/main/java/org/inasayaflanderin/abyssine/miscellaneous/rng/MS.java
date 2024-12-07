package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter @Setter
public class MS implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = 9174087803386901937L;

    private long seed;

    public MS() {
        this(System.currentTimeMillis());
    }

    public MS(long seed) {
        this.seed = seed;
    }

    public double next() {
        this.seed *= this.seed;
        String sResult = Long.toString(this.seed);
        int index = (sResult.length() - 15) / 2;
        this.seed = sResult.length() <= 15 ? Math.abs(this.seed) : Long.parseLong(sResult.substring(index, index + 15));

        if(this.seed == 0 || this.seed == 1) this.seed = System.currentTimeMillis(); // prevent seed fail

        return (double) this.seed / (this.seed % 10 == 0 ? this.seed : Math.pow(10, Math.ceil(Math.log10(this.seed))));
    }
}
