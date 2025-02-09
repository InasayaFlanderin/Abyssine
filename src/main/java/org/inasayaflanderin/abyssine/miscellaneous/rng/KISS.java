package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode
@ToString
public class KISS implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = 8712033139982022052L;

    @Getter
    @Setter
    @EqualsAndHashCode.Exclude
    private long seed;
    private final List<RandomGenerators> rngs;

    public KISS(RandomGenerators... rngs) {
        this(System.currentTimeMillis(), Arrays.asList(rngs));
    }

    public KISS(Collection<RandomGenerators> rngs) {
        this(System.currentTimeMillis(), new ArrayList<>(rngs));
    }

    public KISS(long seed, Collection<RandomGenerators> rngs) {
        this(seed, new ArrayList<>(rngs));
    }

    public KISS(long seed, RandomGenerators... rngs) {
        this(seed, Arrays.asList(rngs));
    }

    private KISS(long seed, List<RandomGenerators> rngs) {
        if(rngs.isEmpty()) throw new IllegalArgumentException("KISS must contain PRNGs");

        this.seed = seed;
        this.rngs = rngs;
    }

    public double next() {
        return rngs.stream().mapToDouble(rng -> {
            rng.setSeed(this.seed);
            var result = rng.next();
            this.seed = rng.getSeed();

            return result;
        }).reduce((first, second) -> second)
                .orElseThrow(() -> new RuntimeException("RNGs failed to generate a number"));
    }
}