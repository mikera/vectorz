package mikera.matrixx.performance;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

import mikera.matrixx.Matrix33;
import mikera.vectorz.Vector3;

/**
 * Caliper based benchmarks
 * 
 * @author Mike
 */

public class ThreeDBenchmark extends SimpleBenchmark {
	public static final int VECTOR_SIZE=3;
	public static final Vector3 r=Vector3.of(0,0,0);

	public static final Vector3 smallDelta=Vector3.of(0.00001,0.00001,0.00001);

	
	public void timeMatrix33Transform(int runs) {
		Vector3 v=new Vector3(1,2,3);
		Matrix33 m=new Matrix33(1,2,3,4,5,6,7,8,9);
		
		for (int i=0; i<runs; i++) {
			v.add(smallDelta);
			r.set(m.transform(v));
		}		
	}
	
	public void timeMatrix33TransformInPlace(int runs) {
		Vector3 v=new Vector3(1,2,3);
		Vector3 t=new Vector3(1,2,3);
		Matrix33 m=new Matrix33(1,2,3,4,5,6,7,8,9);
		
		for (int i=0; i<runs; i++) {
			v.add(smallDelta);
			t.set(v);
			m.transformInPlace(t);
		}
		
		r.set(t);
	}
	
	public void timeMatrix33Clone(int runs) {
		Matrix33 m=new Matrix33(1,2,3,4,5,6,7,8,9);
		
		for (int i=0; i<runs; i++) {
			m=m.clone();
		}
	}
	
	public void timeMatrix33Determinant(int runs) {
		Matrix33 m=new Matrix33(1,2,3,4,5,6,7,8,9);
		double res=0;
		
		for (int i=0; i<runs; i++) {
			m.m00+=0.0000001;
			res+=m.determinant();
		}
		r.set(0,res);
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
