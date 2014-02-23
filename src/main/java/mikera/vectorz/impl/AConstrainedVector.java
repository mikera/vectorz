package mikera.vectorz.impl;

import mikera.vectorz.AVector;

/**
 * Abstract base class for vectors that have constrained values.
 * 
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public abstract class AConstrainedVector extends AVector {

	@Override
	public boolean isElementConstrained() {
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}

}
