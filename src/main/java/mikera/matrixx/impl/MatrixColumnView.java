package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.impl.AMatrixViewVector;

/**
 * A class representing a view of a matrix column as a vector
 * 
 * Used where no better implementations for getColumnView(...) on a matrix exist. 
 * 
 * @author Mike
 */
@SuppressWarnings("serial")
public final class MatrixColumnView extends AMatrixViewVector {
	private final int column;

	public MatrixColumnView(AMatrix aMatrix, int column) {
		super(aMatrix,aMatrix.rowCount());
		this.column = column;
	}

	@Override
	public double get(int i) {
		return source.get(i, column);
	}
	
	@Override
	public double unsafeGet(int i) {
		return source.unsafeGet(i, column);
	}
	
	@Override
	public void set(int i, double value) {
		source.set(i, column, value);
	}
	
	@Override
	public void unsafeSet(int i, double value) {
		source.unsafeSet(i, column, value);
	}
	
	@Override 
	public boolean isFullyMutable() {
		return source.isFullyMutable();
	}
	
	@Override
	public MatrixColumnView exactClone() {
		return new MatrixColumnView(source.exactClone(), column);
	}
	
	@Override public void getElements(double[] data, int offset) {
		source.copyColumnTo(column,data,offset);
	}

	@Override
	protected int calcRow(int i) {
		return i;
	}

	@Override
	protected int calcCol(int i) {
		return column;
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		double result=0.0;
		for (int i=0; i<length; i++) {
			result+=data[offset+i]*unsafeGet(i);
		}
		return result;
	}
	
	@Override
	public void applyOp(Op op) {
		for (int i=0; i<length; i++) {
			unsafeSet(i,op.apply(unsafeGet(i)));
		}
	}
	
	@Override
	public AVector clone() {
		return source.getColumnClone(column);
	}
}