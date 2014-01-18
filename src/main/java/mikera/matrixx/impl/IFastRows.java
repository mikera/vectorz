package mikera.matrixx.impl;

import mikera.matrixx.IMatrix;
import mikera.vectorz.AVector;

/**
 * Marker interface for matrices with fast row access
 * 
 * General intention is that any matrix that implements this must have a fast getRow(), typically
 * - At most one small object allocation
 * - An efficient vector type returned
 * 
 * @author Mike
 *
 */
public interface IFastRows extends IMatrix {
	
	public AVector getColumn(int i);

}
