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
import mikera.vectorz.Op2;
import mikera.vectorz.Ops;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.ops.AddFunction;

/**
 * JMH based benchmarks computing z = 0.5*x + 0.5*y*y three ways: via a fused
 * Op2, via a sequence of bulk vector functions, and element by element.
 *
 * @author Mike
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class OpBenchmark {
	public static final int VECTOR_SIZE = 1000;

	private AVector a;
	private AVector b;
	private AVector t;
	private Op2 op;

	@Setup
	public void setup() {
		a=Vector.createLength(VECTOR_SIZE);
		b=Vector.createLength(VECTOR_SIZE);
		Vectorz.fillGaussian(a);
		Vectorz.fillGaussian(b);
		t=Vector.createLength(VECTOR_SIZE);
		op=AddFunction.create(0.5,0.5,Ops.SQUARE);
	}

	@Benchmark
	public AVector withOp() {
		t.set(a);
		t.applyOp(op, b);
		return t;
	}

	@Benchmark
	public AVector withFunctions() {
		t.set(b);
		t.square();
		t.scale(0.5);
		t.addMultiple(a, 0.5);
		return t;
	}

	@Benchmark
	public AVector elementwise() {
		for (int j=0; j<VECTOR_SIZE; j++) {
			double x=a.unsafeGet(j);
			double y=b.unsafeGet(j);
			double z=0.5*x + 0.5*y*y;
			t.unsafeSet(j,z);
		}
		return t;
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
				.include(OpBenchmark.class.getSimpleName())
				.build()).run();
	}

}
