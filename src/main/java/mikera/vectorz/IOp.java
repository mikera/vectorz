package mikera.vectorz;

import mikera.transformz.impl.AOpTransform;


public interface IOp {
	/**
	 * Applies the operator to a single value, returning the result
	 * @param x
	 * @return
	 */
	public double apply(double x);

	public void applyTo(AVector v);

	
	public void applyTo(double[] data, int start, int length);

	/**
	 * Converts an operator into a corresponding transform that applies the operator to all elements of its input
	 * @param dims
	 * @return
	 */
	public AOpTransform getTransform(int dims);

}
