package org.inasayaflanderin.abyssine.miscellaneous.rng;


import java.io.Serial;

public class LMWCC extends LMWC {
    @Serial
    private static final long serialVersionUID = 867976142157747322L;

    public LMWCC(long multiplier, long increment, long modulus, int lag) {
        super(multiplier, increment, modulus, lag);
    }

    public LMWCC(long seed, long multiplier, long increment, long modulus, int lag) {
        super(seed, multiplier, increment, modulus, lag);
    }

    public double next() {
        var result = 2 * getModulus() - 1 - super.next() * getModulus();
        result %= getModulus();

        lagQueue.set(getLag() - 1, (long) result);
        setIncrement((long) result);

        return result / getModulus();
    }
}
