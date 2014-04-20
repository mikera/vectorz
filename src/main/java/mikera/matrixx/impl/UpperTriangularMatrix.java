package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.impl.ZeroVector;

/**
 * Class for an upper triangular matrix packed densely by columns.
 * 
 * Mostly useful for space efficiency when storing triangular matrices, but is also optimised
 * for certain common operations on triangular matrices.
 * 
 * May not be square, but must have columnCount() > rowCount()
 * 
 * @author Mike
 *
 */
public class UpperTriangularMatrix extends AArrayMatrix implements IFastColumns {
	private static final long serialVersionUID = 4438118586237354484L;

	private UpperTriangularMatrix(double[] data, int rows, int cols) {
		super(data, rows, cols);
	}
	
	private UpperTriangularMatrix(int rows, int cols) {
		this (new double[(rows*(rows+1))>>1],rows,cols);
	}
	
	public static UpperTriangularMatrix createFrom(AMatrix m) {
		int rc=m.rowCount();
		int cc=m.columnCount();
		if (cc<rc) throw new IllegalArgumentException("Insufficient columns in source matrix");
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
	public double unsafeGet(int i, int j) {
		return data[internalIndex(i,j)];
	}
	
	@Override
	public void unsafeSet(int i, int j, double value) {
		data[internalIndex(i,j)]=value;
	}
	
	@Override
	public AVector getColumn(int j) {
		return ArraySubVector.wrap(data, (j*(j+1))>>1, j+1).join(ZeroVector.create(cols-j-1));
	}

	@Override
	public boolean isFullyMutable() {
		return false;
	}

	@Override
	public AMatrix exactClone() {
		return new UpperTriangularMatrix(data.clone(),rows,cols);
	}

}
