package mikera.vectorz.performance;

import java.util.Arrays;
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

import mikera.vectorz.Vector;
import mikera.vectorz.util.DoubleArrays;

/**
 * JMH based benchmarks comparing ways of copying a double[] backing array
 *
 * See debate at: http://stackoverflow.com/questions/17302130/enhanced-for-loop/17302215
 *
 * @author Mike
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class CloneBenchmark {

	static final int LIST_SIZE=100;

	private double[] data;

	@Setup
	public void setup() {
		data=Vector.createLength(LIST_SIZE).getArray();
	}

	@Benchmark
	public Vector cloneArray() {
		return Vector.wrap(data.clone());
	}

	@Benchmark
	public Vector copyOfArray() {
		return Vector.wrap(Arrays.copyOf(data, data.length));
	}

	@Benchmark
	public Vector doubleArraysCopy() {
		return Vector.wrap(DoubleArrays.copyOf(data));
	}

	@Benchmark
	public Vector arrayCopyClone() {
		double[] ds=new double[data.length];
		System.arraycopy(data, 0, ds, 0, data.length);
		return Vector.wrap(ds);
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
				.include(CloneBenchmark.class.getSimpleName())
				.build()).run();
	}

}
