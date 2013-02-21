package mikera.vectorz.ops;

import mikera.vectorz.Op;

public class ComposedOp extends Op {
	private final Op inner;
	private final Op outer;
	
	public ComposedOp(Op outer, Op inner) {
		this.outer=outer;
		this.inner=inner;
	}

	@Override
	public double apply(double x) {
		return outer.apply(inner.apply(x));
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
	public double maxValue() {
		return outer.maxValue();
	}
	
	
	
}
