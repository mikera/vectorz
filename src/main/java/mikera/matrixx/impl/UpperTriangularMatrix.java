package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.impl.IndexedArrayVector;

/**
 * Class for an upper triangular matrix packed densely by columns.
 * 
 * Mutable only in the upper triangular elements
 * 
 * Mostly useful for space efficiency when storing triangular matrices, but is also optimised
 * for certain common operations on triangular matrices.
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
		UpperTriangularMatrix r = new UpperTriangularMatrix(rc,cc);
		for (int i=0; i<rc; i++) {
			for (int j=i; j<cc; j++) {
				r.unsafeSet(i, j, m.unsafeGet(i, j));
			}
		}
		return r;
	}
	
	@Override
	public boolean isUpperTriangular() {
		return true;
	}
	
	@Override
	public int lowerBandwidthLimit() {
		return 0;
	}
	
	@Override
	public int lowerBandwidth() {
		return 0;
	}
	
	@Override
	public AVector getBand(int band) {
		int n=bandLength(band);
		if ((n==0)||(band<0)) return Vectorz.createZeroVector(bandLength(band));
		if (n==1) return ArraySubVector.wrap(data, internalIndex(0,band), 1);
		int[] ixs=new int[n];
		for (int i=0; i<n; i++) {
			ixs[i]=internalIndex(i,i+band);
		}
		return IndexedArrayVector.wrap(data, ixs);
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
		checkIndex(i,j);
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
	public AVector getColumnView(int j) {
		int end=Math.min(j+1, rows);
		return ArraySubVector.wrap(data, internalIndex(0,j), end).join(Vectorz.createZeroVector(rows-end));
	}
	
	@Override
	public LowerTriangularMatrix getTranspose() {
		return LowerTriangularMatrix.wrap(data, cols, rows);
	}
	
	@Override
	public boolean equals(AMatrix a) {
		if (a==this) return true;	
		if (!isSameShape(a)) return false;
		if (a instanceof ADenseArrayMatrix) {
			ADenseArrayMatrix da=(ADenseArrayMatrix)a;
			return equalsArray(da.getArray(),da.getArrayOffset());
		}
		
		for (int j = 0; j < cols; j++) {
			int end=Math.min(j,rows-1);
			for (int i = 0; i <= end; i++) {
				if (data[internalIndex(i, j)] != a.unsafeGet(i, j)) return false;
			}
			
			// TODO: factor out using isRangeZero on rows / cols of a?
			for (int i = j+1; i < rows; i++) {
				if (a.unsafeGet(i, j)!=0.0) return false;
			}
		}
		return true;
	}

	@Override
	public AMatrix exactClone() {
		return new UpperTriangularMatrix(data.clone(),rows,cols);
	}
}
