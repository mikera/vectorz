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

import mikera.vectorz.GrowableVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;

/**
 * JMH based benchmark for incremental GrowableVector construction
 *
 * @author Mike
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MiscOperationBenchmark {
	private static final int VECTOR_SIZE = 20;

	private Vector source;

	@Setup
	public void setup() {
		source=new Vector(Vectorz.createUniformRandomVector(1000+VECTOR_SIZE));
	}

	@Benchmark
	public GrowableVector buildGrowableVector() {
		GrowableVector g=new GrowableVector();
		for (int j=0; j<VECTOR_SIZE; j++) {
			g.append(source.get(j));
		}
		return g;
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
				.include(MiscOperationBenchmark.class.getSimpleName())
				.build()).run();
	}

}
