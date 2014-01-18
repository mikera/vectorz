package mikera.vectorz.performance;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

/**
 * Caliper based benchmarks
 * 
 * @author Mike
 */

public class FloatVsDoubleBenchmark extends SimpleBenchmark {
	private static final int VECTOR_SIZE = 50000;
	
	public volatile double output=0.0;
	
	public void timeFloat(int runs) {
		for (int run=0; run<runs; run++) {
			float result=0;
			float[] fs = new float[VECTOR_SIZE];
			for (int i =0; i<VECTOR_SIZE; i++) {
				fs[i]+=i;
			}
			for (int i =0; i<VECTOR_SIZE; i++) {
				result+=fs[i]/fs[VECTOR_SIZE-1-i];
			}
			output=result;
		}
	}
	
	public void timeDouble(int runs) {
		for (int run=0; run<runs; run++) {
			double result=0;
			double[] ds = new double[VECTOR_SIZE];
			for (int i =0; i<VECTOR_SIZE; i++) {
				ds[i]+=i;
			}
			for (int i =0; i<VECTOR_SIZE; i++) {
				result+=ds[i]/ds[VECTOR_SIZE-1-i];
			}
			output=result;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new FloatVsDoubleBenchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
