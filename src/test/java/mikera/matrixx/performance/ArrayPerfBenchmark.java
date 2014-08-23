package mikera.matrixx.performance;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.algo.Multiplications;
import mikera.vectorz.Vector;

/**
 * Caliper based benchmarks
 * 
 * @author Mike
 */

public class ArrayPerfBenchmark extends SimpleBenchmark {
	public static final int MATRIX_SIZE=10;

	public void timeBlockedMultiply(int runs) {
		Vector v=Vector.createLength(8388608);
		
		for (int i=0; i<runs; i++) {
			v.add(0.125);
			v.sqrt();
			v.scale(2.0);
		}		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ArrayPerfBenchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
