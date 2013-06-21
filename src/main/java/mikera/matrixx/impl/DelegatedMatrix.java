package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

/**
 * Abstract base class that delegates certain methods to a source matrix
 * 
 * @author Mike
 */
abstract class DelegatedMatrix extends AMatrix {

	protected final AMatrix source;
	
	protected DelegatedMatrix(AMatrix source) {
		this.source=source;
	}
	
	@Override
	public int rowCount() {
		return source.rowCount();
	}

	@Override
	public int columnCount() {
		return source.columnCount();
	}

	@Override
	public double get(int row, int column) {
		return source.get(row,column);
	}

	@Override
	public void set(int row, int column, double value) {
		source.set(row,column,value);
	}
	
	@Override
	public long elementCount() {
		return source.elementCount();
	}
	
	@Override
	public double elementSum() {
		return source.elementSum();
	}
	
	@Override
	public long nonZeroCount() {
		return source.nonZeroCount();
	}


}
