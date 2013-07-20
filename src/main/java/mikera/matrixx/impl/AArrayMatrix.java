package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

/**
 * Abstract class for a Matrix backed with a single double[] data array
 * @author Mike
 *
 */
public abstract class AArrayMatrix extends AMatrix {
	protected final int rows;
	protected final int cols;
	public final double[] data;

	protected AArrayMatrix(double[] data, int rows, int cols ) {
		this.rows=rows;
		this.cols=cols;
		this.data=data;
	}
	
	public abstract boolean isPackedArray();
}
