package mikera.vectorz.impl;

import mikera.vectorz.AVector;

/**
 * Abstract base class for vectors using a fixed final size
 * 
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public abstract class ASizedVector extends AVector {
	protected int length;
	
	@Override
	public final int length() {
		return length();
	}
}
