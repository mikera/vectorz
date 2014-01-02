package mikera.matrixx.impl;

import mikera.matrixx.IMatrix;
import mikera.vectorz.AVector;

/**
 * Marker interface for matrices with fast column access
 * @author Mike
 *
 */
public interface IFastColumns extends IMatrix {
	
	public AVector getColumn(int i);

}
