package mikera.vectorz.performance;

import java.util.Arrays;

import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.util.DoubleArrays;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

/**
 * Benchmark for generation of new vectors 
 * 
 * See debate at: http://stackoverflow.com/questions/17302130/enhanced-for-loop/17302215
 * 
 * @author Mike
 */
@SuppressWarnings("unused")
public class NewVectorBenchmark extends SimpleBenchmark {
	int result;
	
	int LIST_SIZE=100;
	
	AVector preAllocated=Vector.createLength(LIST_SIZE);

	public void timeNewVector(int runs) {
		AVector res=null;
		for (int i=0; i<runs; i++) {
			res=Vector.createLength(LIST_SIZE);
		}
		result=res.length();
	}
	
	public void timeNewDoubleArray(int runs) {
		double[] res=null;
		for (int i=0; i<runs; i++) {
			res=new double[LIST_SIZE];
		}
		result=res.length;
	}
	
	public void timeZeroVector(int runs) {
		for (int i=0; i<runs; i++) {
			preAllocated.set(0.0);
		}
		result=preAllocated.length();
	}
	
	public static void main(String[] args) {
		new NewVectorBenchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
