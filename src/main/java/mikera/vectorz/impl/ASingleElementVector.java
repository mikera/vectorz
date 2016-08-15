package mikera.vectorz.impl;

import java.util.Arrays;

import mikera.indexz.Index;
import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector1;
import mikera.vectorz.util.IntArrays;

/**
 * Abstract base classes for immutable sparse vectors that have a single potentially non-zero element
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
	public final boolean isMutable() {
		return false;
	}
	
	@Override
	public final boolean isElementConstrained() {
		return true;
	}
	
	@Override 
	public abstract double dotProduct(AVector v);
	
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
	public long nonZeroCount() {
		return (value()==0.0)?0:1;
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		Arrays.fill(dest, offset,offset+length, 0.0);
		dest[offset+index()]=value();
	}
	
	@Override
	public AScalar slice(int i) {
		checkIndex(i);
		if (i==index) return ImmutableScalar.create(value());
		return ImmutableScalar.ZERO;
	}
	
	@Override
	public abstract boolean equals(AVector v);
	
	@Override
	public int[] nonZeroIndices() {
		if (value()==0.0) {
			return IntArrays.EMPTY_INT_ARRAY;
		} else {
			return new int[]{index};
		}
	}
}
