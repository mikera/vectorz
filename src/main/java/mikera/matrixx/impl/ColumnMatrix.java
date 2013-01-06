package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * Matrix class that wraps a vector as a 1-columns matrix
 * @author Mike
 */
public class ColumnMatrix extends AMatrix {
	private final AVector vector;
	
	public ColumnMatrix(AVector v) {
		vector=v;
	}
	
	public static ColumnMatrix wrap(AVector v) {
		return new ColumnMatrix(v);
	}


	@Override
	public int rowCount() {
		return vector.length();
	}

	@Override
	public int columnCount() {
		return 1;
	}
	
	@Override
	public void scale(double factor) {
		vector.scale(factor);
	}

	@Override
	public double get(int row, int column) {
		assert(column==0);
		return vector.get(row);
	}

	@Override
	public void set(int row, int column, double value) {
		assert(column==0);
		vector.set(row,value);
	}
	
	@Override
	public RowMatrix getTranspose() {
		return new RowMatrix(vector);
	}
	
	@Override
	public AVector toVector() {
		return vector.clone();
	}
	
	@Override
	public AVector asVector() {
		return vector;
	}


}
