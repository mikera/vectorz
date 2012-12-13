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

	@Override
	public int length() {
		return dimensions;
	}

	@Override
	public double get(int i) {
		assert((i>=0)&&(i<dimensions));
		return value;
	}

	@Override
	public void set(int i, double value) {
		this.value=value;
	}

}
