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
	 * Applies the operator to a single value, returning the result
	 * @param x
	 * @return
	 */
	public double apply(double x);

	public void applyTo(AVector v);

	public void applyTo(AVector v, int start, int length);
	
	public void applyTo(double[] data, int start, int length);

	/**
	 * Converts an operator into a corresponding transform that applies the operator to all elements of its input
	 * @param dims
	 * @return
	 */
	public ATransform getTransform(int dims);

	public Op getInverse();
}
