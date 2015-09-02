package mikera.vectorz.impl;

/**
 * Abstract base class for boolean vectors 
 * 
 * Boolean vectors only support two element values, 0.0 and 1.0
 * 
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public abstract class ABooleanVector extends ASizedVector {

	protected ABooleanVector(int length) {
		super(length);
	}

	@Override
	public boolean isBoolean() {
		return true;
	}
	
	@Override
	public boolean isElementConstrained() {
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public boolean hasUncountable() {
		return false;
	}
	
	@Override
	public double elementSquaredSum() {
		// same as the element sum, since only values are 0 and 1
		return elementSum();
	}
	
	/**
     * Returns the sum of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementPowSum(double p) {
        return nonZeroCount();
    }
    
    /**
     * Returns the sum of the absolute values of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementAbsPowSum(double p) {
        return elementPowSum(p);
    }
}
