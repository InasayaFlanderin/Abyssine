package org.inasayaflanderin.abyssine.miscellaneous.rng;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PRNGTest {
    static List<RandomGenerators> initialPRNG() {
        return List.of(
                //Square
                new MS(),
                new MSWS(),
                new Square(100),
                //LCG
                new LCG(2345324232376875686L, 28312, 98123),
                new MCG(9324234234239L, 23034989),
                new ACG(923423023402384L, 923417),
                new MECG(91981291842213L, 9123123, 9123124, 10232134),
                new BLCG(923234234423L, 92342123423L, 9239768323L)
        );
    }

    @ParameterizedTest
    @MethodSource("initialPRNG")
    void test(RandomGenerators rng) {
        assertAll(
                () -> {
                    double f = rng.next();
                    double s = rng.next();

                    assertTrue(f != s, rng.getClass().getSimpleName() + "generate same number");
                },

                () -> {
                    boolean satisfied = true;

                    for(int i = 0; i < 1000; i++) {
                        double r = rng.next();

                        if(r < 0 || r > 1) satisfied = false;
                    }

                    assertTrue(satisfied, rng.getClass().getSimpleName() + "generate out of bound [0, 1]");
                }
        );
    }
}