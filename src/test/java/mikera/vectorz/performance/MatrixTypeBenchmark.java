package mikera.vectorz.performance;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
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
import mikera.matrixx.impl.AStridedMatrix;
import mikera.matrixx.impl.StridedMatrix;

/**
 * JMH based benchmarks comparing dense, strided and transposed-strided matrix
 * layouts under the same bulk operations.
 *
 * The matrices are rebuilt per iteration rather than per invocation, so the
 * mutating operations accumulate within an iteration as they did under the
 * original Caliper harness.
 *
 * @author Mike
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class MatrixTypeBenchmark {

	static final int DIM_SIZE=100;

	private AMatrix src;
	private Matrix dense;
	private StridedMatrix strided;
	private AStridedMatrix stridedTranspose;

	@Setup(Level.Iteration)
	public void setup() {
		src=Matrix.create(DIM_SIZE,DIM_SIZE);
		Matrixx.fillRandomValues(src);
		dense=Matrix.create(src);
		strided=StridedMatrix.create(src);
		stridedTranspose=StridedMatrix.create(src).getTransposeView();
	}

	private AMatrix doMatrixTest(AMatrix m) {
		m.add(1.0);
		m.mul(src);
		return m;
	}

	@Benchmark
	public AMatrix matrix() {
		return doMatrixTest(dense);
	}

	@Benchmark
	public AMatrix stridedMatrix() {
		return doMatrixTest(strided);
	}

	@Benchmark
	public AMatrix stridedMatrixTranspose() {
		return doMatrixTest(stridedTranspose);
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
				.include(MatrixTypeBenchmark.class.getSimpleName())
				.build()).run();
	}

}
