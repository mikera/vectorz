package mikera.vectorz.impl;

import mikera.vectorz.util.ErrorMessages;

/**
 * Base class for computed vectors. Assumed to be immutable and fixed size.
 * 
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public abstract class AComputedVector extends ASizedVector {

	protected AComputedVector(int length) {
		super(length);
	}

	@Override
	public abstract double get(int i);

	@Override
	public void set(int i, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}
	
	@Override
	public ImmutableScalar slice(int i) {
		return ImmutableScalar.create(get(i));
	}
	
	@Override
	public boolean isMutable() {
		return false; // i.e. immutable
	}
	
	@Override
	public AComputedVector exactClone() {
		return this;
	}
}
