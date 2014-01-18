package mikera.vectorz.impl;

import mikera.arrayz.ISparse;
import mikera.indexz.Index;
import mikera.vectorz.AVector;

/**
 * Abstract base class for Sparse vector implementations
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public abstract class ASparseVector extends ASizedVector implements ISparse {

	public ASparseVector(int length) {
		super(length);
	}

	/**
	 * Returns the number of non-sparse elements in the sparse vector.
	 * @return
	 */
	public abstract int nonSparseElementCount();
	
	/**
	 * Returns the non-sparse values as a compacted vector view
	 * @return
	 */
	public abstract AVector nonSparseValues();
	
	/**
	 * Returns the non-sparse indexes
	 */
	@Override
	public abstract Index nonSparseIndexes();
	
	/**
	 * Returns true iff the sparse vector contains the index i 
	 * @param i
	 * @return
	 */
	public abstract boolean includesIndex(int i);
	
	// ========================================
	// standard implementations
	
	@Override
	public double density() {
		return ((double)(nonSparseValues().length()))/length();
	}

	public abstract void add(ASparseVector v);
}
