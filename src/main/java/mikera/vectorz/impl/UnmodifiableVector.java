package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Unmodifiable wrapper for arbitrary vectors
 * 
 * @author Mike
 *
 */
public class UnmodifiableVector extends BaseDerivedVector {
	private static final long serialVersionUID = 2709404707262677811L;

	private UnmodifiableVector(AVector source) {
		super(source);
	}
	
	public UnmodifiableVector wrap(AVector v) {
		return new UnmodifiableVector(v);
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
	
	@Override
	public AVector subVector(int offset, int length) {
		AVector ssv=source.subVector(offset, length);
		if (ssv==source) return this;
		return new UnmodifiableVector(ssv);
	}
	
	@Override
	public AVector sparse() {
		return SparseImmutableVector.create(source);
	}
}
