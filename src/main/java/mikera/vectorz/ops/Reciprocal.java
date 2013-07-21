package mikera.vectorz.ops;

import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.util.DoubleArrays;

public final class Reciprocal extends AFunctionOp {
	public static final Reciprocal INSTANCE = new Reciprocal();

	@Override
	public void applyTo(INDArray a) {
		a.reciprocal();
	}
	
	@Override
	public void applyTo(AVector a) {
		a.reciprocal();
	}
	
	@Override
	public void applyTo(double[] data, int start,int length) {
		DoubleArrays.reciprocal(data, start, length);
	}
	
	@Override
	public double apply(double x) {
		return 1.0/x;
	}

	@Override
	public double derivative(double x) {
		return -1.0/(x*x);
	}

	@Override
	public double derivativeForOutput(double y) {
		return -y*y;
	}

	@Override public double averageValue() {return 1.0;}

	@Override public boolean hasInverse() {return true;}

	@Override public Op getInverse() {return this;}

	@Override public boolean hasDerivative() {return true;}
}