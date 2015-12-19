package mikera.vectorz.performance;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

import mikera.vectorz.Op2;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.ops.AddFunction;
import mikera.vectorz.ops.Power;

/**
 * Caliper based benchmarks
 * 
 * @author Mike
 */

public class OpBenchmark extends SimpleBenchmark {
	private static final int VECTOR_SIZE = 1000;
	private static final Vector a=Vector.createLength(VECTOR_SIZE);
	private static final Vector b=Vector.createLength(VECTOR_SIZE);
	private static final Op2 op=AddFunction.create(0.5,0.5,Power.create(3.0));

	static {
		Vectorz.fillGaussian(a);
		Vectorz.fillGaussian(b);		
	}
	
	public volatile double output=0.0;
	
	// op benchmark for addPower function with scaling 0.5,0.5, exponent 3.0
	
	public void timeOp(int runs) {
		Vector t=Vector.createLength(VECTOR_SIZE);
		for (int run=0; run<runs; run++) {
			t.set(a);
			t.applyOp(op, b);
			output=t.get(0);
		}
	}
	
	public void timeManual(int runs) {
		Vector t=Vector.createLength(VECTOR_SIZE);
		for (int run=0; run<runs; run++) {
			t.set(b);
			t.pow(3.0);
			t.scale(0.5);
			t.addMultiple(a, 0.5);
			output=t.get(0);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new OpBenchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
