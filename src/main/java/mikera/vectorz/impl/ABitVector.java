package mikera.vectorz.impl;

import mikera.vectorz.impl.AConstrainedVector;

/**
 * Abstract base class for bit vectors 
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public abstract class ABitVector extends AConstrainedVector {

	@Override
	public boolean isBoolean() {
		return true;
	}
}
