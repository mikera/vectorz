package mikera.vectorz;

import mikera.arrayz.INDArray;

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
	public double get();
	
	/**
	 * Sets the double value of a scalar array
	 */
	public void set(double value);
	
	
	public AScalar mutable();
	
	public AScalar immutable();
}
