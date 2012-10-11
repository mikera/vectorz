package mikera.vectorz.performance;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector3;

/**
 * Caliper based benchmarks
 * 
 * @author Mike
 */

public class PerformanceTest extends SimpleBenchmark {
	
	public void timeVector3Addition(int runs) {
		Vector3 v=Vector3.of(1,2,3);
		Vector3 v2=Vector3.of(1,2,3);
		for (int i=0; i<runs; i++) {
			v.add(v2);
		}
	}
	
	public void timeVectorAddMultiple(int runs) {
		AVector v=Vector.of(1,2,3);
		AVector v2=Vector3.of(1,2,3);
		
		for (int i=0; i<runs; i++) {
			v.addMultiple(v2,2.0);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PerformanceTest().run();
	}

	private void run() {
		new Runner().run(new String[] {this.getClass().getCanonicalName()});
	}

}
