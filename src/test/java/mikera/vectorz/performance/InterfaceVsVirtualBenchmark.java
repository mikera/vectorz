package mikera.vectorz.performance;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

import mikera.vectorz.AVector;
import mikera.vectorz.IVector;
import mikera.vectorz.Vector3;

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
