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
	 * @param x
	 * @return
	 */
	public double apply(double x);

	/**
	 * Applies this operator to a mutable vector.
	 * @param x
	 * @return
	 */
	public void applyTo(AVector v);

	/**
	 * Applies this operator to the specified range within a mutable vector.
	 * @param x
	 * @return
	 */
	public void applyTo(AVector v, int start, int length);
	
	/**
	 * Applies this operator to the specified range within a double[] array.
	 * @param x
	 * @return
	 */
	public void applyTo(double[] data, int start, int length);
	
	/**
	 * Applies this operator to the specified strided range within a double[] array.
	 * @param x
	 * @return
	 */
	void applyTo(double[] data, int start, int stride, int length);

	/**
	 * Converts an operator into a corresponding transform that applies the operator to all elements of its input
	 * @param dims
	 * @return
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
