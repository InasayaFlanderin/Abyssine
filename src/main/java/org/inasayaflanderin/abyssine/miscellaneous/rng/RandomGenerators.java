package org.inasayaflanderin.abyssine.miscellaneous.rng;

import java.io.Serializable;

public interface RandomGenerators extends Serializable {
    void setSeed(long seed);
    long getSeed();
    double next();
}
