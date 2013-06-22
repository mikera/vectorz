package mikera.vectorz.ops;

import mikera.vectorz.Op;
import mikera.vectorz.Ops;

public final class Division extends Op {
	public final Op a;
	public final Op b;
	
	private Division(Op a, Op b) {
		this.a=a;
		this.b=b;
	}
	
	private static Op tryOptimisedCreate(Op a, Op b) {
		if (a instanceof Division) {
			Division d=(Division)a;
			return Division.create(d.a,Product.create(a, b));
		}
		if (b instanceof Division) {
			Division d=(Division)b;
			return Division.create(Product.create(a,d.b),d.a);
		}
		return null;
	}
	
	public static Op create(Op a, Op b) {
		Op t1=tryOptimisedCreate(a,b);
		if (t1!=null) return t1;
		
		return new Division(a,b);
	}
	
	@Override
	public boolean isStochastic() {
		return a.isStochastic()||b.isStochastic();
	}
	
	@Override
	public double apply(double x) {
		return a.apply(x)/b.apply(x);
	}

	@Override
	public double averageValue() {
		return a.averageValue()/b.averageValue();
	}
	
	@Override
	public boolean hasDerivative() {
		return (a.hasDerivative())&&(b.hasDerivative());
	}
	
	@Override
	public boolean hasDerivativeForOutput() {
		return false;
	}
	
	@Override
	public double derivative(double x) {
		double ay=a.apply(x);
		double by=b.apply(x);
		return a.derivative(x)*by-ay/b.derivative(x);
	}
	
	@Override
	public Op getDerivativeOp() {
		return Ops.sum(a.getDerivativeOp().product(b),Ops.divide(Ops.NEGATE.compose(a), b.getDerivativeOp()));
	}
	
	@Override public String toString() {
		return "Division("+a+","+b+")";
	}
}
