package mikera.vectorz.performance;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

import mikera.vectorz.AVector;
import mikera.vectorz.Op2;
import mikera.vectorz.Ops;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;

/**
 * Benchmark for Op2 performance
 * 
 * @author Mike
 */

public class Op2Benchmark extends SimpleBenchmark {
	public static final int VECTOR_SIZE = 1000;
	public static AVector a=Vector.createLength(VECTOR_SIZE);
	public static AVector b=Vector.createLength(VECTOR_SIZE);
	public static Op2 op=Ops.MAX;

	static {
		Vectorz.fillGaussian(a);
		Vectorz.fillGaussian(b);		
	}
	
	public volatile double output=0.0;
	
	public void timeOp(int runs) {
		AVector t=Vector.createLength(VECTOR_SIZE);
		for (int run=0; run<runs; run++) {
			output=t.reduce(op,0.0);
		}
	}
	
	public void timeElementMax(int runs) {
		AVector t=Vector.createLength(VECTOR_SIZE);
		for (int run=0; run<runs; run++) {
			output=t.elementMax();
		}
	}
	
	public void timeElementwise(int runs) {
		AVector t=Vector.createLength(VECTOR_SIZE);
		for (int run=0; run<runs; run++) {
			double result=Double.NEGATIVE_INFINITY;
			for (int j=0; j<VECTOR_SIZE; j++) {
				result=Math.max(result,t.unsafeGet(j));
			}
			output=result;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Op2Benchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
