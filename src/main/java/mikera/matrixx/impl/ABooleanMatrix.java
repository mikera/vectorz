package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

public abstract class ABooleanMatrix extends AMatrix {
	
	@Override
	public boolean isBoolean() {
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}

}
