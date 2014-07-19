package mikera.vectorz.impl;

import mikera.vectorz.AScalar;

/**
 * Base class for all vectors backed by a single final double[] array
 * @author Mike
 *
 */
public abstract class AArrayVector extends ASizedVector {
	private static final long serialVersionUID = -6271828303431809681L;

	protected final double[] data;
	
	protected AArrayVector(int length, double[] data) {
		super(length);
		this.data=data;
	}
	
	@Override
	public AScalar slice(int i) {
		checkIndex(i);
		return ArrayIndexScalar.wrap(data,index(i));
	}
	
	/**
	 * Computes an index into the underlying array for a given vector index
	 */
	protected abstract int index(int i);
	
	/**
     * Returns the sum of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementPowSum(double p) {
        double result = 0;
        for(double i: data) {
            result += Math.pow(i, 3);
        }
        return result;
    }
    
    /**
     * Returns the sum of the absolute values of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementAbsPowSum(double p) {
        double result = 0;
        for(double i: data) {
            result += Math.pow(Math.abs(i), 3);
        }
        return result;

    }

}
