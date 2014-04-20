package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Class for an lower triangular matrix packed densely by columns.
 * 
 * Mutable only in the lower triangular elements
 * 
 * Mostly useful for space efficiency when storing triangular matrices, but is also optimised
 * for certain common operations on triangular matrices.
 * 
 * May not be square, but must have columnCount() >= rowCount()
 * 
 * @author Mike
 *
 */
public final class LowerTriangularMatrix extends ATriangularMatrix implements IFastRows {
	private static final long serialVersionUID = 8413148328738646551L;

	private LowerTriangularMatrix(double[] data, int rows, int cols) {
		super(data, rows, cols);
	}
	
	private LowerTriangularMatrix(int rows, int cols) {
		this (new double[(rows*(rows+1))>>1],rows,cols);
	}
	
	static LowerTriangularMatrix wrap(double[] data, int rows, int cols) {
		return new LowerTriangularMatrix(data,rows,cols);
	}
	
	public static LowerTriangularMatrix createFrom(AMatrix m) {
		int rc=m.rowCount();
		int cc=m.columnCount();
		if (cc<rc) throw new IllegalArgumentException("Insufficient columns in source matrix");
		LowerTriangularMatrix r = new LowerTriangularMatrix(rc,cc);
		for (int i=0; i<rc; i++) {
			for (int j=0; j<=i; j++) {
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
		return j + ((i*(i+1))>>1);
	}
	
	@Override
	public double get(int i, int j) {
		if ((i<0)||(i>=rows)||(j<0)||(j>=cols)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i,j));
		if (j>i) return 0.0;
		return data[internalIndex(i,j)];
	}
	
	@Override
	public double unsafeGet(int i, int j) {
		if (j>i) return 0.0;
		return data[internalIndex(i,j)];
	}
	
	@Override
	public void unsafeSet(int i, int j, double value) {
		data[internalIndex(i,j)]=value;
	}
	
	@Override
	public AVector getRow(int i) {
		return ArraySubVector.wrap(data, (i*(i+1))>>1, i+1).join(Vectorz.createZeroVector(rows-i-1));
	}
	
	@Override
	public UpperTriangularMatrix getTranspose() {
		return UpperTriangularMatrix.wrap(data, cols, rows);
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
			for (int j = 0; j <= i; j++) {
				if (data[internalIndex(i, j)] != a.unsafeGet(i, j)) return false;
			}
			
			for (int j = i+1; j < cols; j++) {
				if (a.unsafeGet(i, j)!=0.0) return false;
			}
		}
		return true;
	}

	@Override
	public AMatrix exactClone() {
		return new LowerTriangularMatrix(data.clone(),rows,cols);
	}

}
