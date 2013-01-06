package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * Matrix class that wraps a vector as a 1-row matrix
 * @author Mike
 */
public class RowMatrix extends AMatrix {
	private final AVector vector;
	
	public RowMatrix(AVector v) {
		vector=v;
	}
	
	public static RowMatrix wrap(AVector v) {
		return new RowMatrix(v);
	}

	@Override
	public int rowCount() {
		return 1;
	}
	
	@Override
	public AVector toVector() {
		return vector.clone();
	}
	
	@Override
	public AVector asVector() {
		return vector;
	}
	
	@Override
	public void scale(double factor) {
		vector.scale(factor);
	}

	@Override
	public int columnCount() {
		return vector.length();
	}

	@Override
	public double get(int row, int column) {
		assert(row==0);
		return vector.get(column);
	}

	@Override
	public void set(int row, int column, double value) {
		assert(row==0);
		vector.set(column,value);
	}
	
	@Override
	public ColumnMatrix getTranspose() {
		return new ColumnMatrix(vector);
	}


}
