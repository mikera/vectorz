package mikera.matrixx.algo;

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

import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;

/**
 * JMH based benchmarks comparing determinant algorithms across matrix sizes
 *
 * @author Mike
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class BenchmarkDeterminant {

	private Matrix m3;
	private Matrix m4;
	private Matrix m5;

	@Setup
	public void setup() {
		m3=Matrixx.createRandomSquareMatrix(3);
		m4=Matrixx.createRandomSquareMatrix(4);
		m5=Matrixx.createRandomSquareMatrix(5);
	}

	@Benchmark
	public double naive3() {
		return Determinant.naiveDeterminant(m3);
	}

	@Benchmark
	public double small3() {
		return Determinant.calculateSmallDeterminant(m3, 3);
	}

	@Benchmark
	public double lup3() {
		return Determinant.calculateLUPDeterminant(m3);
	}

	@Benchmark
	public double naive4() {
		return Determinant.naiveDeterminant(m4);
	}

	@Benchmark
	public double lup4() {
		return Determinant.calculateLUPDeterminant(m4);
	}

	@Benchmark
	public double lup5() {
		return Determinant.calculateLUPDeterminant(m5);
	}

	@Benchmark
	public double naive5() {
		return Determinant.naiveDeterminant(m5);
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
				.include(BenchmarkDeterminant.class.getSimpleName())
				.build()).run();
	}

}
