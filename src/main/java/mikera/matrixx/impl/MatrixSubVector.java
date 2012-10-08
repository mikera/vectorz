package mikera.matrixx.impl;

import mikera.vectorz.AVector;

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
