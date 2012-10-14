package mikera.vectorz.performance;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

import mikera.vectorz.GrowableVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;

/**
 * Caliper based benchmarks
 * 
 * @author Mike
 */

public class MiscOperationBenchmark extends SimpleBenchmark {
	private static final int VECTOR_SIZE = 20;
	
	private static final Vector source=new Vector( Vectorz.createUniformRandomVector(1000+VECTOR_SIZE));
	
	public void timeBuildGrowableVector(int runs) {
		for (int i=0; i<runs; i++) {
			GrowableVector g=new GrowableVector();
			for (int j=0; j<VECTOR_SIZE; j++) {
				g.append(source.get(j));
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MiscOperationBenchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
