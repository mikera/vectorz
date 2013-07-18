package mikera.vectorz.impl;

/**
 * Base class for computed vectors. Assumed to be immutable and fixed size.
 * 
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public abstract class ComputedVector extends AConstrainedVector {

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
	
	@Override
	public ComputedVector exactClone() {
		return this;
	}
}
