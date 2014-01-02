package mikera.matrixx.impl;

import mikera.matrixx.IMatrix;
import mikera.vectorz.AVector;

/**
 * Marker interface for matrices with fast row access
 * @author Mike
 *
 */
public interface IFastRows extends IMatrix {
	
	public AVector getColumn(int i);

}
