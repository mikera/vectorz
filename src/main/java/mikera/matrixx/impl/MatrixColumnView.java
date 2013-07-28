package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

@SuppressWarnings("serial")
public class MatrixColumnView extends AMatrixSubVector {
	/**
	 * 
	 */
	private final AMatrix source;
	private final int column;

	public MatrixColumnView(AMatrix aMatrix, int column) {
		source = aMatrix;
		this.column = column;
	}

	@Override
	public int length() {
		return source.rowCount();
	}

	@Override
	public double get(int i) {
		return source.get(i, column);
	}
	
	@Override 
	public boolean isFullyMutable() {
		return source.isFullyMutable();
	}

	@Override
	public void set(int i, double value) {
		source.set(i, column, value);
	}
	
	@Override
	public MatrixColumnView exactClone() {
		return new MatrixColumnView(source.exactClone(), column);
	}
	
	@Override public void getElements(double[] data, int offset) {
		source.copyColumnTo(column,data,offset);
	}
}