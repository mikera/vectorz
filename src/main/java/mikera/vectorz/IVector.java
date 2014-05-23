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

	/**
	 * Gets a specified element from a vector.
	 */
	public double get(int i);
	
	/**
	 * Set a specified element in a vector
	 */
	public void set(int i, double value);

	/**
	 * Convert a vector to an immutable format
	 */
	AVector immutable();

	/**
	 * Convert a vector to a mutable format
	 */
	AVector mutable();

	AVector addCopy(AVector a);

	AVector subCopy(AVector a);

	AVector multiplyCopy(AVector a);

	AVector divideCopy(AVector a);

	AVector sqrtCopy();

	boolean epsilonEquals(AVector v, double tolerance);

	double normalise();

	AVector normaliseCopy();
}
