package org.inasayaflanderin.abyssine.miscellaneous.rng;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@Setter
@Getter
@EqualsAndHashCode
@ToString
public class WH implements RandomGenerators {
    @Serial
    private static final long serialVersionUID = 4285723705878801473L;

    @EqualsAndHashCode.Exclude
    private long seed;
    private static final LCG first = new LCG(171, 0, 30269);
    private static final LCG second = new LCG(172, 0, 30307);
    private static final LCG third = new LCG(170, 0, 30323);

    public WH() {
        this(System.currentTimeMillis());
    }

    public WH(long seed) {
        this.seed = seed;
    }

    public double next() {
        first.setSeed(this.seed);
        double s1 = first.next();
        second.setSeed(first.getSeed());
        double s2 = second.next();
        third.setSeed(second.getSeed());
        double s3 = third.next();
        this.seed = third.getSeed();

        return (s1 + s2 + s3) % 1;
    }
}