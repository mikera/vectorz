package mikera.vectorz.ops;

import mikera.vectorz.Op;

public class ComposedOp extends Op {
	public final Op inner;
	public final Op outer;
	
	private ComposedOp(Op outer, Op inner) {
		this.outer=outer;
		this.inner=inner;
	}
	
	public static ComposedOp compose(Op outer, Op inner) {
		return new ComposedOp(outer,inner);
	}

	@Override
	public double apply(double x) {
		return outer.apply(inner.apply(x));
	}
	
	@Override
	public void applyTo(double[] data, int start,int length) {
		inner.applyTo(data, start, length);
		outer.applyTo(data, start, length);
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
	public boolean hasDerivative() {
		return (outer.hasDerivative())&&(inner.hasDerivative());
	}
	
	@Override
	public double derivativeForOutput(double y) {
		return outer.derivativeForOutput(y)*inner.derivativeForOutput(outer.applyInverse(y));
	}
	
	@Override
	public double derivative(double x) {
		double y=inner.apply(x);
		return outer.derivative(y)*inner.derivativeForOutput(y);
	}
	
	@Override
	public double maxValue() {
		return outer.maxValue();
	}
	
	@Override
	public boolean hasInverse() {
		return (outer.hasInverse())&&(inner.hasInverse());
	}
	
	public boolean isStochastic() {
		return (outer.isStochastic())||(inner.isStochastic());
	}
	
	@Override
	public Op getInverse() {
		return inner.getInverse().compose(outer.getInverse());
	}
	
	@Override
	public double averageValue() {
		return outer.averageValue();
	}
	
}
