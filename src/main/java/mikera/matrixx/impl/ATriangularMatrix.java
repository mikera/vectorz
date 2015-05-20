package mikera.matrixx.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;

/**
 * Abstract base class for densely packed triangular matrices (upper and lower)
 * 
 * @author Mike
 */
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
		// not packed in the sense that it is not a fully dense packed array
		return false;
	}
	
	@Override
	public abstract AVector getBand(int i);
	
	@Override
	public double determinant() { 
		if (rows!=cols) throw new IllegalArgumentException(ErrorMessages.nonSquareMatrix(this));
		return this.diagonalProduct();
	}

	@Override
	public boolean isZero() {
		return DoubleArrays.isZero(data);
	}

}
