package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

/**
 * A class representing a view of a matrix row as a vector
 * @author Mike
 */
@SuppressWarnings("serial") 
public class MatrixRowView extends AMatrixSubVector {
	private final AMatrix source;
	private final int row;

	public MatrixRowView(AMatrix aMatrix, int row) {
		source = aMatrix;
		this.row = row;
	}

	@Override
	public int length() {
		return source.columnCount();
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
}