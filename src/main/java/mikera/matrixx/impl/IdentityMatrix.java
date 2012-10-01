package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

public class IdentityMatrix extends AMatrix {
	private final int dimensions;
	
	public IdentityMatrix(int dimensions) {
		this.dimensions=dimensions;
	}
	
	@Override
	public int rowCount() {
		return dimensions;
	}

	@Override
	public int columnCount() {
		return dimensions;
	}

	@Override
	public double get(int row, int column) {
		return (row==column)?1.0:0.0;
	}

	@Override
	public void set(int row, int column, double value) {
		throw new UnsupportedOperationException("Identity matrix is immutable!");
	}
	
	@Override 
	public void transform(AVector source, AVector dest) {
		dest.set(source);
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public boolean isSquare() {
		return true;
	}
	
	@Override
	public void transposeInPlace() {
		// already done!
	}
}
