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
	@Override
	public double get(int i);
	
	/**
	 * Set a specified element in a vector
	 */
	@Override
	public void set(int i, double value);

	/**
	 * Adds a vector element-wise to this vector, returning a new vector.
	 * @param a
	 * @return
	 */
	AVector addCopy(AVector a);

	/**
	 * Subtracts a vector element-wise from this vector, returning a new vector.
	 * @param a
	 * @return
	 */
	AVector subCopy(AVector a);

	/**
	 * Multiplies a vector element-wise with this vector, returning a new vector.
	 * @param a
	 * @return
	 */
	AVector multiplyCopy(AVector a);

	/**
	 * Divides this vector element-wise by another vector, returning a new vector.
	 * @param a
	 * @return
	 */
	AVector divideCopy(AVector a);

	/**
	 * Computes the square root for each element in this vector, returning a new vector.
	 * @param a
	 * @return
	 */
	AVector sqrtCopy();

	boolean epsilonEquals(AVector v, double tolerance);

	double normalise();

	/**
	 * Normalises this vector to unit length, returning a new vector.
	 * @return
	 */
	AVector normaliseCopy();
	
	@Override
	AVector clone();

	/**
	 * Returns true if this vector is exactly the same shape as another vector.
	 * @param a
	 * @return
	 */
	boolean isSameShape(AVector a);

	/**
	 * Shifts a vector by the specified number of elements, filling with zeros. Returns a new vector.
	 * @param shift
	 * @return
	 */
	AVector shiftCopy(int shift);

	/**
	 * Rotates a vector by the specified number of elements, wrapping around element values. Returns a view.
	 * @param shift
	 * @return
	 */
	AVector rotateView(int shift);

	/**
	 * Rotates a vector by the specified number of elements, wrapping around element values. Returns a new vector.
	 * @param shift
	 * @return
	 */
	AVector rotateCopy(int shift);
}
