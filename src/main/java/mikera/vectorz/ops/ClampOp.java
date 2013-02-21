package mikera.vectorz.ops;

/**
 * Operator for clamping values within a given range
 * @author Mike
 *
 */
public final class ClampOp extends ABoundedOp {
	private final double min;
	private final double max;
	
	public static final ClampOp ZERO_TO_ONE=new ClampOp(0,1);

	public ClampOp(double min, double max) {
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
