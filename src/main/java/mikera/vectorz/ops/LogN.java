package mikera.vectorz.ops;

import mikera.vectorz.Op;
import mikera.vectorz.Ops;

/**
 * The renowned exponential function
 * 
 * @author Mike
 *
 */
public final class LogN extends AFunctionOp {
	private final double base;
	private final double logBase;
	
	public static final LogN LOG10=create(10);
	
	private LogN(double base) {
		this.base=base;
		this.logBase=Math.log(base);
	}
	
	public static LogN create(double base) {
		return new LogN(base);
	}
	
	public double getBase() {
		return base;
	}
	
	@Override
	public double apply(double x) {
		return Math.log(x)/logBase;
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
		return Math.exp(y*logBase);
	}

	@Override public boolean hasDerivative() {return true;}
	@Override public boolean hasInverse() {return true;}

	@Override public double minDomain() {return Double.MIN_VALUE;}

	@Override public Op getInverse() {return Exp.INSTANCE.compose(Linear.create(logBase, 0.0));}
	@Override public Op getDerivativeOp() {return Ops.RECIPROCAL;}
}