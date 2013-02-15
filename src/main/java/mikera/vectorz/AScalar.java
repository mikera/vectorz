package mikera.vectorz;

import mikera.arrayz.INDArray;

/**
 * Class to represent a wrapped 0-d scalar value.
 * 
 * Can be a view into another vector/matrix/array
 * 
 * @author Mike
 */
public abstract class AScalar implements INDArray {

	public abstract double get();
	
	public void set(double value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int dimensionality() {
		return 0;
	}
	
	@Override 
	public double get(int... indexes) {
		assert(indexes.length==0);
		return get();
	}
	
	
	
	@Override
	public INDArray reshape(int... dimensions) {
		throw new UnsupportedOperationException();
	}
}
