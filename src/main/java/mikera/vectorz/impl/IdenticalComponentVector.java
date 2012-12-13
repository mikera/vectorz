package mikera.vectorz.impl;

import mikera.vectorz.AVector;

/**
 * A mutable vector that always has identical components.
 * Setting one component will set all components.
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public final class IdenticalComponentVector extends AVector {
	private final int dimensions;
	private double value;
	
	public IdenticalComponentVector(int dims) {
		this.dimensions=dims;
	}
	
	public IdenticalComponentVector(int dims, double value) {
		this.dimensions=dims;
		this.value=value;
	}
	
	public static IdenticalComponentVector create(int dims, double value) {
		IdenticalComponentVector r=new IdenticalComponentVector(dims);
		r.value=value;
		return r;
	}

	@Override
	public int length() {
		return dimensions;
	}

	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public double get(int i) {
		assert((i>=0)&&(i<dimensions));
		return value;
	}
	
	@Override
	public void scale(double factor) {
		value*=factor;
	}

	@Override
	public void set(int i, double value) {
		assert((i>=0)&&(i<dimensions));
		this.value=value;
	}

}
