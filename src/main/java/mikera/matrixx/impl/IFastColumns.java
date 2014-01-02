package mikera.matrixx.impl;

import mikera.matrixx.IMatrix;

/**
 * Marker interface for matrices with fast column access
 * @author Mike
 *
 */
public interface IFastColumns extends IMatrix {
	
	public int getColumn(int i);

}
