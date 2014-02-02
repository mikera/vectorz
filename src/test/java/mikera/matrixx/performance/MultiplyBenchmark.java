package mikera.matrixx.performance;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.algo.Multiplications;

/**
 * Caliper based benchmarks
 * 
 * @author Mike
 */

public class MultiplyBenchmark extends SimpleBenchmark {
	public static final int MATRIX_SIZE=10;

	
	public void timeBlockedMultiply(int runs) {
		Matrix m1=(Matrix)Matrixx.createRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		Matrix m2=(Matrix)Matrixx.createRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		
		for (int i=0; i<runs; i++) {
			Multiplications.blockedMultiply(m1, m2);
		}		
	}
	
	public void timeDoubleBlockedMultiply(int runs) {
		Matrix m1=(Matrix)Matrixx.createRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		Matrix m2=(Matrix)Matrixx.createRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		
		for (int i=0; i<runs; i++) {
			Multiplications.doubleBlockedMultiply(m1, m2);
		}		
	}
	
	public void timeDefaultMultiply(int runs) {
		Matrix m1=(Matrix)Matrixx.createRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		Matrix m2=(Matrix)Matrixx.createRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		
		for (int i=0; i<runs; i++) {
			Multiplications.multiply(m1, m2);
		}		
	}
	
	public void timeInnerProductMultiply(int runs) {
		Matrix m1=(Matrix)Matrixx.createRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		Matrix m2=(Matrix)Matrixx.createRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);

		for (int i=0; i<runs; i++) {
			m1.innerProduct(m2);
		}		
	}
	
	public void timeDirectMultiply(int runs) {
		Matrix m1=(Matrix)Matrixx.createRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		Matrix m2=(Matrix)Matrixx.createRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);

		for (int i=0; i<runs; i++) {
			Multiplications.directMultiply(m1, m2);
		}		
	}
	
	public void timeNaiveMultiply(int runs) {
		Matrix m1=(Matrix)Matrixx.createRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		Matrix m2=(Matrix)Matrixx.createRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);

		for (int i=0; i<runs; i++) {
			Multiplications.naiveMultiply(m1, m2);
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
