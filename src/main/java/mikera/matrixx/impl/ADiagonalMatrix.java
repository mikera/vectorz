package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

/**
 * Abstract base class for diagonal matrices
 * @author Mike
 *
 */
public abstract class ADiagonalMatrix extends AMatrix {
	@Override
	public boolean isSquare() {
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
}
