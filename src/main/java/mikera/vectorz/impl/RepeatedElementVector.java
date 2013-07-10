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
	private final double value;
	
	public RepeatedElementVector(int dims, double value) {
		this.dimensions=dims;
		this.value=value;
	}
	
	public static RepeatedElementVector create(int dims, double value) {
		RepeatedElementVector r=new RepeatedElementVector(dims,value);
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
	public boolean isMutable() {
		return false;
	}
	
	@Override
	public double get(int i) {
		assert((i>=0)&&(i<dimensions));
		return value;
	}
	
	@Override
	public double elementSum() {
		return dimensions*value;
	}
	
	@Override
	public long nonZeroCount() {
		return (value==0)?0:dimensions;
	}

	@Override
	public void set(int i, double value) {
		throw new UnsupportedOperationException(this.getClass().toString()+" is not mutable");
	}

	@Override 
	public RepeatedElementVector exactClone() {
		return new RepeatedElementVector(dimensions,value);
	}
}
