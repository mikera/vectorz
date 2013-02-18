package mikera.vectorz;

import mikera.arrayz.INDArray;
import mikera.vectorz.impl.ScalarVector;

/**
 * Class to represent a wrapped 0-d scalar value.
 * 
 * Can be a view into another vector/matrix/array
 * 
 * @author Mike
 */
public abstract class AScalar implements INDArray {
	
	private static final int[] SCALAR_SHAPE=new int[0];

	public abstract double get();
	
	public void set(double value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int dimensionality() {
		return 0;
	}
	
	@Override
	public INDArray slice(int position) {
		throw new UnsupportedOperationException("Can't slice a scalar!");
	}
	
	@Override
	public boolean isMutable() {
		// scalars are generally going to be mutable, so express this in default
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		return isMutable();
	}
	
	@Override 
	public double get(int... indexes) {
		assert(indexes.length==0);
		return get();
	}
	
	@Override
	public int[] getShape() {
		return SCALAR_SHAPE;
	}
	
	@Override
	public long elementCount() {
		return 1;
	}
	
	@Override
	public AVector asVector() {
		return new ScalarVector(this);
	}
	
	@Override
	public INDArray reshape(int... dimensions) {
		throw new UnsupportedOperationException();
	}
}
