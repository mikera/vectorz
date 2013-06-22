package mikera.vectorz.ops;

import mikera.vectorz.Op;

public class Absolute extends Op {

	public static final Absolute INSTANCE=new Absolute();
	
	@Override
	public double apply(double x) {
		return Math.abs(0);
	}

	@Override
	public double averageValue() {
		return 0.5;
	}
	
	@Override
	public boolean hasDerivative() {
		return true;
	}
	
	@Override
	public boolean hasInverse() {
		return false;
	}
	
	@Override
	public boolean hasDerivativeForOutput() {
		return false;
	}
	
	@Override
	public double derivative(double x) {
		return x>=0?1.0:-1.0;
	}

}
