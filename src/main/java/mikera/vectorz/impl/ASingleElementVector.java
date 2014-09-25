package mikera.vectorz.impl;

import mikera.indexz.Index;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector1;
import mikera.vectorz.util.IntArrays;

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

	// =============================================
	// Abstract / standard methods

	protected abstract double value();
	
	protected final int index() {
		return index;
	}
	
	// =============================================
	// Generic implementations
	
	@Override 
	public double dotProduct(AVector v) {
		return value()*v.unsafeGet(index());
	}
	
	@Override 
	public double dotProduct(double[] data, int offset) {
		return value()*data[offset+index()];
	}
	
	@Override
	public int nonSparseElementCount() {
		return 1;
	}

	@Override
	public AVector nonSparseValues() {
		return Vector1.of(value());
	}

	@Override
	public Index nonSparseIndex() {
		return Index.of(index);
	}
	
	@Override
	public boolean equals(AVector v) {
		int len=v.length();
		if (len!=this.length) return false;
		
		if (v.unsafeGet(index)!=value()) return false;
		if (!v.isRangeZero(0, index)) return false;
		if (!(v.isRangeZero(index+1, len-(index+1)))) return false;
		return true;
	}
	
	@Override
	public int[] nonZeroIndices() {
		if (value()==0.0) {
			return IntArrays.EMPTY_INT_ARRAY;
		} else {
			return new int[]{index};
		}
	}
}
