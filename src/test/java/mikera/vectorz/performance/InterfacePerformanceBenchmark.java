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
import mikera.vectorz.IVector;
import mikera.vectorz.Vector3;

/**
 * JMH based benchmarks comparing dispatch through an interface, an abstract
 * base class and a concrete final type.
 *
 * @author Mike
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class InterfacePerformanceBenchmark {

	private final IVector[] ivectors=new IVector[16];
	private final AVector[] avectors=new AVector[16];
	private final Vector3[] vector3s=new Vector3[16];

	private IVector iv;
	private AVector av;
	private Vector3 v3;

	private int counter;

	@Setup(Level.Iteration)
	public void setup() {
		for (int i=0; i<ivectors.length; i++) {
			Vector3 v=new Vector3();
			ivectors[i]=v;
			avectors[i]=v;
			vector3s[i]=v;
		}
		iv=Vector3.of(1,2,3);
		av=Vector3.of(1,2,3);
		v3=Vector3.of(1,2,3);
		counter=0;
	}

	@Benchmark
	public double iVectorAddition() {
		IVector v2=ivectors[(counter++)&15];
		v2.set(0,iv.get(0)+v2.get(0));
		return v2.get(0);
	}

	@Benchmark
	public double aVectorAddition() {
		AVector v2=avectors[(counter++)&15];
		v2.set(0,av.get(0)+v2.get(0));
		return v2.get(0);
	}

	@Benchmark
	public double vector3Addition() {
		Vector3 v2=vector3s[(counter++)&15];
		v2.set(0,v3.get(0)+v2.get(0));
		return v2.get(0);
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
				.include(InterfacePerformanceBenchmark.class.getSimpleName())
				.build()).run();
	}

}
