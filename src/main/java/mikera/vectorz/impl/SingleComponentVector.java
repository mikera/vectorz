package mikera.vectorz.impl;

import mikera.vectorz.AVector;

/**
 * A mutable vector that always has one non-zero components.
 * All other components are forced to remain at zero.
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public final class SingleComponentVector extends AVector {
	private final int dimensions;
	private final int index;
	private double value;
	
	public SingleComponentVector(int componentIndex, int dimensions) {
		this.dimensions=dimensions;
		this.index=componentIndex;
	}

	@Override
	public int length() {
		return dimensions;
	}

	@Override
	public double get(int i) {
		assert((i>=0)&&(i<dimensions));
		return (i==index)?value:0.0;
	}

	@Override
	public void set(int i, double value) {
		assert((i>=0)&&(i<dimensions));
		if (i==index) this.value=value;
	}

}
