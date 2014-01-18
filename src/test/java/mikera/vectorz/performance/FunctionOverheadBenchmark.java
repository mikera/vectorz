package mikera.vectorz.performance;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

/**
 * Caliper based benchmarks
 * 
 * @author Mike
 */

public class FunctionOverheadBenchmark extends SimpleBenchmark {
	volatile long temp=0;
	
	public void timeDirectOperation(int runs) {
		long t=0;
		for (int i=0; i<runs; i++) {
			for (long j=0; j<100; j++) {
				t+=j+1;
			}
		}
		temp=t;
	}
	
	public static long incFn(long j) {
		return j+1;
	}
	
	public void timeFunctionOperation(int runs) {
		long t=0;
		for (int i=0; i<runs; i++) {
			for (long j=0; j<100; j++) {
				t+=incFn(j);
			}
		}
		temp=t;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new FunctionOverheadBenchmark().run();
	}

	void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
