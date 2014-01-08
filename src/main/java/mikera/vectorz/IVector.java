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

	/**
	 * Returns the length of a vector, in terms of the number of elements.
	 * 
	 * For Euclidean length, use magnitude() instead
	 * 
	 * @return
	 */
	public int length();

	public double get(int i);
	
	public void set(int i, double value);

	AVector immutable();

	AVector mutable();
}
