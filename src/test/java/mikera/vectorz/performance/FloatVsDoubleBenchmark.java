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
 * JMH based benchmark comparing float and double arithmetic throughput
 *
 * @author Mike
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class FloatVsDoubleBenchmark {
	private static final int VECTOR_SIZE = 50000;

	@Benchmark
	public float floats() {
		float result=0;
		float[] fs = new float[VECTOR_SIZE];
		for (int i =0; i<VECTOR_SIZE; i++) {
			fs[i]+=i;
		}
		for (int i =0; i<VECTOR_SIZE; i++) {
			result+=fs[i]/fs[VECTOR_SIZE-1-i];
		}
		return result;
	}

	@Benchmark
	public double doubles() {
		double result=0;
		double[] ds = new double[VECTOR_SIZE];
		for (int i =0; i<VECTOR_SIZE; i++) {
			ds[i]+=i;
		}
		for (int i =0; i<VECTOR_SIZE; i++) {
			result+=ds[i]/ds[VECTOR_SIZE-1-i];
		}
		return result;
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
				.include(FloatVsDoubleBenchmark.class.getSimpleName())
				.build()).run();
	}

}
