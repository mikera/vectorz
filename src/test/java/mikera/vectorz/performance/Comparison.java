package mikera.vectorz.performance;

import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector3;

public class Comparison {
	private static final int ITERATIONS=10000;
	private static final int REPEATS=100;
	private static final int BURN_IN=20;

	public static void main(String[] args) {
		double a=benchmark(testA);
		System.out.println ("Time per operation for test A: " + a + " ns");

		double b=benchmark(testB);
		System.out.println ("Time per operation for test B: " + b + " ns");

	}

	private static final double benchmark(Runnable a) {
		// burn-in
		for (int i=0; i<BURN_IN; i++) {
			a.run();
		}

		long time=System.nanoTime();
		for (int i=0; i<REPEATS; i++) {
			a.run();
		}
		double ns= (System.nanoTime()-time)/(1.0*ITERATIONS*REPEATS);	
		return ns;
	}


	public static final Runnable testA = new Runnable() {
		@Override
		public void run() {
			Vector3 v=Vector3.of(1,2,3);
			Vector3 v2=Vector3.of(1,2,3);

			for (int i=0; i<ITERATIONS; i++) {
				v.add(v2);
			}
		}
	};

	public static final Runnable testB = new Runnable() {
		@Override
		public void run() {
			AVector v=Vector.of(1,2,3);
			AVector v2=Vector3.of(1,2,3);

			for (int i=0; i<ITERATIONS; i++) {
				v.addMultiple(v2,2.0);
			}
		}
	};
}
