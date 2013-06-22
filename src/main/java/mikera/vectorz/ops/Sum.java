package mikera.vectorz.ops;

import mikera.vectorz.Op;

public final class Sum extends Op {
	public final Op a;
	public final Op b;
	
	private Sum(Op a, Op b) {
		this.a=a;
		this.b=b;
	}
	
	public static Op create(Op a, Op b) {
		if ((b instanceof ALinearOp)&&(!(a instanceof ALinearOp))) {
			return b.sum(a);
		}
		
		return new Sum(a,b);
	}

	@Override
	public double apply(double x) {
		return a.apply(x)+b.apply(x);
	}

	@Override
	public double averageValue() {
		return a.averageValue()+b.averageValue();
	}
	
	@Override
	public boolean hasDerivative() {
		return (a.hasDerivative())&&(b.hasDerivative());
	}
	
	@Override
	public double derivative(double x) {
		return a.derivative(x)+b.derivative(x);
	}
	
	@Override
	public Op getDerivativeOp() {
		return a.getDerivativeOp().sum(b.getDerivativeOp());
	}
	
	@Override
	public boolean isStochastic() {
		return a.isStochastic()||b.isStochastic();
	}
	
	@Override public String toString() {
		return "Sum("+a+","+b+")";
	}
}
