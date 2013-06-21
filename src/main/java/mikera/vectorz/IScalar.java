package mikera.vectorz;

import mikera.arrayz.INDArray;

/**
 * Interface for Scalar array objects
 * 
 * @author Mike
 */
public interface IScalar extends INDArray {

	public double get();
	
	public void set(double value);
}
