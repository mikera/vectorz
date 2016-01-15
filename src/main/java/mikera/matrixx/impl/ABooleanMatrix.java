package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.util.VectorzException;

/**
 * Abstract base class for matrices limited to boolean 0/1 values
 * 
 * Such matrices allow for various efficient representations
 * 
 * @author Mike
 *
 */
public abstract class ABooleanMatrix extends ARectangularMatrix {
	private static final long serialVersionUID = 1599922421314660198L;

	protected ABooleanMatrix(int rows, int cols) {
		super(rows, cols);
	}
	
	@Override
	public boolean isBoolean() {
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public double elementMax() {
		if (elementCount()==0L) return -Double.MAX_VALUE;
		return isZero()?0.0:1.0;
	}
	
	@Override
	public double elementSum() {
		return nonZeroCount();
	}
	
	@Override
	public double elementSquaredSum() {
		return nonZeroCount();
	}
	
	
	@Override
	public AMatrix signumCopy() {
		return copy();
	}
	
	@Override
	public AMatrix squareCopy() {
		return copy();
	}
	
	@Override
	public AMatrix absCopy() {
		return copy();
	}
	
	@Override
	public void validate() {
		if (!clone().isBoolean()) {
			throw new VectorzException("Clone of boolean matrix should be boolean!");
		}
		super.validate();
	}

	@Override
	public boolean hasUncountable() {
		return false;
	}
	
	/**
     * Returns the sum of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementPowSum(double p) {
        return nonZeroCount();
    }
    
    /**
     * Returns the sum of the absolute values of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementAbsPowSum(double p) {
        return elementPowSum(p);
    }
}
