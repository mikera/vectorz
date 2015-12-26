package mikera.vectorz;

import mikera.vectorz.impl.ImmutableScalar;

/**
 * Interface for scalar array objects
 * 
 * @author Mike
 */
public interface IScalar {

	/**
	 * Returns the double value of a scalar array
	 * @return
	 */
	public double get();
	
	/**
	 * Sets the double value of a scalar array
	 */
	public void set(double value);
	
	/**
	 * Coerces this scalar to a mutable form
	 */
	public AScalar mutable();
	
	/**
	 * Coerces this scalar to an immutable form
	 */
	public ImmutableScalar immutable();
}
