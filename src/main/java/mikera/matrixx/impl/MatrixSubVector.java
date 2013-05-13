package mikera.matrixx.impl;

import mikera.vectorz.AVector;

/**
 * Abstract class for matrix sub vector views (rows and columns etc.)
 * @author Mike
 */
@SuppressWarnings("serial")
public abstract class MatrixSubVector extends AVector {
	@Override
	public boolean isView() {
		return true;
	}
}
