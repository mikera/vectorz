package mikera.matrixx.impl;

import mikera.matrixx.IMatrix;
import mikera.vectorz.AVector;

/**
 * Marker interface for matrices with fast row access
 * 
 * General intention is that any matrix that implements this must have a fast getRow(), typically
 * - O(1) time and space
 * - An efficient vector type returned (a strided vector or better)
 * 
 * @author Mike
 *
 */
public interface IFastRows extends IMatrix {
	/**
	 * Gets a row of this matrix. Guaranteed to be efficient as part of IFastColumns interface
	 */
	@Override
	public AVector getRow(int i);

}
