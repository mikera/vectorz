package mikera.vectorz.performance;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

/**
 * Benchmark to test the difference between interface and virtual dispatch.
 * 
 * This motivates preferring "AVector" to "IVector" where possible to obtain 
 * polymorphic behaviour in the Vectorz library.
 * 
 * Example results:
 * 
 *  0% Scenario{vm=java, trial=0, benchmark=Interface} 13.04 ns; σ=0.19 ns @ 10 trials
 * 50% Scenario{vm=java, trial=0, benchmark=Virtual} 7.39 ns; σ=0.26 ns @ 10 trials
 * 
 * benchmark    ns linear runtime
 * Interface 13.04 ==============================
 *   Virtual  7.39 =================
 * 
 * vm: java
 * trial: 0
 * @author Mike
 *
 */
public class InterfaceVsVirtualBenchmark extends SimpleBenchmark {

	public static interface I {
		public long foo(Long a);
	}
	
	public static class A implements I {
		public long foo(Long a) {
			return a+1;
		}
	}
	
	public static class B extends A {
		@Override
		public long foo(Long a) {
			return a+2;
		}
	}
	
	public static class C extends A implements Cloneable {
		@Override
		public long foo(Long a) {
			return a+3;
		}
	}
	
	public static class D implements Cloneable, I {
		@Override
		public long foo(Long a) {
			return a+3;
		}
	}
	
	public A a=new A();
	public B b=new B();
	public C c=new C();
	public D d=new D();
	
	public static long output=0;
	
	public I getInterfaceObject(int i) {
		switch (i&3) {
		case 0: return a;
		case 1: return b;
		default: return d;
		}
	}
	
	public A getVirtualObject(int i) {
		switch (i&3) {
		case 0: return a;
		case 1: return b;
		default: return c;
		}		
	}
	
	public void timeInterface(int runs) {
		output=0;
		for (int i=0; i<runs; i++) {
			I object=getInterfaceObject(i);
			output+=object.foo((long)i);
		}		
	}
	
	public void timeVirtual(int runs) {
		output=0;
		for (int i=0; i<runs; i++) {
			A object=getVirtualObject(i);
			output+=object.foo((long)i);
		}				
	}
	
	// performance test stub
	
	public static void main(String[] args) {
		new InterfaceVsVirtualBenchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}
}
