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

import mikera.arrayz.Array;
import mikera.arrayz.INDArray;
import mikera.arrayz.NDArray;
import mikera.matrixx.Matrix;
import mikera.matrixx.impl.StridedMatrix;
import mikera.vectorz.Vector;

/**
 * JMH based benchmarks comparing the cost of the same bulk operations across
 * array representations. Each benchmark allocates its own array, so allocation
 * is deliberately part of what is measured.
 *
 * @author Mike
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class VectorzTypeBenchmark {
	private static final int ARRAY_SIZE = 100;
	private static final int INNER_ITERATIONS = 10;

	@Benchmark
	public INDArray vector() {
		Vector a=Vector.createLength(ARRAY_SIZE);
		for (int ii=0; ii<INNER_ITERATIONS; ii++) {
			a.add(ii);
			a.divide(ii);
		}
		return a;
	}

	@Benchmark
	public INDArray matrix() {
		Matrix a=Matrix.create(1,ARRAY_SIZE);
		for (int ii=0; ii<INNER_ITERATIONS; ii++) {
			a.add(ii);
			a.divide(ii);
		}
		return a;
	}

	@Benchmark
	public INDArray stridedMatrix() {
		StridedMatrix a=StridedMatrix.create(1,ARRAY_SIZE);
		for (int ii=0; ii<INNER_ITERATIONS; ii++) {
			a.add(ii);
			a.divide(ii);
		}
		return a;
	}

	@Benchmark
	public INDArray array() {
		Array a=Array.newArray(1,ARRAY_SIZE);
		for (int ii=0; ii<INNER_ITERATIONS; ii++) {
			a.add(ii);
			a.divide(ii);
		}
		return a;
	}

	@Benchmark
	public INDArray ndArray() {
		NDArray a=NDArray.newArray(1,ARRAY_SIZE);
		for (int ii=0; ii<INNER_ITERATIONS; ii++) {
			a.add(ii);
			a.divide(ii);
		}
		return a;
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
				.include(VectorzTypeBenchmark.class.getSimpleName())
				.build()).run();
	}

}
