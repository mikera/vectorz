package mikera.vectorz;

import mikera.arrayz.INDArray;

/**
 * Basic interface for a Vector
 * 
 * Implementations should normally extend AVector directly, which implements IVector plus 
 * a considerable amount of other important functionality.
 * 
 * @author Mike
 */
public interface IVector extends INDArray {

	public int length();

	public double get(int i);
	
	public void set(int i, double value);
}
