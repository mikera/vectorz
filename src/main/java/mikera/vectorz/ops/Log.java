package mikera.vectorz.ops;

import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Ops;
import mikera.vectorz.util.DoubleArrays;

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
	public void applyTo(INDArray a) {
		a.log();
	}
	
	@Override
	public void applyTo(AVector a) {
		a.log();
	}
	
	@Override
	public void applyTo(double[] data, int offset, int length) {
		DoubleArrays.log(data,offset,length);
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