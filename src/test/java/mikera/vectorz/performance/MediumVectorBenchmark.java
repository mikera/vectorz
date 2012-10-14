package mikera.vectorz.performance;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix33;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector3;
import mikera.vectorz.Vectorz;

/**
 * Caliper based benchmarks
 * 
 * @author Mike
 */

public class MediumVectorBenchmark extends SimpleBenchmark {
	private static final int VECTOR_SIZE=20;
	
	private static final Vector source=new Vector( Vectorz.createUniformRandomVector(1000+VECTOR_SIZE));
	
	
	public void timeVectorAddition(int runs) {
		Vector v=new Vector(Vectorz.createUniformRandomVector(VECTOR_SIZE));
		Vector v2=new Vector(Vectorz.createUniformRandomVector(VECTOR_SIZE));
		for (int i=0; i<runs; i++) {
			v.add(v2);
		}
	}
	
	public void timeVectorAddProduct(int runs) {
		Vector v=new Vector(Vectorz.createUniformRandomVector(VECTOR_SIZE));
		Vector v2=new Vector(Vectorz.createUniformRandomVector(VECTOR_SIZE));
		Vector v3=new Vector(Vectorz.createUniformRandomVector(VECTOR_SIZE));
		for (int i=0; i<runs; i++) {
			v.addProduct(v2,v3);
		}
	}
	
	public void timeVectorOffsetAddition(int runs) {
		Vector v=new Vector(Vectorz.createUniformRandomVector(VECTOR_SIZE));
		for (int i=0; i<runs; i++) {
			v.add(source,100);
		}
	}
	
	public void timeJoinedVectorAddition(int runs) {
		AVector v=Vectorz.createLength(VECTOR_SIZE/2);
		v=v.join(Vectorz.createLength(VECTOR_SIZE-v.length()));

		Vector v2=new Vector(Vectorz.createUniformRandomVector(VECTOR_SIZE));
		for (int i=0; i<runs; i++) {
			v.add(v2);
		}
	}
	
	public void timeJoinedVectorSet(int runs) {
		AVector v=Vectorz.createLength(VECTOR_SIZE/2);
		v=v.join(Vectorz.createLength(VECTOR_SIZE-v.length()));

		Vector v2=new Vector(Vectorz.createUniformRandomVector(VECTOR_SIZE));
		for (int i=0; i<runs; i++) {
			v.set(v2);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MediumVectorBenchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
