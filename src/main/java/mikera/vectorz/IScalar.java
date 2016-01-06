package mikera.vectorz;

import mikera.arrayz.INDArray;
import mikera.vectorz.impl.ImmutableScalar;

/**
 * Interface for scalar array objects
 * 
 * @author Mike
 */
public interface IScalar extends INDArray {

	/**
	 * Returns the double value of a scalar array
	 * @return
	 */
	@Override
	public double get();
	
	/**
	 * Sets the double value of a scalar array
	 */
	@Override
	public void set(double value);
	
	/**
	 * Coerces this scalar to a mutable form
	 */
	@Override
	public AScalar mutable();
	
	/**
	 * Coerces this scalar to an immutable form
	 */
	@Override
	public ImmutableScalar immutable();
}
