package org.inasayaflanderin.abyssine.experiments;

import net.jqwik.api.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JqwikTest {
    @Provide
    Arbitrary<Integer> provider() {
        return Arbitraries.of(1, 2, 3, 4);
    }

    @Property(tries = 100)
    void test(@ForAll("provider") int value) {
        System.out.println(value);

        assertTrue(value > 0);
    }
}