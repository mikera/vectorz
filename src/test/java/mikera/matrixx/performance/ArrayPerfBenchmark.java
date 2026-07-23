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

import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Ops;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.ops.Linear;

/**
 * JMH based benchmarks comparing mutable, immutable and Op-based bulk array
 * operations over a large vector.
 *
 * The vector is rebuilt once per iteration rather than per invocation: it is
 * 8M elements, so per-invocation setup would dominate the measurement. The
 * mutating benchmarks therefore accumulate across invocations within an
 * iteration, as they did under the original Caliper harness.
 *
 * @author Mike
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ArrayPerfBenchmark {
	public static final int VECTOR_SIZE=8388608;

	private Vector v;
	private Op op;

	@Setup(Level.Iteration)
	public void setup() {
		v=Vector.createLength(VECTOR_SIZE);
		Vectorz.fillGaussian(v);
		op=Ops.compose(Linear.create(2.0,0.0), Ops.compose(Ops.SQRT, Linear.create(0.0,0.375)));
	}

	@Benchmark
	public AVector mutable() {
		v.add(0.375);
		v.sqrt();
		v.scale(2.0);
		return v;
	}

	@Benchmark
	public AVector immutable() {
		AVector a=v.addCopy(0.375);
		a=a.sqrtCopy();
		a=a.scaleCopy(2.0);
		return a;
	}

	@Benchmark
	public AVector opsImmutable() {
		return v.applyOpCopy(op);
	}

	@Benchmark
	public AVector opsMutable() {
		v.applyOp(op);
		return v;
	}

	@Benchmark
	public AVector optimised() {
		v.scaleAdd(4.0, 1.5); // combine scaling and addition
		v.sqrt();
		return v;
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
				.include(ArrayPerfBenchmark.class.getSimpleName())
				.build()).run();
	}

}
