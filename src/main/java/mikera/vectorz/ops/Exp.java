package mikera.vectorz.ops;

import mikera.arrayz.INDArray;
import mikera.vectorz.Op;
import mikera.vectorz.util.DoubleArrays;

/**
 * The renowned exponential function
 * 
 * @author Mike
 *
 */
public final class Exp extends AFunctionOp {
	public static final Exp INSTANCE=new Exp();
	
	@Override
	public void applyTo(INDArray a) {
		a.exp();
	}
	
	@Override
	public void applyTo(double[] data, int offset, int length) {
		DoubleArrays.exp(data,offset,length);
	}
	
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
	@Override public Op getInverse() {return Log.INSTANCE;}

	@Override public double minValue() {return 0.0;}

	@Override public Op getDerivativeOp() {return this;}
}