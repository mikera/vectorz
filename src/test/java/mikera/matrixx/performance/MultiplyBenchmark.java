package mikera.matrixx.performance;

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

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.algo.Multiplications;

/**
 * JMH based benchmarks comparing matrix multiplication strategies
 *
 * @author Mike
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MultiplyBenchmark {
	public static final int MATRIX_SIZE=10;

	private Matrix m1;
	private Matrix m2;

	@Setup
	public void setup() {
		m1=(Matrix)Matrixx.createRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		m2=(Matrix)Matrixx.createRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
	}

	@Benchmark
	public AMatrix blockedMultiply() {
		return Multiplications.blockedMultiply(m1, m2);
	}

	@Benchmark
	public AMatrix doubleBlockedMultiply() {
		return Multiplications.doubleBlockedMultiply(m1, m2);
	}

	@Benchmark
	public AMatrix defaultMultiply() {
		return Multiplications.multiply(m1, m2);
	}

	@Benchmark
	public AMatrix innerProductMultiply() {
		return m1.innerProduct(m2);
	}

	@Benchmark
	public AMatrix directMultiply() {
		return Multiplications.directMultiply(m1, m2);
	}

	@Benchmark
	public AMatrix naiveMultiply() {
		return Multiplications.naiveMultiply(m1, m2);
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
				.include(MultiplyBenchmark.class.getSimpleName())
				.build()).run();
	}

}
