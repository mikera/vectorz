package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

/**
 * Abstract class for a Matrix backed with a single double[] data array
 * 
 * Dimensions are fixed, but leaves open the possibility of arbitrary indexing
 * 
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
	
	public final int rowCount() {
		return rows;
	}
	
	public final int columnCount() {
		return cols;
	}
	
	/**
	 * Returns true if the data array is fully packed by this matrix in row-major order
	 * @return
	 */
	public abstract boolean isPackedArray();
	
	/**
	 * Computes the index into the data array for a given position in the matrix
	 * @param i
	 * @param j
	 * @return
	 */
	protected abstract int index(int i, int j);
}
