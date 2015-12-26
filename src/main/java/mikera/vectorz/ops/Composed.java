package mikera.vectorz.ops;

import mikera.vectorz.Op;

public class Composed extends Op {
	public final Op inner;
	public final Op outer;
	
	private Composed(Op outer, Op inner) {
		this.outer=outer;
		this.inner=inner;
	}
	
	public static Op compose(Op outer, Op inner) {
		if (inner instanceof Composed) {
			Composed ci=(Composed)inner;
			return outer.compose(ci.outer).compose(ci.inner);
		}
		
		return new Composed(outer,inner);
	}
	
	public static Op create(Op a, Op b) {
		return compose(a,b);
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
	public boolean hasDerivativeForOutput() {
		return outer.hasInverse()&&(outer.hasDerivativeForOutput())&&(inner.hasDerivativeForOutput());
	}
	
	@Override
	public double derivativeForOutput(double y) {
		return outer.derivativeForOutput(y)*inner.derivativeForOutput(outer.applyInverse(y));
	}
	
	@Override
	public double derivative(double x) {
		double y=inner.apply(x);
		return outer.derivative(y)*inner.derivative(x);
	}
	
	@Override
	public Op getDerivativeOp() {
		return (outer.getDerivativeOp().compose(inner)).product(inner.getDerivativeOp());
	}
	
	@Override
	public double maxValue() {
		return outer.maxValue();
	}
	
	@Override
	public boolean hasInverse() {
		return (outer.hasInverse())&&(inner.hasInverse());
	}
	
	@Override
	public boolean isStochastic() {
		return (outer.isStochastic())||(inner.isStochastic());
	}
	
	@Override
	public Op getInverse() {
		Op innerInv=inner.getInverse();
		Op outerInv=outer.getInverse();
		if ((outerInv==null)||(innerInv==null)) return null;
		
		return innerInv.compose(outerInv);
	}
	
	@Override
	public double averageValue() {
		return outer.averageValue();
	}
	
	
	@Override public String toString() {
		return "Composed("+outer+","+inner+")";
	}
	
}
