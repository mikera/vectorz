package mikera.vectorz.performance;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix33;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector3;

/**
 * Caliper based benchmarks
 * 
 * @author Mike
 */

public class PerformanceBenchmark extends SimpleBenchmark {
	
	public void timeVector3Addition(int runs) {
		Vector3 v=Vector3.of(1,2,3);
		Vector3 v2=Vector3.of(1,2,3);
		for (int i=0; i<runs; i++) {
			v.add(v2);
		}
	}
	
	public void timeMatrix3Rotation(int runs) {
		Vector3 axis=Vector3.of(1,2,3);
		Vector3 v=Vector3.of(Math.random(),Math.random(),Math.random());

		Matrix33 rot=Matrixx.createRotationMatrix(axis, Math.random());
		for (int i=0; i<runs; i++) {
			rot.transformInPlace(v);
		}
	}
	
	public void timeVectorAddMultiple(int runs) {
		AVector v=Vector.of(1,2,3);
		AVector v2=Vector.of(1,2,3);
		
		for (int i=0; i<runs; i++) {
			v.addMultiple(v2,2.0);
		}
	}
	
	public void timeMatrixInverse(int runs) {
		AMatrix m=Matrixx.createRandomSquareMatrix(5);
		for (int i=0; i<runs; i++) {
			m=m.inverse();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PerformanceBenchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
