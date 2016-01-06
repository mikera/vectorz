package mikera.vectorz.ops;

import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Op2;

/**
 * Class representing a composed operation of the form  z = f(x,g(y))
 * 
 * @author Mike
 */
public class ComposedOp2 extends Op2 {
	public final Op inner;
	public final Op2 outer;
	
	private ComposedOp2(Op2 outer, Op inner) {
		this.outer=outer;
		this.inner=inner;
	}
	
	public static ComposedOp2 compose(Op2 outer, Op inner) {
		return new ComposedOp2(outer,inner);
	}
	
	public static ComposedOp2 create(Op2 a, Op b) {
		return compose(a,b);
	}

	@Override
	public double apply(double x, double y) {
		return outer.apply(x,inner.apply(y));
	}
	
	@Override
	public void applyTo(double[] data, int start, int length, AVector b) {
		b.checkLength(length);
		for (int i=0; i<length; i++) {
			double x=data[start+i];
			data[start+i]=outer.apply(x,inner.apply(b.unsafeGet(i)));
		}
	}
	
	@Override
	public void applyTo(double[] data, int start, int length, double b) {
		double bResult=inner.apply(b);
		outer.applyTo(data, start, length, bResult);
	}
	
	@Override
	public boolean isBounded() {
		return outer.isBounded();
	}
	
	@Override
	public double minValue() {
		return outer.minValue();
	}
	
	@Override
	public double maxValue() {
		return outer.maxValue();
	}
	
	@Override
	public boolean isStochastic() {
		return (outer.isStochastic())||(inner.isStochastic());
	}
	
	@Override
	public double averageValue() {
		return outer.averageValue();
	}
	
	@Override public String toString() {
		return "ComposedOp2("+outer+","+inner+")";
	}
	
}
