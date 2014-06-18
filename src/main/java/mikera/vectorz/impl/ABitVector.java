package mikera.vectorz.impl;

/**
 * Abstract base class for bit vectors 
 * 
 * Bit vectors only support two element values, 0.0 and 1.0
 * 
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public abstract class ABitVector extends ASizedVector {

	protected ABitVector(int length) {
		super(length);
	}

	@Override
	public boolean isBoolean() {
		return true;
	}
	
	@Override
	public boolean isElementConstrained() {
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public boolean hasUncountable() {
		return false;
	}
}
