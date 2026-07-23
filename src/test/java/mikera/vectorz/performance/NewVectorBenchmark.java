package mikera.vectorz.performance;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

/**
 * Benchmark for generation of new vectors
 *
 * See debate at: http://stackoverflow.com/questions/17302130/enhanced-for-loop/17302215
 *
 * @author Mike
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class NewVectorBenchmark {

	static final int LIST_SIZE=100;

	private AVector preAllocated;

	@Setup
	public void setup() {
		preAllocated=Vector.createLength(LIST_SIZE);
	}

	@Benchmark
	public AVector newVector() {
		return Vector.createLength(LIST_SIZE);
	}

	@Benchmark
	public double[] newDoubleArray() {
		return new double[LIST_SIZE];
	}

	@Benchmark
	public AVector zeroVector() {
		preAllocated.set(0.0);
		return preAllocated;
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
				.include(NewVectorBenchmark.class.getSimpleName())
				.build()).run();
	}

}
