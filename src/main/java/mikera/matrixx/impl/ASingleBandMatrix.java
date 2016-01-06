package mikera.matrixx.impl;

import mikera.arrayz.ISparse;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

/**
 * Abstract base class to represent sparse matrices with a single non-zero band
 * 
 * Unlike ADiagonalMatrix, this matrix need not be square, and may have non-zero values on
 * an arbitrary diagonal.
 * 
 * @author Mike
 *
 */
public abstract class ASingleBandMatrix extends ABandedMatrix implements ISparse {
	private static final long serialVersionUID = -213068993524224396L;

	/**
	 * Specifies which band of the matrix is nonzero
	 * @return
	 */
	public abstract int nonZeroBand();
	
	/**
	 * Gets the non-zero band from the matrix
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
	public AMatrix subMatrix(int rowStart, int rows, int colStart, int cols) {
		int nzb=nonZeroBand();
		int minBand=colStart-rowStart-rows+1;
		int maxBand=colStart-rowStart+cols-1;
		if ((rows==0)||(cols==0)||(nzb<minBand)||(nzb>maxBand)) {
			return ZeroMatrix.create(rows, cols);
		}
		int destBand=nzb+rowStart-colStart;
		int destBandLength=BandedMatrix.bandLength(rows, cols, destBand);
		int destBandStart=Math.max(rowStart-bandStartRow(nzb),colStart-bandStartColumn(nzb));
		AVector b=getNonZeroBand().subVector(destBandStart,destBandLength);
		return BandedMatrix.wrap(rows, cols, destBand, destBand, new AVector[] {b});
	}
	
	@Override
	public boolean isIdentity() {
		return isSquare()&&(nonZeroBand()==0)&&(getNonZeroBand().elementsEqual(1.0));
	}
	
	@Override
	public boolean isZero() {
		return getNonZeroBand().isZero();
	}
	
	@Override
	public final boolean isSparse() {
		return true;
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
			return Vectorz.createZeroVector(bandLength(band));
		}
	}

	@Override
	public boolean hasUncountable() {
		return getNonZeroBand().hasUncountable();
	}
	
	@Override
	public int rank() {
		return (int)getNonZeroBand().nonZeroCount();
	}
	
	/**
     * Returns the sum of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementPowSum(double p) {
        return getNonZeroBand().elementPowSum(p);
    }
    
    /**
     * Returns the sum of the absolute values of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementAbsPowSum(double p) {
        return getNonZeroBand().elementAbsPowSum(p);
    }
	
	// TODO: inner product with single band matrix should be v.fast
}
