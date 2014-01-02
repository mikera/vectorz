package mikera.matrixx.impl;

import mikera.matrixx.IMatrix;

/**
 * Marker interface for matrices with fast row access
 * @author Mike
 *
 */
public interface IFastRows extends IMatrix {
	
	public int getColumn(int i);

}
