package mikera.vectorz.impl;

import mikera.vectorz.AVector;

/**
 * Abstract base classes for sparse vectors that have a single potentially non-zero element
 * 
 * @author Mike
 *
 */
public abstract class ASingleElementVector extends ASparseVector {
	private static final long serialVersionUID = -5246190958486810285L;

	protected final int index;

	protected ASingleElementVector(int index, int length) {
		super(length);
		this.index=index;
	}

	protected abstract double value();
	
	protected final int index() {
		return index;
	}
	
	@Override 
	public double dotProduct(AVector v) {
		return value()*v.unsafeGet(index());
	}
	
	@Override 
	public double dotProduct(double[] data, int offset) {
		return value()*data[offset+index()];
	}
}
