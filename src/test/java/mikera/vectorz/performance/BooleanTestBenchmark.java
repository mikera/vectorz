package mikera.vectorz.performance;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

/**
 * Test to measure the cost of a never-taken boolean branch in a tight inner loop.
 * 
 * @author Mike
 */

public class BooleanTestBenchmark extends SimpleBenchmark {
	int result;
	
	final int SIZE=100;
	int[] dat=new int[SIZE];
	
	private int basic(boolean b) {
		for (int j=0; j<SIZE-1; j++) {
			dat[j]=dat[j+1];
		}		
		return 0;
	}
	
	private int withTest(boolean b) {
		for (int j=0; j<SIZE-1; j++) {
			dat[j]=dat[j+1];
			if (b) {
				return 1;
			}		
		}		
		return 0;
	}
	
	// about 13ns per iteration
	public void timeBasic(int runs) {
		for (int i=0; i<runs; i++) {
			result=basic(false);
		}
	}
	
	public void timeWithTest(int runs) {
		for (int i=0; i<runs; i++) {
			result=withTest(false);
		}
	}

	public static void main(String[] args) {
		new BooleanTestBenchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
