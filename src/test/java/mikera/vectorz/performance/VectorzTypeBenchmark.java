package mikera.vectorz.performance;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

import mikera.arrayz.Array;
import mikera.arrayz.NDArray;
import mikera.matrixx.Matrix;
import mikera.matrixx.impl.StridedMatrix;
import mikera.vectorz.Vector;

/**
 * Caliper based benchmarks
 * 
 * @author Mike
 */

public class VectorzTypeBenchmark extends SimpleBenchmark {
	private static final int ARRAY_SIZE = 100;
	private static final int INNER_ITERATIONS = 10;
	
	public void timeVector(int runs) {
		for (int i=0; i<runs; i++) {
			Vector a=Vector.createLength(ARRAY_SIZE);
			for (int ii=0; ii<INNER_ITERATIONS; ii++) {
				a.add(ii);
				a.divide(ii);
			}
		}
	}
	
	public void timeMatrix(int runs) {
		for (int i=0; i<runs; i++) {
			Matrix a=Matrix.create(1,ARRAY_SIZE);
			for (int ii=0; ii<INNER_ITERATIONS; ii++) {
				a.add(ii);
				a.divide(ii);
			}
		}
	}
	
	public void timeStridedMatrix(int runs) {
		for (int i=0; i<runs; i++) {
			StridedMatrix a=StridedMatrix.create(1,ARRAY_SIZE);
			for (int ii=0; ii<INNER_ITERATIONS; ii++) {
				a.add(ii);
				a.divide(ii);
			}
		}
	}
	
	public void timeArray(int runs) {
		for (int i=0; i<runs; i++) {
			Array a=Array.newArray(1,ARRAY_SIZE);
			for (int ii=0; ii<INNER_ITERATIONS; ii++) {
				a.add(ii);
				a.divide(ii);
			}
		}
	}
	
	public void timeNDArray(int runs) {
		for (int i=0; i<runs; i++) {
			NDArray a=NDArray.newArray(1,ARRAY_SIZE);
			for (int ii=0; ii<INNER_ITERATIONS; ii++) {
				a.add(ii);
				a.divide(ii);
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new VectorzTypeBenchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
