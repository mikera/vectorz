package mikera.vectorz;

import mikera.vectorz.ops.AFunctionOp;

/**
 * The renowned exponential function
 * 
 * @author Mike
 *
 */
public final class Exp extends AFunctionOp {
	@Override
	public double apply(double x) {
		return Math.exp(x);
	}

	@Override
	public double derivative(double x) {
		return Math.exp(x);
	}

	@Override
	public double derivativeForOutput(double y) {
		return y;
	}
	
	@Override public double applyInverse(double y) {
		return Math.log(y);
	}

	@Override public boolean hasDerivative() {return true;}
	@Override public boolean hasInverse() {return true;}

	@Override public double minValue() {return 0.0;}

	@Override public Op getDerivativeOp() {return this;}
}