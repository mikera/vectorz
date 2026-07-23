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
import mikera.matrixx.Matrix33;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector3;

/**
 * JMH based benchmarks for common small vector and matrix operations
 *
 * @author Mike
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class PerformanceBenchmark {

	private Vector3 v3;
	private Vector3 v3b;
	private Vector3 rotated;
	private Matrix33 rot;
	private AVector v;
	private AVector vb;
	private AMatrix square;

	@Setup(Level.Iteration)
	public void setup() {
		v3=Vector3.of(1,2,3);
		v3b=Vector3.of(1,2,3);
		rotated=Vector3.of(Math.random(),Math.random(),Math.random());
		rot=Matrixx.createRotationMatrix(Vector3.of(1,2,3), Math.random());
		v=Vector.of(1,2,3);
		vb=Vector.of(1,2,3);
		square=Matrixx.createRandomSquareMatrix(5);
	}

	@Benchmark
	public AVector vector3Addition() {
		v3.add(v3b);
		return v3;
	}

	@Benchmark
	public AVector matrix3Rotation() {
		rot.transformInPlace(rotated);
		return rotated;
	}

	@Benchmark
	public AVector vectorAddMultiple() {
		v.addMultiple(vb,2.0);
		return v;
	}

	@Benchmark
	public AMatrix matrixInverse() {
		return square.inverse();
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
				.include(PerformanceBenchmark.class.getSimpleName())
				.build()).run();
	}

}
