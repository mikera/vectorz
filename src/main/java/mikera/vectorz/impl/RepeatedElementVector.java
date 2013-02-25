package mikera.vectorz.impl;

import mikera.vectorz.AVector;

/**
 * A mutable vector that always has a single repeated component.
 * Setting any component will therefore set all components.
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public final class RepeatedElementVector extends AVector {
	private final int dimensions;
	private double value;
	
	public RepeatedElementVector(int dims) {
		this.dimensions=dims;
	}
	
	public RepeatedElementVector(int dims, double value) {
		this.dimensions=dims;
		this.value=value;
	}
	
	public static RepeatedElementVector create(int dims, double value) {
		RepeatedElementVector r=new RepeatedElementVector(dims);
		r.value=value;
		return r;
	}

	@Override
	public int length() {
		return dimensions;
	}

	@Override
	public boolean isFullyMutable() {
		// not fully mutable, since elements are tied to each other
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

	@Override 
	public RepeatedElementVector exactClone() {
		return new RepeatedElementVector(dimensions,value);
	}
}
