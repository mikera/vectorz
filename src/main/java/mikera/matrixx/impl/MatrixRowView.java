package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.AMatrixViewVector;

/**
 * A class representing a view of a matrix row as a vector
 * @author Mike
 */
@SuppressWarnings("serial") 
public final class MatrixRowView extends AMatrixViewVector {
	private final int row;

	public MatrixRowView(AMatrix aMatrix, int row) {
		super(aMatrix,aMatrix.columnCount());
		this.row = row;
	}

	@Override
	public double get(int i) {
		return source.get(row, i);
	}
		
	@Override
	public double unsafeGet(int i) {
		return source.unsafeGet(row, i);
	}

	@Override
	public void set(int i, double value) {
		source.set(row, i, value);
	}

	@Override
	public void unsafeSet(int i, double value) {
		source.unsafeSet(row, i, value);
	}
	
	@Override 
	public boolean isFullyMutable() {
		return source.isFullyMutable();
	}
	
	@Override
	public MatrixRowView exactClone() {
		return new MatrixRowView(source.exactClone(), row);
	}
	
	@Override public void getElements(double[] data, int offset) {
		source.copyRowTo(row,data,offset);
	}

	@Override
	protected int calcRow(int i) {
		return row;
	}

	@Override
	protected int calcCol(int i) {
		return i;
	}
	
	@Override
	public AVector clone() {
		return source.getRowClone(row);
	}
	
	@Override
	public boolean equals(AVector v) {
		if (v==this) return true;
		if (v.length()!=length) return false;
		for (int i=0; i<length; i++) {
			if (v.unsafeGet(i)!=unsafeGet(i)) return false;
		}
		return true;
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		double result=0.0;
		for (int i=0; i<length; i++) {
			result+=data[offset+i]*unsafeGet(i);
		}
		return result;
	}
}