package mikera.vectorz.performance;

import java.util.ArrayList;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

/**
 * Caliper based benchmarks for sublist iteration
 * 
 * See debate at: http://stackoverflow.com/questions/17302130/enhanced-for-loop/17302215
 * 
 * @author Mike
 */

public class ListBenchmark extends SimpleBenchmark {
	int result;
	
	int LIST_SIZE=8;
	//int LIST_SIZE=320;
	
	// about 13ns per iteration
	public void timeIndexed(int runs) {
		ArrayList<Integer> calc = new ArrayList<Integer>();
		for (int i=0; i<LIST_SIZE; i++) calc.add(i);
		
		for (int i=0; i<runs; i++) {
			int n=calc.size(); 
			for (int j=2; j<n; j++) {
				result+=calc.get(j);
			}
		}
	}
	
	public void timeIndexedWithSize(int runs) {
		ArrayList<Integer> calc = new ArrayList<Integer>();
		for (int i=0; i<LIST_SIZE; i++) calc.add(i);
		
		for (int i=0; i<runs; i++) {
			for (int j=2; j<calc.size(); j++) {
				result+=calc.get(j);
			}
		}
	}
	
	// about 33 ns per iteration
	public void timeIterator(int runs) {
		ArrayList<Integer> calc = new ArrayList<Integer>();
		for (int i=0; i<LIST_SIZE; i++) calc.add(i);
		
		for (int i=0; i<runs; i++) {
			int n=calc.size(); 
			for (Integer j: calc.subList(2, n)) {
				result+=j;
			}
		}
	}
	

	public static void main(String[] args) {
		new ListBenchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
