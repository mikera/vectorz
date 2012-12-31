package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

public class RowMatrix extends AMatrix {
	private final AVector vector;
	
	public RowMatrix(AVector v) {
		vector=v;
	}

	@Override
	public int rowCount() {
		return 1;
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

}
