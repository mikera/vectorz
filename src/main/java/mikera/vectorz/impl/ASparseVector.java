package mikera.vectorz.impl;

import mikera.vectorz.AVector;

/**
 * Abstract base class for Sparse vector implementations
 * @author Mike
 *
 */
public abstract class ASparseVector extends AVector {
	private static final long serialVersionUID = -6043956533730989975L;

	/**
	 * Returns the number of non-sparse elements in the sparse vector.
	 * @return
	 */
	public abstract int nonSparseElementCount();
	
	public abstract AVector nonSparseView();
}
