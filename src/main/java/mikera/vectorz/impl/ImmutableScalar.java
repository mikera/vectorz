package mikera.vectorz.impl;

import mikera.vectorz.AScalar;
import mikera.vectorz.Scalar;
import mikera.vectorz.util.ErrorMessages;

public final class ImmutableScalar extends AScalar {
	private final double value;
	
	private ImmutableScalar(double value) {
		this.value=value;
	}
	
	public static ImmutableScalar create(double value) {
		return new ImmutableScalar(value);
	}

	public static ImmutableScalar create(AScalar a) {
		return create(a.get());
	}
	
	@Override
	public boolean isMutable() {
		return false;
	}
	
	@Override
	public boolean isFullyMutable() {
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
	public void getElements(double[] dest, int offset) {
		dest[offset] = value;
	}
	
	@Override
	public void set(double value) {
		throw (new UnsupportedOperationException(ErrorMessages.immutable(this)));
	}

	@Override
	public AScalar exactClone() {
		return new ImmutableScalar(value);
	}
	
	@Override
	public Scalar mutable() {
		return Scalar.create(value);
	}

	@Override
	public ImmutableScalar immutable() {
		return this;
	}
}
