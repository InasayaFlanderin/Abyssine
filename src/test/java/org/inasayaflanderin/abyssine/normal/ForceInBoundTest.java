package org.inasayaflanderin.abyssine.normal;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread) @SuppressWarnings("ResultOfMethodCallIgnored")
public class ForceInBoundTest {
    private static final Random ran = new Random();
    private static final int[] fPrepareTest = new int[25000000];
    private static final int[] sPrepareTest = new int[25000000];
    private static final int[] eResult = new int[25000000];
    private static final int[] aResult = new int[25000000];
    private static int fPrepareBenchmark, sPrepareBenchmark;

    private int expected(int position, int max) {
        return Math.min(Math.max(0, position), max);
    }

    private int fTestTarget(int position, int max) {
        int temp = position & ~((position >> 31) ^ (max >> 31));
        int sign = ((temp - max) >> 31) & 1;
        return (sign * temp) + ((1 - sign) * max);
    }

    private int sTestTarget(int position, int max) {
        int temp = position & ~(position >> 31);
        int sign = (temp - max) >> 31;
        return (sign & temp) | (~sign & max);
    }

    @BeforeAll
    public static void prepareTest() {
        for (int i = 0; i < 25000000; i++) {
            fPrepareTest[i] = ran.nextInt(20000) - 10000;
            sPrepareTest[i] = ran.nextInt(17900) + 100;
        }
    }

    @Setup(Level.Iteration)
    public void prepareBenchmark() {
        fPrepareBenchmark = ran.nextInt(20000) - 10000;
        sPrepareBenchmark = ran.nextInt(17900) + 100;
    }

    @Test
    public void firstTest() {
        for (int i = 0; i < 25000000; i++) {
            eResult[i] = expected(fPrepareTest[i], sPrepareTest[i]);
            aResult[i] = fTestTarget(fPrepareTest[i], sPrepareTest[i]);
        }

        assertArrayEquals(eResult, aResult);
    }

    @Test
    public void secondTest() {
        for (int i = 0; i < 25000000; i++) {
            eResult[i] = expected(fPrepareTest[i], sPrepareTest[i]);
            aResult[i] = sTestTarget(fPrepareTest[i], sPrepareTest[i]);
        }

        assertArrayEquals(eResult, aResult);
    }

    @Benchmark @BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.NANOSECONDS) @Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.MICROSECONDS) @Measurement(iterations = 50000, time = 1, timeUnit = TimeUnit.MICROSECONDS) @Fork(1)
    /*
    * Benchmark                           Mode    Cnt  Score   Error  Units
    * ForceInBoundTest.standardBenchmark  avg  50000  3.151 ± 0.196  ns/op
    * */
    public void standardBenchmark() {
        expected(fPrepareBenchmark, sPrepareBenchmark);
    }

    @Benchmark @BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.NANOSECONDS) @Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.MICROSECONDS) @Measurement(iterations = 50000, time = 1, timeUnit = TimeUnit.MICROSECONDS) @Fork(1)
    /*
     * Benchmark                              Mode    Cnt  Score   Error  Units
     * ForceInBoundTest.firstTargetBenchmark  avg  50000  2.915 ± 0.132  ns/op
     * */
    public void firstTargetBenchmark() {
        fTestTarget(fPrepareBenchmark, sPrepareBenchmark);
    }

    @Benchmark @BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.NANOSECONDS) @Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.MICROSECONDS) @Measurement(iterations = 50000, time = 1, timeUnit = TimeUnit.MICROSECONDS) @Fork(1)
    /*
    * Benchmark                               Mode    Cnt  Score   Error  Units
    * ForceInBoundTest.secondTargetBenchmark  avg  50000  2.984 ± 0.147  ns/op
    * */
    public void secondTargetBenchmark() {
        sTestTarget(fPrepareBenchmark, sPrepareBenchmark);
    }
}