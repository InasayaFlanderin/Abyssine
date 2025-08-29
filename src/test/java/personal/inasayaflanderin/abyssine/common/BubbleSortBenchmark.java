package personal.inasayaflanderin.abyssine.common;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Thread)
public class BubbleSortBenchmark {
	private final Double[] subject = new Double[32000];
	private final Random rng = new Random();

	@Setup(Level.Iteration)
	public void setUp() {
		for(var i = 0; i < subject.length; i++) subject[i] = rng.nextDouble();
	}

	@Benchmark
	@BenchmarkMode(Mode.All)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
	@Measurement(iterations = 100, time = 1, timeUnit = TimeUnit.SECONDS)
	@Fork(1)
	public void bubble(Blackhole bh) {
		Sort.bubble(subject, Double::compareTo, 0, subject.length);
		bh.consume(subject);
	}
}
