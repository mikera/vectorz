package mikera.vectorz.ops;

import mikera.vectorz.Op;

public class Division extends Op {
	public final Op a;
	public final Op b;
	
	private Division(Op a, Op b) {
		this.a=a;
		this.b=b;
	}
	
	private static Op tryOptimisedCreate(Op a, Op b) {
		if (b instanceof Division) {
			Division d=(Division)b;
			return new Division(Product.create(a,d.b),d.a);
		}
		return null;
	}
	
	public static Op create(Op a, Op b) {
		Op t1=tryOptimisedCreate(a,b);
		if (t1!=null) return t1;
		
		return new Division(a,b);
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
	public double derivative(double x) {
		double ay=a.apply(x);
		double by=b.apply(x);
		return a.derivative(x)*by-ay/b.derivative(x);
	}
	
	@Override public String toString() {
		return "Division("+a+","+b+")";
	}
}
