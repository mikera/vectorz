package mikera.matrixx.performance;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

import mikera.matrixx.Matrix;
import mikera.matrixx.Matrix33;
import mikera.matrixx.Matrixx;
import mikera.matrixx.algo.DenseMultiply;
import mikera.vectorz.Vector3;

/**
 * Caliper based benchmarks
 * 
 * @author Mike
 */

public class MultiplyBenchmark extends SimpleBenchmark {
	public static final int MATRIX_SIZE=100;

	
	public void timeDenseMultiply(int runs) {
		Matrix m1=(Matrix)Matrixx.createRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		Matrix m2=(Matrix)Matrixx.createRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		
		for (int i=0; i<runs; i++) {
			DenseMultiply.multiply(m1, m2);
		}		
	}
	
	public void timeDirectMultiply(int runs) {
		Matrix m1=(Matrix)Matrixx.createRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		Matrix m2=(Matrix)Matrixx.createRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);

		for (int i=0; i<runs; i++) {
			m1.innerProduct(m2);
		}		
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MultiplyBenchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
