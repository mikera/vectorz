package mikera.vectorz.performance;

import java.util.Arrays;

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

public class CloneBenchmark extends SimpleBenchmark {
	int result;
	
	int LIST_SIZE=100;

	public void timeCloneArray(int runs) {
		Vector v=Vector.createLength(LIST_SIZE);
		
		Vector res=v;
		for (int i=0; i<runs; i++) {
			res=Vector.wrap(res.data.clone());
		}
		result=v.length();
	}
	
	public void timeCopyOfArray(int runs) {
		Vector v=Vector.createLength(LIST_SIZE);
		
		Vector res=v;
		for (int i=0; i<runs; i++) {
			res=Vector.wrap(Arrays.copyOf(res.data, res.data.length));
		}
		result=v.length();
	}
	
	public void timeDoubleArraysCopy(int runs) {
		Vector v=Vector.createLength(LIST_SIZE);
		
		Vector res=v;
		for (int i=0; i<runs; i++) {
			res=Vector.wrap(DoubleArrays.copyOf(res.data));
		}
		result=v.length();
	}
	
	public void timeArrayCopyClone(int runs) {
		Vector v=Vector.createLength(LIST_SIZE);
		
		Vector res=v;
		for (int i=0; i<runs; i++) {
			double[] ds=new double[res.data.length];
			System.arraycopy(res.data, 0, ds, 0, res.data.length);
			res=Vector.wrap(ds);
		}
		result=v.length();
	}

	public static void main(String[] args) {
		new CloneBenchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
