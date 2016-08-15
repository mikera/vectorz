package mikera.matrixx.impl;

import mikera.matrixx.Matrix;

/**
 * Abstract class for a Matrix backed with a single double[] data array
 * 
 * Dimensions are fixed, but leaves open the possibility of arbitrary indexing
 * 
 * @author Mike
 *
 */
public abstract class AArrayMatrix extends ARectangularMatrix {
	private static final long serialVersionUID = 7423448070352281717L;

	public final double[] data;

	protected AArrayMatrix(double[] data, int rows, int cols ) {
		super(rows,cols);
		this.data=data;
	}
	
	/**
	 * Gets the underlying data array for this array-backed matrix.
	 * 
	 * Mutation of this data array is possible, but caution is advised as this may cause 
	 * unpredictable side effects if the array is aliased elsewhere
	 * @return
	 */
	public final double[] getArray() {
		return data;
	}
	
	@Override
	public double get(int i, int j) {
		checkIndex(i,j);
		return data[index(i,j)];
	}
	
	@Override
	public void unsafeSet(int i, int j,double value) {
		data[index(i,j)]=value;
	}
	
	@Override
	public double unsafeGet(int i, int j) {
		return data[index(i,j)];
	}
	
	/**
	 * Returns true if the data array is fully packed by this matrix in row-major order
	 * @return
	 */
	public abstract boolean isPackedArray();
	
	@Override
	public Matrix getTransposeCopy() {
		return toMatrixTranspose();
	}
	
	/**
	 * Computes the index into the underlying data array for a given position in this array-backed matrix
	 * @param i
	 * @param j
	 * @return
	 */
	protected abstract int index(int i, int j);
}
