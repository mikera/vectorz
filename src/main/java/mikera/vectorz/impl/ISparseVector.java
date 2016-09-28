package mikera.vectorz.impl;

import mikera.arrayz.ISparse;
import mikera.indexz.Index;
import mikera.vectorz.AVector;
import mikera.vectorz.IVector;

public interface ISparseVector extends IVector, ISparse{

	/**
	 * Coerces this vector to a SparseIndexedVector. May return this vector if already a SparseIndexedVector
	 * @return
	 */
	SparseIndexedVector toSparseIndexedVector();

	/**
	 * Returns the non-sparse elements as a compacted vector. May or may not be a view.
	 * @return
	 */
	public AVector nonSparseValues();
	
	/**
	 * Returns an Index specifying the non-sparse elements 
	 * @return
	 */
	public Index nonSparseIndex();
}
