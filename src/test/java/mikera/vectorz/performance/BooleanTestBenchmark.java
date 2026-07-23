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
 * Test to measure the cost of a never-taken boolean branch in a tight inner loop.
 *
 * @author Mike
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class BooleanTestBenchmark {

	static final int SIZE=100;

	private final int[] dat=new int[SIZE];

	private int basic() {
		for (int j=0; j<SIZE-1; j++) {
			dat[j]=dat[j+1];
		}
		return 0;
	}

	private int withTest(boolean b) {
		for (int j=0; j<SIZE-1; j++) {
			dat[j]=dat[j+1];
			if (b) {
				return 1;
			}
		}
		return 0;
	}

	@Benchmark
	public int basicLoop() {
		return basic();
	}

	@Benchmark
	public int loopWithTest() {
		return withTest(false);
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
				.include(BooleanTestBenchmark.class.getSimpleName())
				.build()).run();
	}

}
