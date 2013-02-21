package mikera.vectorz.ops;

import mikera.vectorz.Op;

/**
 * Operator for clamping values within a given range
 * @author Mike
 *
 */
public final class ClampOp extends Op {
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
	public boolean isBounded() {
		return true;
	}
	
	@Override
	public double minValue() {
		return min;
	}

	@Override
	public double maxValue() {
		return max;
	}
}
