package mikera.vectorz.performance;

import java.util.Arrays;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.impl.AStridedMatrix;
import mikera.matrixx.impl.StridedMatrix;
import mikera.vectorz.Ops;
import mikera.vectorz.Vector;
import mikera.vectorz.util.DoubleArrays;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

/**
 * Caliper based benchmarks for sublist iteration
 * 
 * See debate at: http://stackoverflow.com/questions/17302130/enhanced-for-loop/17302215
 * 
 * @author Mike
 */
@SuppressWarnings("unused")
public class MatrixTypeBenchmark extends SimpleBenchmark {
	double result;
	
	static int DIM_SIZE=100;

	static final AMatrix src=Matrix.create(DIM_SIZE,DIM_SIZE);
	
	static{
		Matrixx.fillRandomValues(src);
	}
	
	private void doMatrixText(AMatrix m) {
		m.add(1.0);
		m.mul(src);
	}
	
	public void timeMatrix(int runs) {
		Matrix m=Matrix.create(src);	
		for (int i=0; i<runs; i++) {
			doMatrixText(m);
		}
		result=m.elementSum();
	}
	
	public void timeStridedMatrix(int runs) {
		StridedMatrix m=StridedMatrix.create(src);	
		for (int i=0; i<runs; i++) {
			doMatrixText(m);
		}
		result=m.elementSum();
	}
	
	public void timeStridedMatrixTranspose(int runs) {
		AStridedMatrix m=StridedMatrix.create(src).getTransposeView();	
		for (int i=0; i<runs; i++) {
			doMatrixText(m);
		}
		result=m.elementSum();
	}

	public static void main(String[] args) {
		new MatrixTypeBenchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
