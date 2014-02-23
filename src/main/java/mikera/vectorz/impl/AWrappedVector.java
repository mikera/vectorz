package mikera.vectorz.impl;

import mikera.vectorz.AVector;

/**
 * Abstract base class for vectors that wrap an underlying object
 * @author Mike
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public abstract class AWrappedVector<T> extends AVector {

	public abstract T getWrappedObject();
}
