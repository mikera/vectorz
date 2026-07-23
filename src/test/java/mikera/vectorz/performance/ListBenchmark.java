package mikera.vectorz.performance;

import java.util.ArrayList;
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

/**
 * JMH based benchmarks for sublist iteration
 *
 * See debate at: http://stackoverflow.com/questions/17302130/enhanced-for-loop/17302215
 *
 * @author Mike
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ListBenchmark {

	static final int LIST_SIZE=8;

	private ArrayList<Integer> calc;

	@Setup
	public void setup() {
		calc=new ArrayList<Integer>();
		for (int i=0; i<LIST_SIZE; i++) calc.add(i);
	}

	@Benchmark
	public int indexed() {
		int result=0;
		int n=calc.size();
		for (int j=2; j<n; j++) {
			result+=calc.get(j);
		}
		return result;
	}

	@Benchmark
	public int indexedWithSize() {
		int result=0;
		for (int j=2; j<calc.size(); j++) {
			result+=calc.get(j);
		}
		return result;
	}

	@Benchmark
	public int iterator() {
		int result=0;
		int n=calc.size();
		for (Integer j: calc.subList(2, n)) {
			result+=j;
		}
		return result;
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
				.include(ListBenchmark.class.getSimpleName())
				.build()).run();
	}

}
