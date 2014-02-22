package mikera.vectorz.ops;

import mikera.vectorz.AVector;
import mikera.vectorz.util.DoubleArrays;

public final class Tanh extends ABoundedOp {
	
	public static final Tanh INSTANCE=new Tanh();
	
	@Override
	public double apply(double x) {
		return Math.tanh(x);
	}
	
	@Override
	public void applyTo(AVector v) {
		v.tanh();
	}
	
	@Override
	public void applyTo(double[] data) {
		DoubleArrays.tanh(data);
	}
	
	@Override
	public void applyTo(double[] data, int start,int length) {
		DoubleArrays.tanh(data, start, length);
	}
	
	@Override
	public boolean hasDerivative() {
		return true;
	}
	
	@Override
	public double derivativeForOutput(double y) {
		return 1.0-y*y;
	}
	
	@Override
	public double derivative(double x) {
		double y=Math.tanh(x);
		return 1.0-y*y;
	}

	@Override
	public double minValue() {
		return -1.0;
	}
	
	@Override
	public double averageValue() {
		return 0.0;
	}

	@Override
	public double maxValue() {
		return 1.0;
	}

}
