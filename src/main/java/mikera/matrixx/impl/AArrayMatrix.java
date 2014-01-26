package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.util.ErrorMessages;

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
	
	public double[] getArray() {
		return data;
	}
	
	@Override
	public double get(int i, int j) {
		if ((i<0)||(i>=rows)||(j<0)||(j>=cols)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i,j));
		return data[index(i,j)];
	}
	
	@Override
	public void set(int i, int j,double value) {
		if ((i<0)||(i>=rows)||(j<0)||(j>=cols)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i,j));
		data[index(i,j)]=value;
	}
	
	@Override
	public double unsafeGet(int i, int j) {
		return data[index(i,j)];
	}
	
	@Override
	public void unsafeSet(int i, int j,double value) {
		data[index(i,j)]=value;
	}
	
	/**
	 * Returns true if the data array is fully packed by this matrix in row-major order
	 * @return
	 */
	public abstract boolean isPackedArray();
	
	@Override
	public AMatrix getTransposeCopy() {
		int rc=this.rowCount();
		int cc=this.columnCount();
		Matrix m=Matrix.create(cc,rc);
		for (int j=0; j<cc; j++) {
			this.copyColumnTo(j, m.data, j*rc);
		}
		return m;
	}
	
	/**
	 * Computes the index into the data array for a given position in the matrix
	 * @param i
	 * @param j
	 * @return
	 */
	protected abstract int index(int i, int j);

	@Override
	public boolean equals(AMatrix a) {
		if (a==this) return true;
		
		int rc = rowCount();
		if (rc != a.rowCount()) return false;
		int cc = columnCount();
		if (cc != a.columnCount()) return false;
		for (int i = 0; i < rc; i++) {
			for (int j = 0; j < cc; j++) {
				if (data[index(i, j)] != a.unsafeGet(i, j))
					return false;
			}
		}
		return true;
	}
}
