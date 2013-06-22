package mikera.vectorz.ops;

import mikera.vectorz.Op;
import mikera.vectorz.Ops;

/**
 * The renowned exponential function
 * 
 * @author Mike
 *
 */
public final class Log extends AFunctionOp {
	public static final Op INSTANCE=new Log();
	
	@Override
	public double apply(double x) {
		return Math.log(x);
	}

	@Override
	public double derivative(double x) {
		return 1.0/x;
	}

	@Override
	public double derivativeForOutput(double y) {
		return 1/Math.exp(y);
	}
	
	@Override public double applyInverse(double y) {
		return Math.exp(y);
	}

	@Override public boolean hasDerivative() {return true;}
	@Override public boolean hasInverse() {return true;}

	@Override public double minDomain() {return Double.MIN_VALUE;}

	@Override public Op getInverse() {return Exp.INSTANCE;}
	@Override public Op getDerivativeOp() {return Ops.RECIPROCAL;}
}