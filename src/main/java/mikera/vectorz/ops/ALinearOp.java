package mikera.vectorz.ops;

import mikera.vectorz.Op;

public abstract class ALinearOp extends Op {

	public abstract double getFactor();
	
	public abstract double getConstant();
	
	@Override
	public double derivative(double x) {
		return getFactor();
	}
	
	@Override
	public double derivativeForOutput(double y) {
		return getFactor();
	}
}
