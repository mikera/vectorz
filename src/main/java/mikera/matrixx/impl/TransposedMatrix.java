package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * Class representing a transposed view of another matrix
 * @author Mike
 *
 */
public class TransposedMatrix extends AMatrix {
	private final AMatrix source;
	
	private TransposedMatrix(AMatrix source) {
		this.source=source;
	}
	
	public static AMatrix wrap(AMatrix m) {
		if (m instanceof TransposedMatrix) return ((TransposedMatrix)m).source;
		return new TransposedMatrix(m);
	}

	public int rowCount() {
		return source.columnCount();
	}

	@Override
	public int columnCount() {
		return source.rowCount();
	}

	@Override
	public double get(int row, int column) {
		return source.get(column, row);
	}

	@Override
	public void set(int row, int column, double value) {
		source.set(column,row,value);
	}
	
	@Override
	public AVector getRow(int row) {
		return source.getColumn(row);
	}
	
	@Override
	public AVector getColumn(int column) {
		return source.getRow(column);
	}
}
