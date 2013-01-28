package mikera.matrixx.impl;

import mikera.vectorz.AVector;

/**
 * Abstract class for matrix sub vector views (rows and columns)
 * @author Mike
 */
@SuppressWarnings("serial")
public abstract class MatrixSubVector extends AVector {
	@Override
	public boolean isReference() {
		return true;
	}
	
	@Override
	public boolean isMutable() {
		return true;
	}
}
