package mikera.vectorz.ops;

import mikera.arrayz.INDArray;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * Operator for clamping values within a given range
 * @author Mike
 *
 */
public final class Clamp extends ABoundedOp {
	private final double min;
	private final double max;
	
	public static final Clamp ZERO_TO_ONE=new Clamp(0,1);

	public Clamp(double min, double max) {
		this.min=min;
		this.max=max;
	}

	@Override
	public double apply(double x) {
		if (x<=min) return min;
		if (x>=max) return max;
		return x;
	}
	
	@Override
	public void applyTo(INDArray v) {
		v.clamp(min, max);
	}
	
	@Override
	public void applyTo(AVector v) {
		v.clamp(min, max);
	}
	
	@Override
	public void applyTo(AMatrix v) {
		v.clamp(min, max);
	}
	
	@Override
	public void applyTo(double[] data, int start,int length) {
		for (int i=0; i<length; i++) {
			double x=data[start+i];
			data[start+i]=(x<min) ? min : ((x>max)?max:x);
		}
	}
	
	@Override
	public double minValue() {
		return min;
	}

	@Override
	public double maxValue() {
		return max;
	}
	
	@Override
	public boolean hasDerivative() {
		return true;
	}
	
	@Override
	public double derivative(double x) {
		if ((x<=min)||(x>=max)) return 0.0;
		return 1.0;
	}
	
	@Override
	public double derivativeForOutput(double y) {
		if ((y<=min)||(y>=max)) return 0.0;
		return 1.0;
	}
}
