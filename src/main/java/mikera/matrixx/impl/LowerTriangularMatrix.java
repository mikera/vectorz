package mikera.matrixx.impl;

import java.util.Arrays;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.impl.IndexedArrayVector;

/**
 * Class for a lower triangular matrix packed densely by rows.
 * 
 * Mutable only in the lower triangular elements
 * 
 * Mostly useful for space efficiency when storing triangular matrices, but is also optimised
 * for certain common operations on triangular matrices.
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
		LowerTriangularMatrix r = new LowerTriangularMatrix(rc,cc);
		for (int j=0; j<cc; j++) {
			for (int i=j; i<rc; i++) {
				r.unsafeSet(i, j, m.unsafeGet(i, j));
			}
		}
		return r;
	}
	
	@Override
	public boolean isLowerTriangular() {
		return true;
	}
	
	@Override
	public int upperBandwidthLimit() {
		return 0;
	}
	
	@Override
	public int upperBandwidth() {
		return 0;
	}
	
	@Override
	public AVector getBand(int band) {
		int n=bandLength(band);
		if ((n==0)||(band>0)) return Vectorz.createZeroVector(bandLength(band));

		int[] ixs=new int[n];
		for (int i=0; i<n; i++) {
			ixs[i]=internalIndex(i-band,i);
		}
		return IndexedArrayVector.wrap(data, ixs);
	}
	
	@Override
	protected int index(int i, int j) {
		if (i>=j) return internalIndex(i,j);
		throw new IndexOutOfBoundsException("Can't compute array index for sparse entry!");
	}
	
	private int internalIndex(int i, int j) {
		return j + ((i*(i+1))>>1);
	}

	@Override
	public double get(int i, int j) {
		checkIndex(i,j);
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
	public AVector getRowView(int i) {
		this.checkRow(i);
		int end=Math.min(i+1, cols);
		return ArraySubVector.wrap(data, internalIndex(i,0), end).join(Vectorz.createZeroVector(cols-end));
	}
	
	@Override
	public void copyRowTo(int i, double[] dest, int offset) {
		this.checkRow(i);
		int nn=Math.min(i+1,cols);
		System.arraycopy(data, internalIndex(i,0), dest, offset, nn);
		if (nn<cols) Arrays.fill(dest, offset+nn, offset+cols, 0.0);
	}
	
	@Override
	public UpperTriangularMatrix getTranspose() {
		return UpperTriangularMatrix.wrap(data, cols, rows);
	}

	@Override
	public AMatrix exactClone() {
		return new LowerTriangularMatrix(data.clone(),rows,cols);
	}
}
