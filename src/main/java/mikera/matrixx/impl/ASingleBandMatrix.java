package mikera.matrixx.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.impl.ZeroVector;

/**
 * Abstract base class to represent matrices with a single non-zero band
 * 
 * Unlike ADiagonalMatrix, this matrix need not be square, and may have non-zero values on
 * an arbitrary diagonal.
 * 
 * @author Mike
 *
 */
public abstract class ASingleBandMatrix extends ABandedMatrix {

	/**
	 * Override to specify which band of the matrix is nonzero
	 * @return
	 */
	public abstract int nonZeroBand();
	
	/**
	 * Override to specify which band of the matrix is nonzero
	 * @return
	 */
	public abstract AVector getNonZeroBand();
	
	@Override
	public boolean isSymmetric() {
		if (rowCount()!=columnCount()) return false;
		if ((nonZeroBand()==0)||getNonZeroBand().isZero()) return true;
		return false;
	}
	
	@Override
	public boolean isZero() {
		return getNonZeroBand().isZero();
	}
	
	@Override 
	public long nonZeroCount() {
		return getNonZeroBand().nonZeroCount();
	}
	
	@Override
	public AVector getBand(int band) {
		if (band==nonZeroBand()) {
			return getNonZeroBand();
		} else {
			return ZeroVector.create(bandLength(band));
		}
	}
	
	// TODO: inner product with single band matrix should be v.fast
}
