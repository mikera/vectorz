package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

/**
 * Abstract base class for matrices limited to boolean 0/1 values
 * @author Mike
 *
 */
public abstract class ABooleanMatrix extends AMatrix {
	private static final long serialVersionUID = 1599922421314660198L;

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
}
