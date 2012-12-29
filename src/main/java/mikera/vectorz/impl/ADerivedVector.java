package mikera.vectorz.impl;

import mikera.vectorz.AVector;

/**
 * Derived vector delegates all calls to an underlying vector
 * @author Mike
 */
public abstract class ADerivedVector extends AVector {
	private static final long serialVersionUID = -9039112666567131812L;

	protected AVector source;
	
	protected ADerivedVector(AVector source) {
		this.source=source;
	}
	
	@Override
	public int length() {
		return source.length();
	}

	@Override
	public double get(int i) {
		return source.get(i);
	}

	@Override
	public void set(int i, double value) {
		source.set(i,value);
	}

}
