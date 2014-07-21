package mikera.vectorz.impl;

import mikera.vectorz.AScalar;

/**
 * Base class for all vectors backed by a single final double[] array
 * 
 * Supports arbitrary indexing: array elements may not correspond to vector elements in any particular order. Ordering of elements is
 * defined by subclasses of AArrayVector.
 * 
 * @author Mike
 *
 */
public abstract class AArrayVector extends ASizedVector {
	private static final long serialVersionUID = -6271828303431809681L;

	protected final double[] data;
	
	protected AArrayVector(int length, double[] data) {
		super(length);
		this.data=data;
	}
	
	@Override
	public AScalar slice(int i) {
		checkIndex(i);
		return ArrayIndexScalar.wrap(data,index(i));
	}
	
	/**
	 * Computes an index into the underlying array for a given vector index
	 */
	protected abstract int index(int i);

}
