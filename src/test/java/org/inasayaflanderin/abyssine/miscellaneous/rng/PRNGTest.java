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
                new LCG(23453242323768756868297340987234792384D, 28312, 981233824925234920384239848523892375928309230957D),
                new MCG(9324234234239L, 23034989),
                new ACG(923423023402384L, 923417),
                new MECG(91981291842213L, 9123123, 912312482934L, 5),
                new BLCG(923234234423L, 92342123423L, 389295897593L),
                new ICG(910284904124L, 9123809213L, 91248124124L),
                new PCG(9234023492034L,12378612874L, 23),
                new LMWC(9302489322434L,84124312,1324885,23),
                new LMWCC(3942234324234L, 9832423, 89234832, 34),
                new MWC(9234234234L, 9234234234L, 234234L),
                new MWCC(9324234324L, 823478923, 9234234),
                //LFG
                new ALFG(24, 35),
                new MLFG(44, 17),
                new LFSR()
        );
    }

    @ParameterizedTest
    @MethodSource("initialPRNG")
    void test(RandomGenerators rng) {
        assertAll(
                () -> {
                    double f = rng.next();
                    double s = rng.next();

                    assertTrue(f != s, rng.getClass().getSimpleName() + " generate same number");
                },

                () -> {
                    boolean satisfied = true;

                    for(int i = 0; i < 1000000; i++) {
                        double r = rng.next();

                        if(r < 0 || r > 1) satisfied = false;
                    }

                    assertTrue(satisfied, rng.getClass().getSimpleName() + " generate out of bound [0, 1]");
                }
        );
    }
}