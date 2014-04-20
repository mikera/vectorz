package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Class for an upper triangular matrix packed densely by columns.
 * 
 * Mutable only in the upper triangular elements
 * 
 * Mostly useful for space efficiency when storing triangular matrices, but is also optimised
 * for certain common operations on triangular matrices.
 * 
 * May not be square, but must have columnCount() >= rowCount()
 * 
 * @author Mike
 *
 */
public final class UpperTriangularMatrix extends ATriangularMatrix implements IFastColumns {
	private static final long serialVersionUID = 4438118586237354484L;

	private UpperTriangularMatrix(double[] data, int rows, int cols) {
		super(data, rows, cols);
	}
	
	private UpperTriangularMatrix(int rows, int cols) {
		this (new double[(cols*(cols+1))>>1],rows,cols);
	}
	
	static UpperTriangularMatrix wrap(double[] data, int rows, int cols) {
		return new UpperTriangularMatrix(data,rows,cols);
	}
	
	public static UpperTriangularMatrix createFrom(AMatrix m) {
		int rc=m.rowCount();
		int cc=m.columnCount();
		if (rc<cc) throw new IllegalArgumentException("Insufficient rows in source matrix");
		UpperTriangularMatrix r = new UpperTriangularMatrix(rc,cc);
		for (int i=0; i<rc; i++) {
			for (int j=i; j<rc; j++) {
				r.unsafeSet(i, j, m.unsafeGet(i, j));
			}
		}
		return r;
	}
	
	@Override
	public boolean isPackedArray() {
		return false;
	}

	@Override
	protected int index(int i, int j) {
		if (i<=j) return internalIndex(i,j);
		throw new IndexOutOfBoundsException("Can't compute array index for sparse entry!");
	}
	
	private int internalIndex(int i, int j) {
		return i + ((j*(j+1))>>1);
	}
	
	@Override
	public double get(int i, int j) {
		if ((i<0)||(i>=rows)||(j<0)||(j>=cols)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i,j));
		if (i>j) return 0.0;
		return data[internalIndex(i,j)];
	}
	
	@Override
	public double unsafeGet(int i, int j) {
		if (i>j) return 0.0;
		return data[internalIndex(i,j)];
	}
	
	@Override
	public void unsafeSet(int i, int j, double value) {
		data[internalIndex(i,j)]=value;
	}
	
	@Override
	public AVector getColumn(int j) {
		return ArraySubVector.wrap(data, (j*(j+1))>>1, j+1).join(Vectorz.createZeroVector(cols-j-1));
	}
	
	@Override
	public LowerTriangularMatrix getTranspose() {
		return LowerTriangularMatrix.wrap(data, cols, rows);
	}

	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public boolean isMutable() {
		return true;
	}
	
	@Override
	public double determinant() { 
		if (rows!=cols) throw new UnsupportedOperationException(ErrorMessages.nonSquareMatrix(this));
		return this.diagonalProduct();
		
	}
	
	@Override
	public boolean equals(AMatrix a) {
		if (a==this) return true;	
		if (a instanceof ADenseArrayMatrix) {
			return equals((ADenseArrayMatrix)a);
		}

		if (!isSameShape(a)) return false;
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < i; j++) {
				if (a.unsafeGet(i, j)!=0.0) return false;
			}
			
			for (int j = i; j < cols; j++) {
				if (data[internalIndex(i, j)] != a.unsafeGet(i, j)) return false;
			}
		}
		return true;
	}

	@Override
	public AMatrix exactClone() {
		return new UpperTriangularMatrix(data.clone(),rows,cols);
	}

}
