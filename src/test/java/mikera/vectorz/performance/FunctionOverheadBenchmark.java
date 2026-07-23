package mikera.vectorz.performance;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * JMH based benchmark measuring the overhead of a static method call versus
 * the equivalent operation inlined by hand.
 *
 * @author Mike
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class FunctionOverheadBenchmark {

	public static long incFn(long j) {
		return j+1;
	}

	@Benchmark
	public long directOperation() {
		long t=0;
		for (long j=0; j<100; j++) {
			t+=j+1;
		}
		return t;
	}

	@Benchmark
	public long functionOperation() {
		long t=0;
		for (long j=0; j<100; j++) {
			t+=incFn(j);
		}
		return t;
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
				.include(FunctionOverheadBenchmark.class.getSimpleName())
				.build()).run();
	}

}
