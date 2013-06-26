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
	
	public void timeIndexed(int runs) {
		ArrayList<Integer> calc = new ArrayList<Integer>();
		for (int i=0; i<8; i++) calc.add(i);
		
		for (int i=0; i<runs; i++) {
			int n=calc.size(); 
			for (int j=2; j<n; j++) {
				result+=calc.get(j);
			}
		}
	}
	
	public void timeIterator(int runs) {
		ArrayList<Integer> calc = new ArrayList<Integer>();
		for (int i=0; i<8; i++) calc.add(i);
		
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
