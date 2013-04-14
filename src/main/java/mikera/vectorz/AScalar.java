package mikera.vectorz;

import mikera.arrayz.AbstractArray;
import mikera.arrayz.INDArray;
import mikera.randomz.Hash;
import mikera.vectorz.impl.ScalarVector;
import mikera.vectorz.util.VectorzException;

/**
 * Class to represent a wrapped 0-d scalar value.
 * 
 * Can be a view into another vector/matrix/array
 * 
 * @author Mike
 */
public abstract class AScalar extends AbstractArray {
	
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
	public boolean isElementConstrained() {
		return false;
	}
	
	public void add(AScalar s) {
		set(get()+s.get());
	}
	
	public void sub(AScalar s) {
		set(get()-s.get());
	}
	
	public INDArray innerProduct(INDArray a) {
		a=a.clone();
		a.scale(get());
		return a;
	}
	
	@Override 
	public double get(int... indexes) {
		assert(indexes.length==0);
		return get();
	}
	
	@Override 
	public void set(int[] indexes, double value) {
		if (indexes.length==0) {
			set(value);
		} else {
			throw new VectorzException(""+indexes.length+"D set not supported on AScalar");
		}
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
	
	@Override
	public void applyOp(IOp op) {
		set(op.apply(get()));
	}
	
	@Override
	public void applyOp(Op op) {
		set(op.apply(get()));
	}
	
	@Override
	public AScalar clone() {
		return (AScalar) super.clone();
	}
	
	@Override 
	public void scale(double factor) {
		set(factor*get());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof AScalar) {
			return equals((AScalar)o);
		} else if (o instanceof INDArray) {
			return equals((INDArray) o);
		}
		return false;
	}
	
	public boolean equals(INDArray o) {
		return (o.dimensionality()==0)&&(o.get(SCALAR_SHAPE)==get());
	}
	
	public boolean equals(AScalar o) {
		return get()==o.get();
	}

	@Override
	public int hashCode() {
		return 31+Hash.hashCode(get());
	}
	
	@Override
	public abstract AScalar exactClone();
}
