package mikera.vectorz.performance;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

import mikera.vectorz.AVector;
import mikera.vectorz.IVector;
import mikera.vectorz.Vector3;

/**
 * Caliper based benchmarks
 * 
 * @author Mike
 */

public class InterfacePerformanceBenchmark extends SimpleBenchmark {
	double r;
	
	public static final IVector[] ivectors=new IVector[16];
	public static final AVector[] avectors=new AVector[16];
	public static final Vector3[] vector3s=new Vector3[16];
	static {
		for (int i=0; i<ivectors.length; i++) {
			Vector3 v=new Vector3();
			ivectors[i]=v;
			avectors[i]=v;
			vector3s[i]=v;
		}		
	}
	
	public void timeIVectorAddition(int runs) {
		IVector v=Vector3.of(1,2,3);
		for (int i=0; i<runs; i++) {
			IVector v2=ivectors[i&15];
			v2.set(0,v.get(0)+v2.get(0));
		}
		r=v.get(0);
	}
	
	public void timeAVectorAddition(int runs) {
		AVector v=Vector3.of(1,2,3);
		for (int i=0; i<runs; i++) {
			AVector v2=avectors[i&15];
			v2.set(0,v.get(0)+v2.get(0));
		}
		r=v.get(0);
	}
	
	public void timeVector3Addition(int runs) {
		Vector3 v=Vector3.of(1,2,3);
		for (int i=0; i<runs; i++) {
			Vector3 v2=vector3s[i&15];
			v2.set(0,v.get(0)+v2.get(0));
		}
		r=v.get(0);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new InterfacePerformanceBenchmark().run();
	}

	void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
