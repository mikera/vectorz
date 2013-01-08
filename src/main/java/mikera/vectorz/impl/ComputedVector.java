package mikera.vectorz.impl;

import mikera.vectorz.AVector;

@SuppressWarnings("serial")
public abstract class ComputedVector extends AVector {

	@Override
	public abstract int length();

	@Override
	public abstract double get(int i);

	@Override
	public void set(int i, double value) {
		throw new UnsupportedOperationException("Vector is immutable: "+this.getClass());
	}
	
	@Override
	public boolean isMutable() {
		return false;
	}

}
