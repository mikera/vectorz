package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

public abstract class APrimitiveMatrix extends AMatrix {
	
	@Override
	public boolean isFullyMutable() {
		return true;
	}
}
