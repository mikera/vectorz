package mikera.matrixx.impl;

import mikera.vectorz.AVector;

/**
 * Abstract class for matrix sub vector views (rows and columns etc.)
 * 
 * @author Mike
 */
@SuppressWarnings("serial")
public abstract class AMatrixSubVector extends AVector {
	@Override
	public boolean isView() {
		return true;
	}
}
