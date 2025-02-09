package org.inasayaflanderin.abyssine.test.template;

import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class TestTest {
    @RepeatedParameterizedTest(value = 10)
    @CsvSource({"1", "2", "3"})
    public void test(String string) {
        assertTrue(Integer.parseInt(string) > 0);
    }
}