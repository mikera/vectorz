package mikera.vectorz.performance;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Benchmark to test the difference between interface and virtual dispatch.
 *
 * This motivates preferring "AVector" to "IVector" where possible to obtain
 * polymorphic behaviour in the Vectorz library.
 *
 * Example results under the original Caliper harness:
 *
 * <pre>
 * benchmark    ns linear runtime
 * Interface 13.04 ==============================
 *   Virtual  7.39 =================
 * </pre>
 *
 * @author Mike
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class InterfaceVsVirtualBenchmark {

	public static interface I {
		public long foo(Long a);
	}

	public static class A implements I {
		@Override
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

	private int counter;

	@Setup(Level.Iteration)
	public void setup() {
		counter=0;
	}

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

	@Benchmark
	public long interfaceDispatch() {
		int i=counter++;
		I object=getInterfaceObject(i);
		return object.foo((long)i);
	}

	@Benchmark
	public long virtualDispatch() {
		int i=counter++;
		A object=getVirtualObject(i);
		return object.foo((long)i);
	}

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
				.include(InterfaceVsVirtualBenchmark.class.getSimpleName())
				.build()).run();
	}
}
