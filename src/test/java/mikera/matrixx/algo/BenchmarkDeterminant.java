package mikera.matrixx.algo;

import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

/**
 * Caliper based benchmarks
 * 
 * @author Mike
 */

public class BenchmarkDeterminant extends SimpleBenchmark {
	public static final int MATRIX_SIZE=10;
	public volatile double result;
	
	public void time3Naive(int runs) {
		Matrix m=Matrixx.createRandomSquareMatrix(3);
		
		for (int i=0; i<runs; i++) {
			result=Determinant.naiveDeterminant(m);
		}		
	}
	
	public void time3Small(int runs) {
		Matrix m=Matrixx.createRandomSquareMatrix(3);
		
		for (int i=0; i<runs; i++) {
			result=Determinant.calculateSmallDeterminant(m, 3);
		}		
	}
	
	public void time3LUP(int runs) {
		Matrix m=Matrixx.createRandomSquareMatrix(3);
		
		for (int i=0; i<runs; i++) {
			result=Determinant.calculateLUPDeterminant(m);
		}		
	}
	
	public void time4Naive(int runs) {
		Matrix m=Matrixx.createRandomSquareMatrix(4);
		
		for (int i=0; i<runs; i++) {
			result=Determinant.naiveDeterminant(m);
		}		
	}
	
	public void time4LUP(int runs) {
		Matrix m=Matrixx.createRandomSquareMatrix(4);
		
		for (int i=0; i<runs; i++) {
			result=Determinant.calculateLUPDeterminant(m);
		}		
	}
	
	public void time5LUP(int runs) {
		Matrix m=Matrixx.createRandomSquareMatrix(5);
		
		for (int i=0; i<runs; i++) {
			result=Determinant.calculateLUPDeterminant(m);
		}		
	}
	
	public void time5Naive(int runs) {
		Matrix m=Matrixx.createRandomSquareMatrix(5);
		
		for (int i=0; i<runs; i++) {
			result=Determinant.naiveDeterminant(m);
		}		
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new BenchmarkDeterminant().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
