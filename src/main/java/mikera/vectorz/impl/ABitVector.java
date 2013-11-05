package mikera.vectorz.impl;

import mikera.vectorz.impl.AConstrainedVector;

/**
 * Abstract base class for bit vectors 
 * 
 * Bit vectors only support two element values, 0.0 and 1.0
 * 
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
