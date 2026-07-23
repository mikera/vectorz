package mikera.matrixx.performance;

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

import mikera.matrixx.Matrix33;
import mikera.vectorz.Vector3;

/**
 * JMH based benchmarks for 3D vector and 3x3 matrix operations
 *
 * @author Mike
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ThreeDBenchmark {
	public static final Vector3 SMALL_DELTA=Vector3.of(0.00001,0.00001,0.00001);

	private Vector3 v;
	private Vector3 t;
	private Matrix33 m;

	@Setup(Level.Iteration)
	public void setup() {
		v=new Vector3(1,2,3);
		t=new Vector3(1,2,3);
		m=new Matrix33(1,2,3,4,5,6,7,8,9);
	}

	@Benchmark
	public Vector3 matrix33Transform() {
		v.add(SMALL_DELTA);
		return m.transform(v);
	}

	@Benchmark
	public Vector3 matrix33TransformInPlace() {
		v.add(SMALL_DELTA);
		t.set(v);
		m.transformInPlace(t);
		return t;
	}

	@Benchmark
	public Matrix33 matrix33Clone() {
		return m.clone();
	}

	@Benchmark
	public double matrix33Determinant() {
		m.m00+=0.0000001;
		return m.determinant();
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
				.include(ThreeDBenchmark.class.getSimpleName())
				.build()).run();
	}

}
