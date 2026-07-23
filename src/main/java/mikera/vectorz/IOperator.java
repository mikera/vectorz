package mikera.vectorz;

import mikera.transformz.ATransform;

/**
 * Interface for a scalar operator that transforms a double value to another double value
 * 
 * @author Mike
 *
 */
public interface IOperator {
	/**
	 * Applies this operator to a single value, returning the result
	 * @param x The input value
	 * @return The result of applying this operator to x
	 */
	public double apply(double x);

	/**
	 * Applies this operator to a mutable vector.
	 * @param v The vector to modify in place
	 */
	public void applyTo(AVector v);

	/**
	 * Applies this operator to the specified range within a mutable vector.
	 * @param v The vector to modify in place
	 * @param start The index of the first element to modify
	 * @param length The number of elements to modify
	 */
	public void applyTo(AVector v, int start, int length);

	/**
	 * Applies this operator to the specified range within a double[] array.
	 * @param data The array to modify in place
	 * @param start The index of the first element to modify
	 * @param length The number of elements to modify
	 */
	public void applyTo(double[] data, int start, int length);

	/**
	 * Applies this operator to the specified strided range within a double[] array.
	 * @param data The array to modify in place
	 * @param start The index of the first element to modify
	 * @param stride The stride between successive elements
	 * @param length The number of elements to modify
	 */
	void applyTo(double[] data, int start, int stride, int length);

	/**
	 * Converts an operator into a corresponding transform that applies the operator to all elements of its input
	 * @param dims The number of dimensions of the transform's input
	 * @return A transform applying this operator element-wise
	 */
	public ATransform getTransform(int dims);

	/**
	 * Gets the inverse of this operator.
	 * 
	 * Returns null if no inverse exists.
	 * 
	 * @return
	 */
	public Op getInverse();

}
