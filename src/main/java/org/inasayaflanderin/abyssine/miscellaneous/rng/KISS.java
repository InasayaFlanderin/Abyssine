package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.util.Collection;

@EqualsAndHashCode @ToString
public class KISS implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = 8712033139982022052L;

    @Getter @Setter
    @EqualsAndHashCode.Exclude private long seed;
    private final RandomGenerators[] rngs;

    public KISS(RandomGenerators... rngs) {
        this(System.currentTimeMillis(), rngs);
    }

    public KISS(Collection<RandomGenerators> rngs) {
        this(System.currentTimeMillis(), rngs.toArray(new RandomGenerators[0]));
    }

    public KISS(long seed, Collection<RandomGenerators> rngs) {
        this(seed, rngs.toArray(new RandomGenerators[0]));
    }

    public KISS(long seed, RandomGenerators... rngs) {
        if(rngs.length == 0) throw new IllegalArgumentException("KISS must contain PRNGs");

        this.seed = seed;
        this.rngs = rngs;
    }

    public double next() {
        double result = -1;

        for(RandomGenerators rng : rngs) {
            rng.setSeed(this.seed);
            result = rng.next();
            this.seed = rng.getSeed();
        }

        if(result < 0) throw new Error("KISS failed to generate a number");

        return result;
    }
}
