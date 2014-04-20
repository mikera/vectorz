package mikera.matrixx.impl;

import mikera.vectorz.util.ErrorMessages;

public abstract class ATriangularMatrix extends AArrayMatrix {
	private static final long serialVersionUID = -5557895922040729998L;

	protected ATriangularMatrix(double[] data, int rows, int cols) {
		super(data, rows, cols);
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public boolean isMutable() {
		return true;
	}
	
	@Override
	public boolean isPackedArray() {
		return false;
	}
	
	@Override
	public double determinant() { 
		if (rows!=cols) throw new UnsupportedOperationException(ErrorMessages.nonSquareMatrix(this));
		return this.diagonalProduct();
	}

}
