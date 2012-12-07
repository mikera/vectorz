package mikera.matrixx.performance;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

import mikera.matrixx.Matrix33;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector3;
import mikera.vectorz.Vectorz;

/**
 * Caliper based benchmarks
 * 
 * @author Mike
 */

public class ThreeDBenchmark extends SimpleBenchmark {
	public static final int VECTOR_SIZE=3;
	public static final Vector3 r=Vector3.of(0,0,0);
	
	public void timeMatrix33Transform(int runs) {
		Vector3 v=new Vector3(1,2,3);
		Matrix33 m=new Matrix33(1,2,3,4,5,6,7,8,9);
		
		for (int i=0; i<runs; i++) {
			r.set(m.transform(v));
		}		
	}
	
	public void timeMatrix33TransformInPlace(int runs) {
		Vector3 v=new Vector3(1,2,3);
		Vector3 t=new Vector3(1,2,3);
		Matrix33 m=new Matrix33(1,2,3,4,5,6,7,8,9);
		
		for (int i=0; i<runs; i++) {
			t.set(v);
			m.transformInPlace(t);
		}
		
		r.set(t);
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ThreeDBenchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
