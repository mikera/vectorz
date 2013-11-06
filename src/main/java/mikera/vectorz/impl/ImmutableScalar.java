package mikera.vectorz.impl;

import mikera.vectorz.AScalar;
import mikera.vectorz.util.ErrorMessages;

public class ImmutableScalar extends AScalar {
	private final double value;
	
	public ImmutableScalar(double value) {
		this.value=value;
	}
	
	@Override
	public boolean isMutable() {
		return false;
	}
	
	@Override
	public boolean isView() {
		return false;
	}

	@Override
	public double get() {
		return value;
	}
	
	@Override
	public void set(double value) {
		throw (new UnsupportedOperationException(ErrorMessages.immutable(this)));
	}

	@Override
	public AScalar exactClone() {
		return new ImmutableScalar(value);
	}

}
