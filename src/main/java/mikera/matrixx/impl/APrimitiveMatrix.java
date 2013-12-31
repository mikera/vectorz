package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

/**
 * Base class for the small square primitive backed matrices
 * @author Mike
 *
 */
public abstract class APrimitiveMatrix extends AMatrix {
	
	@Override
	public boolean isSquare() {
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		return true;
	}
}
