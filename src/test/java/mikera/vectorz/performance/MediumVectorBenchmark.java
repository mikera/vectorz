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

import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;

/**
 * JMH based benchmarks for medium-length vector operations, comparing dense
 * vectors against joined views of the same length.
 *
 * State is rebuilt per iteration rather than per invocation, so the mutating
 * operations accumulate within an iteration as they did under the original
 * Caliper harness.
 *
 * @author Mike
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MediumVectorBenchmark {
	private static final int VECTOR_SIZE=20;

	private Vector source;
	private Vector v;
	private Vector v2;
	private Vector v3;
	private AVector joined;

	@Setup(Level.Iteration)
	public void setup() {
		source=new Vector(Vectorz.createUniformRandomVector(1000+VECTOR_SIZE));
		v=new Vector(Vectorz.createUniformRandomVector(VECTOR_SIZE));
		v2=new Vector(Vectorz.createUniformRandomVector(VECTOR_SIZE));
		v3=new Vector(Vectorz.createUniformRandomVector(VECTOR_SIZE));
		AVector j=Vectorz.newVector(VECTOR_SIZE/2);
		joined=j.join(Vectorz.newVector(VECTOR_SIZE-j.length()));
	}

	@Benchmark
	public AVector vectorAddition() {
		v.add(v2);
		return v;
	}

	@Benchmark
	public AVector vectorAddProduct() {
		v.addProduct(v2,v3);
		return v;
	}

	@Benchmark
	public double aVectorDotProduct() {
		return v.dotProduct(v2);
	}

	@Benchmark
	public AVector vectorOffsetAddition() {
		v.add(source,100);
		return v;
	}

	@Benchmark
	public AVector joinedVectorSet() {
		joined.set(v2);
		return joined;
	}

	@Benchmark
	public AVector joinedVectorAddition() {
		joined.add(v2);
		return joined;
	}

	@Benchmark
	public AVector joinedVectorAddMultiple() {
		joined.addMultiple(v2,0.5);
		return joined;
	}

	@Benchmark
	public AVector joinedVectorAddProduct() {
		joined.addProduct(v2,v2,0.001);
		return joined;
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
				.include(MediumVectorBenchmark.class.getSimpleName())
				.build()).run();
	}

}
