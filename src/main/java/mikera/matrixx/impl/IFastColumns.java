package mikera.matrixx.impl;

import mikera.matrixx.IMatrix;
import mikera.vectorz.AVector;

/**
 * Marker interface for matrices with fast column access
 * 
 * General intention is that any matrix that implements this must have a fast getColumn(), typically
 * - O(1) time and space
 * - An efficient vector type returned
 * 
 * @author Mike
 *
 */
public interface IFastColumns extends IMatrix {
	/**
	 * Gets a column of this matrix. Guranteed to be efficient as part of IFastColumns interface
	 */
	@Override
	public AVector getColumn(int i);

}
