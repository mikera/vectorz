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
	
	@Override
	public double elementMax() {
		if (elementCount()==0L) return -Double.MAX_VALUE;
		return isZero()?0.0:1.0;
	}
}
