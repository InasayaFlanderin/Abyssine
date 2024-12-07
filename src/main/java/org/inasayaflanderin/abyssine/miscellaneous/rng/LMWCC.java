package org.inasayaflanderin.abyssine.miscellaneous.rng;


import java.io.Serial;

public class LMWCC extends LMWC {
    @Serial
    private static final long serialVersionUID = 867976142157747322L;

    public LMWCC(double multiplier, double increment, double modulus, int lag) {
        super(multiplier, increment, modulus, lag);
    }

    public LMWCC(double seed, double multiplier, double increment, double modulus, int lag) {
        super(seed, multiplier, increment, modulus, lag);
    }

    public double next() {
        var result = getModulus() - super.next() * getModulus();
        result %= getModulus();

        lagQueue.set(getLag() - 1, result);
        setIncrement(result / getModulus());

        return result / getModulus();
    }
}
