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

/**
 * Benchmark for Op2 performance
 *
 * NOTE: this measures a zero-filled vector, preserving the behaviour of the
 * original Caliper benchmark. That version also built two Gaussian-filled
 * vectors and then never used them, so the numbers have always described the
 * all-zeros case. Worth revisiting if the intent was to measure real data.
 *
 * @author Mike
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class Op2Benchmark {
	public static final int VECTOR_SIZE = 1000;

	private AVector t;
	private Op2 op;

	@Setup
	public void setup() {
		t=Vector.createLength(VECTOR_SIZE);
		op=Ops.MAX;
	}

	@Benchmark
	public double reduceWithOp() {
		return t.reduce(op,0.0);
	}

	@Benchmark
	public double elementMax() {
		return t.elementMax();
	}

	@Benchmark
	public double elementwise() {
		double result=Double.NEGATIVE_INFINITY;
		for (int j=0; j<VECTOR_SIZE; j++) {
			result=Math.max(result,t.unsafeGet(j));
		}
		return result;
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
				.include(Op2Benchmark.class.getSimpleName())
				.build()).run();
	}

}
