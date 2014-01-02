package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;

public class UnmodifiableVector extends BaseDerivedVector {
	private static final long serialVersionUID = 2709404707262677811L;

	public UnmodifiableVector(AVector source) {
		super(source);
	}

	@Override
	public void set(int i, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}
	
	@Override
	public void unsafeSet(int i, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
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
	public UnmodifiableVector exactClone() {
		return new UnmodifiableVector(source.exactClone());
	}
}
