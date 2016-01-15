package mikera.vectorz.impl;

/**
 * Abstract base class for vectors that wrap an underlying object
 * @author Mike
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public abstract class AWrappedVector<T> extends ASizedVector {

	protected AWrappedVector(int length) {
		super(length);
	}
}
