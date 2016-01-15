package mikera.vectorz.impl;

import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;

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
	
	@Override
	public AVector selectView(int... indices) {
		if (isFullyMutable()) {
			int len=indices.length;
			int[] ixs=new int[len];
			for (int i=0; i<len; i++) {
				ixs[i]=index(indices[i]);
			}
			return IndexedArrayVector.wrap(data, ixs);
		} else {
			return super.select(indices);
		}
	}
	
	/**
	 * Computes an index into the underlying array for a given vector index
	 */
	protected abstract int index(int i);

}
